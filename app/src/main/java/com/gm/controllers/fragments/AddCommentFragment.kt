package com.gm.controllers.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.GMApplication
import com.gm.R
import com.gm.VideoCompressor.VideoCompress
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.AudioCaptureActivity
import com.gm.controllers.activities.FullScreenVideoActivity
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.activities.TrimmerActivity
import com.gm.controllers.adapter.MediaTypeAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.ImageUtils
import com.gm.utilities.RealPathUtil
import com.gm.utilities.VideoCompressor
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.gmcoreui.utils.Keys
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.alert_media_upload.*
import kotlinx.android.synthetic.main.fragment_add_comment.*
import kotlinx.android.synthetic.main.item_audio_player.view.*
import kotlinx.android.synthetic.main.item_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.media_view.*
import java.io.File
import java.util.concurrent.TimeUnit


class AddCommentFragment : GMBaseFragment(), OnItemClickListener {
    private var VIDEO_CAPTURED = 1
    private var AUDIO_CAPTURED = 2
    private var mediaType = 1
    private val AUDIO_ID = 1
    private val VIDEO_ID = 2
    private val IMAGE_ID = 3
    var type = 0
    var timeElapsed: Int? = 0
    var forwardTime = 2000
    var backwardTime = 2000
    private var audioPlayer: MediaPlayer? = null
    private val mediaUploaded = ArrayList<Model.SupportTicketsMediaResponse>()
    private val mediaList = ArrayList<Model.UploadMedia>()
    private var imageAdapter: MediaTypeAdapter? = null
    private var audioAdapter: MediaTypeAdapter? = null
    private var videoAdapter: MediaTypeAdapter? = null

    val MEDIA_GALLERY_REQUEST_CODE = 112
    private var dialog: Dialog? = null
    var finalTime = 0
    private var isInitialStartAndResume = true
    private val mediaListImage = ArrayList<Model.UploadMedia>()
    private val mediaListAudio = ArrayList<Model.UploadMedia>()
    private val mediaListVideo = ArrayList<Model.UploadMedia>()

