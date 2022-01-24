package com.gm.utilities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.URLUtils
import com.gm.models.MediaType
import java.io.File

object ImageUtils {
    fun decodeBitmap(outputFileUri: Uri?): Bitmap? {
        val mBitmap = MediaStore.Images.Media.getBitmap(GMApplication.appContext?.contentResolver, outputFileUri)
        return mBitmap
    }

    fun getImageUri(inImage: Bitmap?): Uri {
        val path = MediaStore.Images.Media.insertImage(GMApplication.appContext?.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String {
        val cursor = uri?.let { GMApplication.appContext?.contentResolver?.query(it, null, null, null, null) }
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val result = cursor?.getString(idx!!)!!
        cursor.close()
        return result
    }

    fun readAsImageFile(file: File): Bitmap? {
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    @SuppressLint("CheckResult")
    fun loadImageToGlide(imageView: ImageView, url:String?=null, uri:Uri?=null){
        val requestOptions = RequestOptions()
        requestOptions.error(R.drawable.placeholder_image)
        requestOptions.placeholder(R.drawable.placeholder_image)
        val request = RequestOptions()
        request.override(50)
        val request2 = RequestOptions()
        request2.override(10)


        url?.let {
            val imageUrl= URLUtils.baseUrl+url
            Glide.with(GMApplication.appContext!!)
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageUrl)
                    .thumbnail(Glide.with(GMApplication.appContext!!)
                            .setDefaultRequestOptions(request)
                            .load(imageUrl)
                            .thumbnail(Glide.with(GMApplication.appContext!!)
                                    .setDefaultRequestOptions(request2)
                                    .load(imageUrl))
                    )
                    .into(imageView)
        }
        uri?.let {
            Glide.with(GMApplication.appContext!!)
                    .setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .into(imageView)
        }
    }
    @SuppressLint("CheckResult")
    fun loadVideoToGlide(imageView: ImageView, url:String?=null,uri:Uri?=null){
        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.placeholder_video)
        requestOptions.error(R.drawable.placeholder_video)

        val request = RequestOptions()
        request.override(50)
        val request2 = RequestOptions()
        request2.override(10)

        url?.let {
            val imageUrl= URLUtils.baseUrl+url
            Glide.with(GMApplication.appContext!!)
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageUrl)
                    .thumbnail(Glide.with(GMApplication.appContext!!)
                            .setDefaultRequestOptions(request)
                            .load(imageUrl)
                            .thumbnail(Glide.with(GMApplication.appContext!!)
                                    .setDefaultRequestOptions(request2)
                                    .load(imageUrl))
                    )
                    .into(imageView)

        }
        uri?.let {
            Glide.with(GMApplication.appContext!!)
                    .setDefaultRequestOptions(requestOptions)
                    .load(uri)
                    .into(imageView)
        }
    }




    fun loadProgressGIF(imageView: ImageView,gifPath:Int){
        Glide.with(imageView.context)
                .load(gifPath)
                .into(imageView);
    }


}