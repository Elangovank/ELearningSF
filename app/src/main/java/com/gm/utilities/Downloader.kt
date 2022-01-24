package com.gm.utilities

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.gm.WebServices.DataProvider
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.models.ModuleType
import com.gmcoreui.utils.DateUtils
import java.io.File
import java.lang.Exception


class Downloader : DownloadListener {

    private var downloadManager: DownloadManager? = null
    private var receiver: DownloadBroadCastReceiver? = null
    private var downloadIdList = ArrayList<Long>()
    val handler = Handler()
    var context: Context? = null
    var offlineDownload = ArrayList<Model.Download>()
    var fileListener: FileDownloadedListener? = null
    var showProgress: Boolean = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        var downloader: Downloader? = null

        fun getInstance(listener: FileDownloadedListener?, context1: Context): Downloader {
            if (downloader != null) {
                downloader?.context = context1
                downloader?.fileListener = listener
                return downloader!!
            } else {
                downloader = Downloader(listener, context1)
                return downloader!!
            }
        }


        fun getInstance(context: Context): Downloader {
            if (downloader != null) {
                downloader?.context = context
                return downloader!!
            } else {
                downloader = Downloader(context)
                return downloader!!
            }
        }
    }


    fun setListener(listener: FileDownloadedListener?) {
        fileListener = listener
    }

    constructor(listener: FileDownloadedListener?, context1: Context) {
        fileListener = listener
        context = context1
        downloadManager = context1.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    }

    constructor(context1: Context) {
        downloadManager = context1.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        context = context1
    }


    fun download(url: String, title: String? = null, mediaType: MediaType, moduleType: ModuleType,showProgress:Boolean,isEncryptedMedia:Boolean=false) {
        if (downloadIdList.size == 0) {
            register(showProgress)
        }
        /*generate file name */
        val encryptedFileName = moduleType.toString()+"_"+ (title?:"")+"_" + System.currentTimeMillis() + if(mediaType==MediaType.Video)".mp4" else ".mp3"
        val request = DownloadManager.Request(Uri.parse(url))
        title?.let {
            request.setTitle(title)
        }
        request.setDestinationInExternalPublicDir(FileUtils.getFileStorageByMediaType(mediaType), encryptedFileName)
        val id = downloadManager?.enqueue(request)

        id?.let {
            downloadIdList.add(it)
            val filePath = FileUtils.getFullMediaPathByMediaType(mediaType) + File.separator + encryptedFileName
            val item = Model.Download(it, title,url, fileName = encryptedFileName,downloadDate =DateUtils.getTodayDate() , filePath = filePath, mediaType = mediaType, moduleType = moduleType,isDownloaded =  false,isEncryptedMedia=isEncryptedMedia)
            offlineDownload.add(item)
            insertDownloadItem(item)
        }

    }

    override fun downloadCompleted(downloadId: Long) {
            handler.removeCallbacks(runnable)
            downloadIdList.remove(downloadId)
            showProgress()
            getFileInfo(downloadId)
            if (downloadIdList.size == 0) {
                unregister()
            }
    }

    fun updateDownloader() {
        handler.removeCallbacks(runnable)
        offlineDownload.clear()
        downloadIdList.clear()
        DataProvider.threadBlock {
            val downloadList = DataProvider.application?.database?.getManageDownloadDao()?.getPendingDownloadList()
            downloadList?.let {
                downloadList.forEach {
                    doProcess(it)
                }
            }
            val completedList=DataProvider.application?.database?.getManageDownloadDao()?.getCompletedList()
            completedList?.forEach {
                if (!File(it.filePath?:"").exists()){
                    removeDownloadItem(it)
                }
            }
            if (downloadIdList.size == 0) {
                unregister()
            }
        }
    }


    fun doProcess(download: Model.Download) {
        download.let {
            download.downloadId?.let { dId ->
                val query = DownloadManager.Query()
                query.setFilterById(dId)
                val cursor = downloadManager?.query(query)
                var cursorBoolean = cursor?.moveToNext()
                if (cursorBoolean == false) {
                    removeDownloadItem(it)
                } else {
                    while (cursorBoolean == true) {
                        val status = cursor?.getInt(cursor?.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                ?: -1
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            it.isDownloaded = true
                            updateDownload(it)
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            removeDownloadItem(it)
                        } else {
                            downloadIdList.add(dId)
                            offlineDownload.add(it)

                        }
                        cursorBoolean = cursor?.moveToNext()
                    }
                }
            }
        }
    }

    fun removeDownloadItem(item: Model.Download) {
        DataProvider.threadBlock {
            DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(item))
        }
    }

    fun updateDownload(item: Model.Download) {
        DataProvider.threadBlock {
            DataProvider.application?.database?.getManageDownloadDao()?.update(item)
        }
    }

    fun insertDownloadItem(item: Model.Download) {
        DataProvider.threadBlock {
            DataProvider.application?.database?.getManageDownloadDao()?.insert(arrayListOf(item))
        }
    }


    fun getFileInfo(downloadId: Long) {
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager?.query(query)
        var cursorBoolean=cursor?.moveToNext()
        if (cursorBoolean==false){
            val item = offlineDownload.filter { it.downloadId != null && it.downloadId == downloadId }.single()
            offlineDownload.remove(item)
            removeDownloadItem(item)
            fileListener?.onDownloadCancel()
        }else {
            while (cursorBoolean == true) {
                cursor?.let {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))?:-1
                    Log.i(status.toString(), "status")
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        run breaker@{
                            offlineDownload.forEach {
                                if (it.downloadId == downloadId) {
                                    it.isDownloaded = true
                                    updateDownload(it)
                                    return@breaker
                                }
                            }
                        }
                        handler.removeCallbacks(runnable)
                        val item = offlineDownload.filter { it.downloadId != null && it.downloadId == downloadId }.single()
                        offlineDownload.remove(item)
                        val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
                        val uri = downloadManager?.getUriForDownloadedFile(id)
                        val mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                        fileListener?.fileDownloaded(uri!!, mimeType!!, downloadId)
                }
                }
                cursorBoolean= cursor?.moveToNext()
            }
        }
        cursor?.close()
    }


    fun register(isShowProgress: Boolean = true) {
        if (receiver == null) {
            receiver = DownloadBroadCastReceiver(this)
            (receiver as DownloadBroadCastReceiver).register(context)
        }
        showProgress = isShowProgress
        showProgress()
    }

    fun unregister() {
        try {
            if (receiver!=null){
                receiver?.unregister(context)
                receiver=null
            }
        }catch (e:Exception){}
        stopHandler()
    }



    fun showProgress() {
        if (showProgress)
        handler.postDelayed(runnable, 500)
    }




    var runnable = object : Runnable {
        override fun run() {
            downloadIdList.forEachIndexed { index, id ->
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val c = downloadManager?.query(query)
                if (c?.moveToFirst() == true) {
                    val sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val size = c.getInt(sizeIndex)
                    val downloaded = c.getInt(downloadedIndex)
                    var progress = 0.0
                    if (size != -1) progress = downloaded * 100.0 / size
                    //   Log.i("progress${index}:::size${size}::::", progress.toString())
                    fileListener?.onProgressChange(id, progress.toInt())
                }
            }
            if (downloadIdList.size != 0) {
                handler.postDelayed(this, 500)
            }
        }
    }



    fun stopHandler() {
        runnable?.let {
            showProgress = false
            handler?.removeCallbacks(runnable)
        }
    }

    fun cancelDownload(downloadId: Long) {
        if (downloadIdList.contains(downloadId)) {
            downloadManager?.remove(downloadId)
        }
    }


    fun cancelAll() {
        downloadIdList.forEach {dId->
            cancelDownload(dId)
            val item = offlineDownload.filter { it.downloadId != null && it.downloadId == dId }.single()
            offlineDownload.remove(item)
            removeDownloadItem(item)
        }
        if (downloadIdList.size!=0){
            fileListener?.onDownloadCancel()
        }
        downloadIdList.clear()
        unregister()
    }

}

class DownloadBroadCastReceiver(var listener: DownloadListener) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
            listener.downloadCompleted(intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1)
    }

    fun register(context: Context?) {
        val intentFilter=IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context?.registerReceiver(this, intentFilter)
    }

    fun unregister(context: Context?) {
        context?.unregisterReceiver(this)
    }
}

interface DownloadListener {
    fun downloadCompleted(downloadId: Long)
}

interface FileDownloadedListener {
    fun fileDownloaded(uri: Uri, mimeType: String, downloadId: Long)
    fun onProgressChange(downloadId: Long, progressPercent: Int)
    fun onDownloadCancel()
}


enum class DownloadEnum {
    NOT_DOWNLOADED, DOWNLOADED, DOWNLOADING, LOCAL_FILE
}