    private var deletefiles = ArrayList<Model.UploadMedia>()
    private var deleteStatus: Boolean? = false

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.UploadMedia) {
            if (item.status == 0) {
                deletefiles.remove(item)
            } else if (item.status == 1) {
                deletefiles.add(item)
            } else {
                if (item.mediaTypeId == MediaType.Video.toString()) {
                    var intent = Intent(context, FullScreenVideoActivity::class.java)
                    intent.putExtra("VideoURL", item.mediaUri.toString())
                    intent.putExtra("ResumePosition", 0L)
                    intent.putExtra("ResumeWindow", 0)
                    startActivity(intent)
                } else if (item.mediaTypeId == MediaType.Audio.toString()) {
                    var uri = item.mediaUri.toString()
                    showConfirmationDialog(Uri.parse(uri), uri, item.mediaTypeId!!)
                } else {
                    val uriImage = item.mediaUri.toString()

                    showConfirmationDialog(Uri.parse(uriImage), uriImage, item.mediaTypeId!!)
                }
            }
        }
    }


    override fun setResultImage(outputFileUri: Uri) {
        val data = Model.UploadMedia()
        data?.mediaUri = outputFileUri
        data?.mediaTypeId = MediaType.Image.toString()
        mediaListImage.add(data!!)
        initPhotoAdapter(mediaListImage, MediaType.Image)
        val media = Model.UploadMedia()
        media.MediaTypeId = IMAGE_ID
        media.mediaUri = outputFileUri
        media.file = File(outputFileUri.path)
        mediaList.add(media)
    }

    override fun onPermissionGranted(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_CAMERA) {
            checkAndGetPermission()
        } else if (requestCode == Keys.PERMISSION_REQUEST_MICRO_PHONE) {
            startAudioRecordActivity()
        } else if (requestCode == Keys.PERMISSION_REQUEST_STORAGE) {
            checkAndGetPermission()
        }
    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    companion object {
        const val ARG_SUPPORT_ID = "Suppor_id"


        fun newInstance(args: Bundle): AddCommentFragment {
            val fragment = AddCommentFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_comment, container, false)
    }

  fun setResourceString()
  {
      commentGMTextInputLayout?.hint=getResourceString("comment")
      mediaTextView?.text=getResourceString("upload_media")
      addItemTextView?.text=getResourceString("add_item")
      deleteTextView?.text=getResourceString("delete")
      uploadMediaTextView?.text=getResourceString("add_item")
      noItemTextView?.text=getResourceString("no_media_found")
      videoLabelTextView?.text=getResourceString("video")
      photoLabelTextView?.text=getResourceString("photos")
      audioLabelTextView?.text=getResourceString("select_audio")
      submitButton?.text=getResourceString("submit")
      lable_upload?.text=getResourceString("uplaod_content")

  }
    private fun showConfirmationDialog(uri: Uri, image: String, types: String) {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_audio_player, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.currentPositiontTextView
        if (types == "Audio") {
            confirmationDialogView?.imageLayout?.visibility = View.GONE
            confirmationDialogView?.audioPlayerLayout?.visibility = View.VISIBLE
            confirmationDialogView.media_pause.setOnClickListener {
                pauseAudioPlayer(confirmationDialog, confirmationDialogView)
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
                    pauseAudioPlayer(confirmationDialog, confirmationDialogView)
                }
            }
            playAudio(uri, confirmationDialog, confirmationDialogView)
            confirmationDialog?.show()
            confirmationDialogView.cancel1.setOnClickListener {
                stopPlaying()
                confirmationDialog?.cancel()
            }
        } else {
            confirmationDialog?.show()
            confirmationDialogView.imageLayout.visibility = View.VISIBLE
            confirmationDialogView.audioPlayerLayout.visibility = View.GONE
            releasePlayer(false)
            stopAudioPlayer()
            ImageUtils.loadImageToGlide(confirmationDialogView.displayImageView, uri = uri)
            confirmationDialogView.cancel2.setOnClickListener {
                confirmationDialog?.cancel()
            }

        }

    }


    private fun stopPlaying() {
        if (audioPlayer != null) {
            audioPlayer?.stop()
            audioPlayer?.release()
            audioPlayer = null
        }
    }

    private fun pauseAudioPlayer(dialog: AlertDialog, view: View) {
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

    private var player: SimpleExoPlayer? = null
    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0

    private fun releasePlayer(setData: Boolean = true) {
        isInitialStartAndResume = false
        if (player != null) {
            if (setData) {
                playbackPosition = player?.currentPosition!!
                currentWindow = player?.currentWindowIndex!!
            } else {
                playbackPosition = 0L
                currentWindow = 0
            }
            playWhenReady = player?.playWhenReady!!
            player?.stop()
            player?.release()
            player = null
        }
    }


    private fun stopAudioPlayer() {
        if (audioPlayer != null) {
            audioPlayer?.stop()
            audioPlayer?.release()
            audioPlayer = null
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
        releasePlayer(false)
        stopAudioPlayer()
        initializeSeekBar(dialog, view)
        view.media_play.setImageResource(android.R.drawable.ic_media_pause)
        audioPlayer = MediaPlayer()
        audioPlayer?.let {
            context?.let { it1 -> audioPlayer?.setDataSource(it1, uri) }
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        initToolbar(getResourceString("new_comment"))
        toggleVisibility()
        mediaTextView?.text =getResourceString("attachements")

        submitButton?.setOnClickListener {
            if (!commentEditText?.text.isNullOrEmpty())
                showConfirmationDialog()
            else
                showSnackBar(getResourceString("messege_support_submit"))
        }
        uploadMediaTextView?.setOnClickListener {
            showUploadTypeDialog()
        }
        addItemTextView?.setOnClickListener {
            if (mediaList.size < 5)
                showUploadTypeDialog()
            else
                showSnackBar(getResourceString("message_upload_media"))
        }

        deleteTextView?.setOnClickListener {
            if (deleteStatus == true) {
                if (deletefiles.size != 0) {
                    showConfirmationDeleteDialog()
                } else {
                    deleteMedia()
                }

            } else {
                setDeleteSelection()
                deleteStatus = true
                toggleStatus(2)
                imageAdapter?.notifyDataSetChanged()
                audioAdapter?.notifyDataSetChanged()
                videoAdapter?.notifyDataSetChanged()
            }

        }
    }

    private fun deleteMedia() {
        resetDeleteSelection()
        deleteStatus = false
        toggleStatus()
        deletefiles.forEach {
            val uri = it.mediaUri
            when {
                it.mediaTypeId == MediaType.Audio.toString() -> {
                    mediaListAudio.remove(it)
                }
                it.mediaTypeId == MediaType.Image.toString() -> {
                    mediaListImage.remove(it)
                }
                it.mediaTypeId == MediaType.Video.toString() -> {
                    mediaListVideo.remove(it)
                }
            }
            var count = 0
            run breaker@{
                mediaList.forEach {
                    if (it.mediaUri == uri) {
                        mediaList.removeAt(count)
                        return@breaker
                    }
                    ++count
                }
            }
        }
        deletefiles.clear()
        imageAdapter?.notifyDataSetChanged()
        audioAdapter?.notifyDataSetChanged()
        videoAdapter?.notifyDataSetChanged()
        toggle()
    }

    private fun showConfirmationDeleteDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_confirmation_dialog, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.confirmationMsgTextView.text =getResourceString("alter_message")
        confirmationDialogView.confirmTextView?.text = getResourceString("yes")
        confirmationDialogView.cancelTextView?.text=getResourceString("cancel")
        confirmationDialogView.cancelTextView.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialogView.confirmTextView.setOnClickListener {

            confirmationDialogView.confimationStartImageView.visibility = View.GONE
            confirmationDialogView.confimationEndImageView.visibility = View.VISIBLE
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    confirmationDialog.dismiss()
                }
            }
            handler.postDelayed(runnable, 1000)
            deleteMedia()
        }
        confirmationDialog?.show()
    }

    private fun toggleStatus(status: Int = -1) {
        mediaListImage.forEach {
            it.status = status
        }
        mediaListAudio.forEach {
            it.status = status
        }
        mediaListVideo.forEach {
            it.status = status
        }
    }

    private fun resetDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.delete_media)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.red_shade_1)!!)
        toggleClickableAction1(true)
    }

    private fun setDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete_selected)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.button_background)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.white)!!)
        toggleClickableAction1(false)
    }


    private fun toggleClickableAction1(status: Boolean) {
        addItemTextView?.isEnabled = status
        if (status) {
            addDisableLayout?.visibility = View.GONE
        } else {
            addDisableLayout?.visibility = View.VISIBLE
        }
    }

    private fun toggleVisibility() {
        mediaTitleLayout?.visibility = View.GONE
        mediaLayoutView?.visibility = View.VISIBLE
        emptyImage?.visibility = View.VISIBLE
        videoLinearLayout?.visibility = View.GONE
        photoLinearLayout?.visibility = View.GONE
        audioLinearLayout?.visibility = View.GONE
        newUploadLayout?.visibility = View.VISIBLE
        uploadMediaTextView?.visibility = View.VISIBLE
    }

    private fun toggle() {
        toggleLayoutVisibility(MediaType.Image, mediaListImage)
        toggleLayoutVisibility(MediaType.Video, mediaListVideo)
        toggleLayoutVisibility(MediaType.Audio, mediaListAudio)
        if (mediaListImage.size == 0 && mediaListAudio.size == 0 && mediaListVideo.size == 0) {
            uploadMediaTextView?.visibility = View.VISIBLE
            newUploadLayout?.visibility = View.VISIBLE
            emptyImage?.visibility = View.VISIBLE
            addItemTextView?.visibility = View.INVISIBLE
            deleteTextView?.visibility = View.INVISIBLE
        } else {
            mediaTitleLayout?.visibility = View.VISIBLE
            uploadMediaTextView?.visibility = View.VISIBLE
            emptyImage?.visibility = View.GONE
            newUploadLayout?.visibility = View.GONE
            addItemTextView?.visibility = View.VISIBLE
            deleteTextView?.visibility = View.VISIBLE
        }

    }

    private fun toggleLayoutVisibility(mediaType: MediaType, list: ArrayList<Model.UploadMedia>) {
        when (mediaType) {
            MediaType.Audio -> {
                if (list.size == 0) {
                    audioLinearLayout?.visibility = View.GONE
                } else {
                    audioLinearLayout?.visibility = View.VISIBLE
                }
            }
            MediaType.Video -> {
                if (list.size == 0) {
                    videoLinearLayout?.visibility = View.GONE
                } else {
                    videoLinearLayout?.visibility = View.VISIBLE
                }
            }
            MediaType.Image -> {
                if (list.size == 0) {
                    photoLinearLayout?.visibility = View.GONE
                } else {
                    photoLinearLayout?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startAudioRecordActivity() {
        val audioCaptureIntent = Intent(context, AudioCaptureActivity::class.java)
        startActivityForResult(audioCaptureIntent, AUDIO_CAPTURED)
    }

    private fun startVideoRecordActivity() {
        val captureVideoIntent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)
        captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, GMKeys.MEDIA_MINUTES * 60)
        startActivityForResult(captureVideoIntent, VIDEO_CAPTURED)
    }

    private fun initPhotoAdapter(imageFile: ArrayList<Model.UploadMedia>, mediaType: MediaType) {
        mediaTitleLayout?.visibility = View.VISIBLE
        emptyImage?.visibility = View.GONE
        newUploadLayout?.visibility = View.GONE
        uploadMediaTextView?.visibility = View.VISIBLE
        addItemTextView?.visibility = View.VISIBLE
        deleteTextView?.visibility = View.VISIBLE
        if (imageFile.size != 0)
            mediaTitleLayout?.visibility = View.VISIBLE
        when (mediaType) {
            MediaType.Image -> {
                photoLinearLayout?.visibility = View.VISIBLE
                imageAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType)
                photosRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                photosRecyclerView?.adapter = imageAdapter
            }
            MediaType.Video -> {
                videoLinearLayout?.visibility = View.VISIBLE
                videoAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType)
                videoRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                videoRecyclerView?.adapter = videoAdapter
            }
            MediaType.Audio -> {
                audioLinearLayout?.visibility = View.VISIBLE
                audioAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType)
                audioRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                audioRecyclerView?.adapter = audioAdapter
            }
        }
    }


    private fun changeMediaUri(sourcePath: String?): String? {
        val destinationPath = activity?.externalCacheDir?.path + File.separator + "compressedVideo"
        var desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedSupport" + System.currentTimeMillis() + ".mp4"
        }
        var VC = VideoCompressor(sourcePath, destinationPath + File.separator + desFileName)
        VC.compressLowQualityVideo(object : VideoCompress.CompressListener {
            override fun onStart() {
                showProgressDialog(getResourceString("compressing_message"))
            }

            override fun onSuccess() {
                dismissProgressDialog()
            }

            override fun onFail() {
                dismissProgressDialog()
            }

            override fun onProgress(percent: Float) {

            }
        })
        return destinationPath + File.separator + desFileName
    }


    fun changeMediaUriFromGallery(sourcePath: String?, isTrimmedVideo: Boolean = false) {
        val destinationPath = activity?.externalCacheDir?.path + File.separator + "compressedVideo"
        val desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedSupport" + System.currentTimeMillis() + ".mp4"
        }
        val destinationFilePath = destinationPath + File.separator + desFileName
        try {
            val VC = VideoCompressor(sourcePath, destinationFilePath)

            VC.compressLowQualityVideo(object : VideoCompress.CompressListener {
                override fun onStart() {
                    showProgressDialog(getResourceString("compressing_message"))
                }

                override fun onSuccess() {
                    mediaListVideo.add(Model.UploadMedia(mediaUri = Uri.parse(destinationFilePath), mediaTypeId = MediaType.Video.toString()))
                    initPhotoAdapter(mediaListVideo, MediaType.Video)
                    mediaList.add(Model.UploadMedia(mediaTypeId = VIDEO_ID.toString(), mediaUri = Uri.parse(destinationFilePath), MediaTypeId = VIDEO_ID, file = File(destinationFilePath)))
                    toggleVisibility()
                    toggle()
                    if (isTrimmedVideo) {
                        val file = File(sourcePath)
                        if (file.exists()) file.delete()
                    }
                    dismissProgressDialog()
                }

                override fun onFail() {
                    if (isTrimmedVideo) {
                        val file = File(sourcePath)
                        if (file.exists()) file.delete()
                    }
                    showSnackBar(getResourceString("error_not_supported_video"))
                    dismissProgressDialog()
                }

                override fun onProgress(percent: Float) {

                }
            })
        } catch (e: Exception) {
            if (isTrimmedVideo) {
                val file = File(sourcePath)
                if (file.exists()) file.delete()
            }
            showSnackBar(getResourceString("error_not_supported_video"))
            dismissProgressDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE) {
                data?.getStringExtra(TrimmerActivity.EXTRA_OUTPUT_URI)?.let { path ->
                    changeMediaUriFromGallery(path, isTrimmedVideo = true)
                }

            } else if (requestCode == 111) {
                playbackPosition = data?.getLongExtra("RESUME_POSITION", 0L)!!
                currentWindow = data.getIntExtra("CURRENT_INDEX", 0)
                isInitialStartAndResume = false
                //  player?.seekTo(currentWindow, playbackPosition)
                //  initializePlayer(requiredURI)
            } else if (requestCode == VIDEO_CAPTURED) {
                val videoUri: Uri? = data?.data
                videoUri?.let {
                    val videoDetails = Model.UploadMedia()
                    val media = Model.UploadMedia()
                    media.MediaTypeId = VIDEO_ID
                    media.file = File(changeMediaUri(RealPathUtil.getRealPathFromUri(context!!, it)))
                    var videoUri = Uri.parse(media.file!!.absolutePath)
                    media.mediaUri = videoUri
                    videoDetails.mediaUri = videoUri
                    videoDetails.mediaTypeId = MediaType.Video.toString()
                    mediaListVideo.add(videoDetails)
                    initPhotoAdapter(mediaListVideo, MediaType.Video)
                    mediaList.add(media)
                }
            } else if (requestCode == MEDIA_GALLERY_REQUEST_CODE) {
                val uri = data?.data
                activity?.let {
                    val contentResolver = it.contentResolver
                    contentResolver?.let {
                        uri?.let {
                            val mime = contentResolver.getType(uri)
                            mime?.let {
                                if (mime.contains("image/")) {
                                    mediaListImage.add(Model.UploadMedia(mediaUri = uri, mediaTypeId = MediaType.Image.toString()))
                                    initPhotoAdapter(mediaListImage, MediaType.Image)
                                    //   mediaList.add(Model.UploadMedia(mediaTypeId = IMAGE_ID.toString(), file = File(uri.getPath())))
                                    mediaList.add(Model.UploadMedia(mediaTypeId = IMAGE_ID.toString(), mediaUri = uri, MediaTypeId = IMAGE_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggleVisibility()
                                    toggle()
                                } else if (mime.contains("video/")) {

                                    val filePath = RealPathUtil.getRealPathFromUri(context!!, uri)
                                    try {
                                        val retriever = MediaMetadataRetriever()
                                        retriever.setDataSource(filePath)
                                        val timeInMinutes = retriever.extractMetadata(
                                                MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?.let { it1 -> TimeUnit.MILLISECONDS.toMinutes(it1) }
                                        if (timeInMinutes?.toInt()!! <= GMKeys.MEDIA_MINUTES) {
                                            changeMediaUriFromGallery(filePath)
                                        } else {
                                            startVideoTrimmerActivity(uri)
                                        }
                                    } catch (e: Exception) {
                                        showSnackBar(getResourceString("error_not_supported_video"))
                                    }

                                } else if (mime.contains("audio/")) {
                                    mediaListAudio.add(Model.UploadMedia(mediaUri = uri, mediaTypeId = MediaType.Audio.toString()))
                                    initPhotoAdapter(mediaListAudio, MediaType.Audio)
                                    //  mediaList.add(Model.UploadMedia(mediaTypeId = AUDIO_ID.toString(), file = File(uri.getPath())))
                                    mediaList.add(Model.UploadMedia(mediaTypeId = AUDIO_ID.toString(), mediaUri = uri, MediaTypeId = AUDIO_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggleVisibility()
                                    toggle()
                                }
                            }
                        }
                    }
                }
            }
        } else if (resultCode == AUDIO_CAPTURED) {

            val audioPath: String? = data?.getStringExtra("AUDIO")
            if (audioPath == null) {
                showSnackBar(getResourceString("record_audio_error"))
            } else {
                val audioUri: Uri? = Uri.parse(audioPath)
                audioUri?.let {
                    val audioDetails = Model.UploadMedia()
                    audioDetails.mediaUri = audioUri
                    audioDetails.mediaTypeId = MediaType.Audio.toString()
                    mediaListAudio.add(audioDetails)
                    initPhotoAdapter(mediaListAudio, MediaType.Audio)
                    val media = Model.UploadMedia()
                    media.MediaTypeId = AUDIO_ID
                    media.mediaUri = audioUri
                    media.file = File(audioUri.path)
                    mediaList.add(media)
                }
            }

        } else if (resultCode == 201) {
            mediaTitleLayout.visibility = View.VISIBLE
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startVideoTrimmerActivity(uri: Uri) {
        val intent = Intent(context, TrimmerActivity::class.java)
        intent.putExtra(TrimmerActivity.EXTRA_INPUT_URI, uri)
        intent.putExtra(TrimmerActivity.IS_VIDEO_URI, true)
        startActivityForResult(intent, TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE)
    }


    private fun showConfirmationDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_confirmation_dialog, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.cancelTextView.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialogView.confirmTextView.setOnClickListener {
            showProgressBar()
            confirmationDialogView.confimationStartImageView.visibility = View.GONE
            confirmationDialogView.confimationEndImageView.visibility = View.VISIBLE
            val handler = Handler()
            val runnable = Runnable {
                if (mediaList.size != 0) {
                    confirmationDialogView.confirmTextView.setEnabled(false)
                    uploadMedia(0)
                } else {
                    confirmationDialogView.confirmTextView.setEnabled(false)
                    updateSupportTicket(true)
                }

                confirmationDialog.dismiss()
            }
            handler.postDelayed(runnable, 1000)
        }
        confirmationDialog?.show()
    }


    private fun updateSupportTicket(showProgress: Boolean = false) {
        // if (showProgress) showProgressBar()
        val submitList = Model.SupportUpdate()
        submitList.userId = GMApplication.loginUserId
        submitList.comment = commentEditText?.text.toString() ?: ""
        submitList.supportTicketId = arguments?.getLong(ARG_SUPPORT_ID)
        submitList.medias = mediaUploaded
        submitList.let {
            DataProvider.updateSupportTicket(it, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    dismissProgressBar()
                    if (responseObject is Model.SupportCreateTicketResponse) {
                        // commentEditText.visibility = View.GONE
                        uploadMediaTextView?.visibility = View.GONE
                        context?.let {
                            showSnackBar(getResourceString("submittedSucessfully"))
                            (activity as HomeActivity)?.onBackPressed()
                        }

                    } else {
                        showSuccessDialog(responseObject.toString(), DialogInterface.OnClickListener { _, _ ->
                            (activity as HomeActivity).onBackPressed()
                        })
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)
                    dismissProgressBar()
                }
            })
        }
    }

    private fun showUploadTypeDialog() {
        context?.let {
            dialog = Dialog(context!!)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(true)
            dialog?.setContentView(R.layout.alert_media_upload)


            dialog?.mediaTitleTextView?.text=getResourceString("media_recorder")
            dialog?.galleryTitleTextView?.text=getResourceString("gallery")
            dialog?.uploadDialogTextView?.text=getResourceString("upload")
            dialog?.videoDialogTextView?.text=getResourceString("select_video")
            dialog?.audioDialogTextView?.text=getResourceString("select_audio")
            dialog?.imageDialogTextView?.text=getResourceString("select_image")
            dialog?.recordTextView?.text=getResourceString("record_video")
            dialog?.recordAudioTextView?.text=getResourceString("record_audio")
            dialog?.captureTextView?.text=getResourceString("capture_photo")
            dialog?.closeImage?.setOnClickListener {
                dialog?.dismiss()
            }
            dialog?.galleryLayout?.setOnClickListener {
                mediaType = GMKeys.GALLERY_INTENT
                checkAndGetPermission()
            }
            dialog?.mediaLayout?.setOnClickListener {
                setMediaCaptureSelection(dialog!!)
            }
            dialog?.media_photo?.setOnClickListener {
                mediaType = GMKeys.TAKE_PHOTO
                checkAndGetPermission()
                dialog?.dismiss()
            }
            dialog?.media_record_video?.setOnClickListener {
                mediaType = GMKeys.TAKE_VIDEO
                checkAndGetPermission()
                dialog?.dismiss()
            }
            dialog?.media_record_audio?.setOnClickListener {
                mediaType = GMKeys.RECORD_AUDIO
                checkAndGetPermission()
                dialog?.dismiss()
            }
            dialog?.select_video?.setOnClickListener {
                startGalleryIntent(MediaType.Video)
                dialog?.dismiss()
            }
            dialog?.select_audio?.setOnClickListener {
                startGalleryIntent(MediaType.Audio)
                dialog?.dismiss()
            }
            dialog?.select_photo?.setOnClickListener {
                startGalleryIntent(MediaType.Image)
                dialog?.dismiss()
            }
            dialog?.show()
        }
    }

    private fun setGallerySelection(dialog: Dialog) {
        if (dialog.selectionLayout.visibility == View.GONE) {
            dialog.selectionLayout.visibility = View.VISIBLE
            dialog.galleryImageView.setImageResource(R.drawable.ic_gallery_placeholder)
            dialog.galleryTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.activity_text_color))
            dialog.gallery_selector.setImageResource(R.drawable.ic_checked)

        } else {
            dialog.selectionLayout.visibility = View.GONE
            dialog.galleryImageView.setImageResource(R.drawable.ic_gallery_placeholder)
            dialog.galleryTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
            dialog.gallery_selector.setImageResource(R.drawable.ic_unchecked)
        }
        dialog.mediaCaptureLayout.visibility = View.GONE
        dialog.mediaImageView.setImageResource(R.drawable.ic_video)
        dialog.mediaTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
        dialog.media_selector.setImageResource(R.drawable.ic_unchecked)
    }

    private fun setMediaCaptureSelection(dialog: Dialog) {
        if (dialog.mediaCaptureLayout.visibility == View.GONE) {
            dialog.mediaCaptureLayout.visibility = View.VISIBLE
            dialog.mediaImageView.setImageResource(R.drawable.ic_media)
            dialog.mediaTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.activity_text_color))
            dialog.media_selector.setImageResource(R.drawable.ic_checked)

        } else {
            dialog.mediaCaptureLayout.visibility = View.GONE
            dialog.mediaImageView.setImageResource(R.drawable.ic_video)
            dialog.mediaTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
            dialog.media_selector.setImageResource(R.drawable.ic_unchecked)
        }
        dialog.selectionLayout.visibility = View.GONE
        dialog.galleryImageView.setImageResource(R.drawable.ic_gallery_placeholder)
        dialog.galleryTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
        dialog.gallery_selector.setImageResource(R.drawable.ic_unchecked)
    }

    private fun startGalleryIntent(type: MediaType) {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        when (type) {
            MediaType.Image -> intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            MediaType.Video -> intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            MediaType.Audio -> intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            MediaType.Pdf -> {
            }
        }
        startActivityForResult(Intent.createChooser(intent,getResourceString("select_files")), MEDIA_GALLERY_REQUEST_CODE)
    }

    private fun checkAndGetPermission() {
        if (mediaType == GMKeys.TAKE_PHOTO) {
            if (activity is HomeActivity) {
                if ((activity as HomeActivity).checkCameraPermission()) {
                    takePhoto()
                } else {
                    (activity as HomeActivity).requestCameraPermission()
                }
            }
        } else if (mediaType == GMKeys.TAKE_VIDEO) {
            if (activity is HomeActivity) {
                if ((activity as HomeActivity).checkCameraPermission()) {
                    if ((activity as HomeActivity).checkStoragePermission()) {
                        startVideoRecordActivity()
                    }
                } else {
                    (activity as HomeActivity).requestCameraPermission()
                }
            }
        } else if (mediaType == GMKeys.RECORD_AUDIO) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                if ((activity as HomeActivity).checkAudioRecodPermission()) {
                    startAudioRecordActivity()
                }
            }
        } else if (mediaType == GMKeys.GALLERY_INTENT) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                setGallerySelection(dialog!!)
            }
        }
    }

    private fun uploadMedia(position: Int) {
        if (position < mediaList.size) {
            val mediaItem = mediaList[position]
            if (position == 0) {
                //showProgressBar()
                mediaUploaded.clear()
            }
            mediaItem.let {

                val mediaTypeId = it.MediaTypeId

                val offlineSupport = Model.OfflineUpdateSupportMediaList().apply {
                    supportId = arguments?.getInt(ARG_SUPPORT_ID)
                    mediaType = mediaTypeId
                    mediaPath = it.file?.absolutePath
                }

                DataProvider.uploadCommentMedia(offlineSupport, object : ServiceRequestListener {
                    override fun onRequestCompleted(responseObject: Any?) {
                        if (responseObject is Model.SupportTicketResultResponse) {
                            responseObject.response?.let { it1 ->
                                it1.mediaType = mediaTypeId
                                mediaUploaded.add(it1)
                            }
                        }
                        uploadMedia(position + 1)
                    }

                    override fun onRequestFailed(responseObject: String) {
                        context?.let {
                            showSnackBar(getResourceString("error_upload"))
                            dismissProgressBar()
                        }

                    }
                })
            }
        } else {
            updateSupportTicket(false)
        }
    }


}
