package com.gm.controllers.fragments

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.FullScreenVideoActivity
import com.gm.controllers.adapter.DownloadAdapter
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.utilities.Downloader
import com.gm.utilities.FileDownloadedListener
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.item_audio_player.view.*
import kotlinx.android.synthetic.main.layout_no_record.*
import java.io.File

interface DownloadAdapterListener{
    fun onItemSelected(item:Any?,selectedIndex: Int,isCancel:Boolean)
}


class DownloadFragment : GMBaseFragment(), DownloadAdapterListener {

    var type = 0
    var timeElapsed: Int? = 0
    var forwardTime = 2000
    var backwardTime = 2000
    var finalTime = 0
    private var audioPlayer: MediaPlayer? = null

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int,isCancel: Boolean) {
        if (item is Model.DownloadQueue) {
            if (item.isDownloaded==false && isCancel) Downloader.getInstance(context!!).cancelDownload(item.downloadId!!)
            else if (item.mediaType==MediaType.Video){
                /*play video*/
                startFullScreen(item)
            }else if (item.mediaType==MediaType.Audio){
                /*play audio*/
                showAudioDialog( Uri.parse(if (item.isDownloaded==true) item.filePath?:"" else item.url?:""))
            }
        }
    }

    fun startFullScreen(item:Model.DownloadQueue){
        val intent = Intent(context, FullScreenVideoActivity::class.java)
        if (item.isEncryptedMedia==true && item.isDownloaded==false){
            intent.putExtra("VideoPath", item.url)
        }else if (item.isEncryptedMedia==true && item.isDownloaded==true){
            intent.putExtra("VideoPath", item.filePath)
        }else if (item.isEncryptedMedia==false && item.isDownloaded==false){
            intent.putExtra("VideoURL", Uri.parse(item.url).toString())
        }else if (item.isEncryptedMedia==false && item.isDownloaded==true){
            intent.putExtra("VideoURL", Uri.parse(item.filePath).toString())
        }
        intent.putExtra("ResumePosition", 0L)
        intent.putExtra("ResumeWindow", 0)
        startActivity(intent)
    }


    companion object {
        fun newInstance(args: Bundle): DownloadFragment {
            val fragment = DownloadFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView.visibility = View.GONE
        swipeContainer.isRefreshing = false
        swipeContainer.isEnabled = false
        initToolbar(getResourceString("label_downloads"))
        initAdapter()
    }

    val list = ArrayList<Model.DownloadQueue>()
    fun initAdapter() {
        recyclerViewRepository?.layoutManager = LinearLayoutManager(context)
        recyclerViewRepository?.adapter = DownloadAdapter(list, this)
        getDataFromDownloadsDB()
    }

    fun getDataFromDownloadsDB() {
        DataProvider.threadBlock {
            list.clear()
            val downloadUrlQueue = DataProvider.application?.database?.getManageDownloadDao()?.getPendingDownloadList()
            downloadUrlQueue?.let {
                it.forEach { item ->
                    if (File(item.filePath?:"").exists()){
                        list.add(Model.DownloadQueue(title = item.title.toString(),filePath = item.filePath,url = item.url,isDownloaded = item.isDownloaded,downloadDate = item.downloadDate,isEncryptedMedia = item.isEncryptedMedia, downloadId = item.downloadId, mediaType = item.mediaType, moduleType = item.moduleType))
                    }else{
                        DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(item))
                    }
                }
            }
            val completedDownloadQueue = DataProvider.application?.database?.getManageDownloadDao()?.getCompletedList()
            completedDownloadQueue?.let {
                it.forEach { item ->
                    if (File(item.filePath?:"").exists()) {
                        list.add(Model.DownloadQueue(title = item.title.toString(), filePath = item.filePath, url = item.url, isDownloaded = item.isDownloaded,downloadDate = item.downloadDate, isEncryptedMedia = item.isEncryptedMedia, downloadId = item.downloadId, mediaType = item.mediaType, moduleType = item.moduleType))
                    }else{
                        DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(item))
                    }
                }
            }
            activity?.runOnUiThread {
                if (list.size == 0) {
                    noDataLayout?.visibility = View.VISIBLE
                    recyclerViewRepository?.visibility = View.GONE
                } else {
                    noDataLayout?.visibility = View.GONE
                    recyclerViewRepository?.visibility = View.VISIBLE
                    recyclerViewRepository?.adapter?.notifyDataSetChanged()
                }
                getProgress(downloadUrlQueue)
            }
        }
    }


    fun getProgress(pendingDownloadQueue: List<Model.Download>?) {
        context?.let {
            if (!pendingDownloadQueue.isNullOrEmpty()){
                Downloader.getInstance(object : FileDownloadedListener {
                    override fun onDownloadCancel() {
                        getDataFromDownloadsDB()
                    }

                    override fun fileDownloaded(uri: Uri, mimeType: String, downloadId: Long) {
                        getDataFromDownloadsDB()
                    }

                    override fun onProgressChange(downloadId: Long, progressPercent: Int) {
                        if (list.isNotEmpty()) {
                            list.filter { it.downloadId == downloadId }.single().downloadProgress = progressPercent.toDouble()
                            recyclerViewRepository?.adapter?.notifyDataSetChanged()
                        }
                    }
                }, it).register()
            }
        }
    }


    override fun onDestroy() {
        Downloader.getInstance(context!!).stopHandler()
        super.onDestroy()
    }




    private fun showAudioDialog(uri: Uri) {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_audio_player, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
            confirmationDialogView?.imageLayout?.visibility = View.GONE
            confirmationDialogView?.audioPlayerLayout?.visibility = View.VISIBLE
            confirmationDialogView.media_pause.setOnClickListener {
                pauseAudioPlayer(confirmationDialogView)
            }

            confirmationDialogView.media_forward.setOnClickListener {
                forwardAudioPlayer()
            }

            confirmationDialogView.media_rewind.setOnClickListener {
                backwardAudioPlayer()
            }

            confirmationDialogView.media_play.setOnClickListener {
                if (type == 0) {
                    confirmationDialogView.media_play.setImageResource(android.R.drawable.ic_media_pause)
                    type = 1
                    playAudioPlayer(confirmationDialog, confirmationDialogView)

                } else {
                    confirmationDialogView.media_play.setImageResource(android.R.drawable.ic_media_play)
                    type = 0
                    pauseAudioPlayer(confirmationDialogView)
                }
            }
            playAudio(uri, confirmationDialog, confirmationDialogView)
            confirmationDialog?.show()
            confirmationDialogView.cancel1.setOnClickListener {
                stopAudioPlayer()
                confirmationDialog?.cancel()
            }
    }


    private fun playAudioPlayer(dialog: AlertDialog, view: View) {
        audioPlayer?.start()
        timeElapsed = audioPlayer?.currentPosition
        view.seekBar?.progress = timeElapsed as Int
        initializeSeekBar(dialog, view)
    }


    fun initializeSeekBar(dialog: AlertDialog, view: View) {
        dialog.setView(view)
        dialog.setCancelable(false)
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (audioPlayer != null) {
                    val totalDuration = audioPlayer?.duration
                    val currentDuration = audioPlayer?.currentPosition
                    view.totalDurationTextView?.text = DateUtils.milliSecondsToTimer(totalDuration
                            ?: 0)
                    view.currentPositiontTextView?.text = DateUtils.milliSecondsToTimer(currentDuration
                            ?: 0)
                    val mCurrentPosition = audioPlayer?.currentPosition
                    view.seekBar?.progress = mCurrentPosition ?: 0
                    initializeSeekBar(dialog, view)
                }
            }
        }
        handler.postDelayed(runnable, 10)
    }


    fun playAudio(uri: Uri, dialog: AlertDialog, view: View) {
        dialog.setView(view)
        dialog.setCancelable(false)
        stopAudioPlayer()
        initializeSeekBar(dialog, view)
        view.media_play.setImageResource(android.R.drawable.ic_media_pause)
        audioPlayer = MediaPlayer()
        audioPlayer?.let {
            context?.let {nonNullcontext->
                audioPlayer?.setDataSource(nonNullcontext, uri)
                audioPlayer?.setOnPreparedListener {
                    audioPlayer?.start()
                    dismissProgressBar()
                    view.seekBar.max = audioPlayer!!.duration
                    finalTime = audioPlayer!!.duration
                    initializeSeekBar(dialog, view)
                }
                audioPlayer?.prepareAsync()
            }
        }
    }


    private fun stopAudioPlayer() {
        if (audioPlayer != null) {
            audioPlayer?.stop()
            audioPlayer?.release()
            audioPlayer = null
        }
    }

    private fun pauseAudioPlayer( view: View) {
        view.media_play.setImageResource(android.R.drawable.ic_media_play)
        type = 0
        audioPlayer?.pause()
    }

    private fun forwardAudioPlayer() {
        val time = timeElapsed?.plus(forwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.plus(forwardTime)
                timeElapsed?.let { audioPlayer?.seekTo(it) }
            }
        }
    }

    private fun backwardAudioPlayer() {
        val time = timeElapsed?.minus(backwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.minus(backwardTime)
                timeElapsed?.let { audioPlayer?.seekTo(it) }
            }
        }
    }

}
