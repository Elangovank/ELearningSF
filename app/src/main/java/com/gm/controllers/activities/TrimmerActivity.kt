package com.gm.controllers.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.gm.R
import com.gm.utilities.GMKeys
import com.gm.utilities.RealPathUtil
import com.gmcoreui.controllers.BaseActivity
import com.lb.video_trimmer_library.interfaces.VideoTrimmingListener
import kotlinx.android.synthetic.main.activity_trimmer.*
import java.io.File

class TrimmerActivity : BaseActivity(), VideoTrimmingListener {
    var isVideo:Boolean=true
    companion object {
        val VIDEO_TRIMMER_REQUEST_CODE = 113
        val EXTRA_INPUT_URI="extra_input_uri"
        val EXTRA_OUTPUT_URI="extra_output_uri"
        val IS_VIDEO_URI="is_video_uri"
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trimmer)
        val inputVideoUri: Uri? = intent?.getParcelableExtra(EXTRA_INPUT_URI)
        isVideo = intent?.getBooleanExtra(IS_VIDEO_URI,false)?:false
        if (inputVideoUri == null) {
            finish()
            return
        }
    if (!isVideo){
          /*  val filePath=RealPathUtil.getRealPathFromUri(this,inputVideoUri)
            val dir=File(this.externalCacheDir?.path+File.separator+"audioToMp4")
            dir.mkdirs()
            val path=this.externalCacheDir?.path+File.separator+"audioToMp4"+File.separator+"convertedAudio${System.currentTimeMillis()}.mp4"
            File(filePath).copyTo(File(path))
            inputVideoUri=Uri.parse(path)*/
        }

        val timeInMillis=GMKeys.MEDIA_MINUTES * 60 * 1000
        videoTrimmerView.setMaxDurationInMs(timeInMillis)
        videoTrimmerView.setOnK4LVideoListener(this)
        val parentFolder = File(this.externalCacheDir?.path+File.separator+"trimmed")
        parentFolder.mkdirs()
        val fileName = "trimmedVideo_${System.currentTimeMillis()}" + if (isVideo) ".mp4" else ".mp3"
        val trimmedVideoFile = File(parentFolder, fileName)
        videoTrimmerView.setDestinationFile(trimmedVideoFile)
        videoTrimmerView.setVideoURI(inputVideoUri!!)
        videoTrimmerView.setVideoInformationVisibility(true)
        cancelTextView?.setOnClickListener {
        finish()
        }

        okTextView?.setOnClickListener {
            videoTrimmerView?.initiateTrimming()
        }
    }

    fun getResourceString()
    {
        cancelTextView?.text=getResourceString("cancel")
        okTextView?.text=getResourceString("ok")
    }

    override fun onTrimStarted() {
        showProgressDialog(getResourceString("trimming_message"))
}

    override fun onFinishedTrimming(uri: Uri?) {
        dismissProgressDialog()
        if (uri == null) {
            Toast.makeText(this@TrimmerActivity,getResourceString("error_trimming") , Toast.LENGTH_SHORT).show()
            finish()
        } else {
/*val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, if (isVideo) "video/mp4" else "audio/mp3")
            startActivity(intent)*/

            val intent=Intent()
            intent.putExtra(EXTRA_OUTPUT_URI,uri.path)
            setResult(-1,intent)
        }
        finish()
    }

    override fun onErrorWhileViewingVideo(what: Int, extra: Int) {
        dismissProgressDialog()
        Toast.makeText(this@TrimmerActivity, getResourceString("error_while_previewing_video"), Toast.LENGTH_SHORT).show()
    }

    override fun onVideoPrepared() {
        //        Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
    }
}
