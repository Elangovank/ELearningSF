package com.gm.utilities

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.gm.models.MediaType

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URISyntaxException
import java.util.Calendar
import java.util.Locale

object FileUtils {

    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context, uri: Uri): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun writeFile(context: Context) {
        try {
            val os = getLogStream(context)
            os.write(getInformation(context).toByteArray(charset("utf-8")))
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun writeFile(context: Context, str: String) {
        try {
            val os = getLogStream(context)
            os.write(str.toByteArray(charset("utf-8")))
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun getLogStream(context: Context): OutputStream {
        //crash_log_pkgname.log
        val model = Build.MODEL.replace(" ", "_")
        val fileName = String.format("compress_$model.log", context.packageName)
        val file = File(Environment.getExternalStorageDirectory(), fileName)

        if (!file.exists()) {
            file.createNewFile()
        }

        return FileOutputStream(file, true)
    }

    fun getInformation(context: Context): String {
        val current = System.currentTimeMillis()
        val sb = StringBuilder().append('\n')
        sb.append("BOARD: ").append(Build.BOARD).append('\n')
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n')
        sb.append("BRAND: ").append(Build.BRAND).append('\n')
        sb.append("CPU_ABI: ").append(Build.CPU_ABI).append('\n')
        sb.append("CPU_ABI2: ").append(Build.CPU_ABI2).append('\n')
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n')
        sb.append("DISPLAY: ").append(Build.DISPLAY).append('\n')
        sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append('\n')
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n')
        sb.append("HOST: ").append(Build.HOST).append('\n')
        sb.append("ID: ").append(Build.ID).append('\n')
        sb.append("MANUFACTURER: ").append(Build.MANUFACTURER).append('\n')
        sb.append("MODEL: ").append(Build.MODEL).append('\n')
        sb.append("PRODUCT: ").append(Build.PRODUCT).append('\n')
        sb.append("SERIAL: ").append(Build.SERIAL).append('\n')
        sb.append("TAGS: ").append(Build.TAGS).append('\n')
        sb.append("TIME: ").append(Build.TIME).append(' ').append(toDateString(Build.TIME)).append('\n')
        sb.append("TYPE: ").append(Build.TYPE).append('\n')
        sb.append("USER: ").append(Build.USER).append('\n')
        sb.append("VERSION.CODENAME: ").append(Build.VERSION.CODENAME).append('\n')
        sb.append("VERSION.INCREMENTAL: ").append(Build.VERSION.INCREMENTAL).append('\n')
        sb.append("VERSION.RELEASE: ").append(Build.VERSION.RELEASE).append('\n')
        sb.append("VERSION.SDK_INT: ").append(Build.VERSION.SDK_INT).append('\n')
        sb.append("LANG: ").append(context.resources.configuration.locale.language).append('\n')
        sb.append("APP.VERSION.NAME: ").append(getVersionName(context)).append('\n')
        sb.append("APP.VERSION.CODE: ").append(getVersionCode(context)).append('\n')
        sb.append("CURRENT: ").append(current).append(' ').append(toDateString(current)).append('\n')

        return sb.toString()
    }

    fun toDateString(timeMilli: Long): String {
        val calc = Calendar.getInstance()
        calc.timeInMillis = timeMilli
        return String.format(Locale.CHINESE, "%04d.%02d.%02d %02d:%02d:%02d:%03d",
                calc.get(Calendar.YEAR), calc.get(Calendar.MONTH) + 1, calc.get(Calendar.DAY_OF_MONTH),
                calc.get(Calendar.HOUR_OF_DAY), calc.get(Calendar.MINUTE), calc.get(Calendar.SECOND), calc.get(Calendar.MILLISECOND))
    }

    fun getVersionName(context: Context): String? {
        val packageManager = context.packageManager
        var packInfo: PackageInfo? = null
        var version: String? = null
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
            version = packInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

    fun getVersionCode(context: Context): Int {
        val packageManager = context.packageManager
        var packInfo: PackageInfo? = null
        var version = 0
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
            version = packInfo!!.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }



    fun getFileStorageByMediaType(mediaType: MediaType):String{
        return "/Suguna"+if (mediaType==MediaType.Audio)"/Audio" else "/Video"
    }

    fun getFullMediaPathByMediaType(mediaType: MediaType):String{
        return Environment.getExternalStorageDirectory().path+ getFileStorageByMediaType(mediaType)
    }

}
