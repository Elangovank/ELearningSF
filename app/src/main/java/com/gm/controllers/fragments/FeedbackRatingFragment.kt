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
import android.text.SpannableStringBuilder
import android.view.*
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.GMApplication
import com.gm.R
import com.gm.VideoCompressor.VideoCompress
import com.gm.WebServices.DataProvider
import com.gm.WebServices.URLUtils
import com.gm.controllers.activities.AudioCaptureActivity
import com.gm.controllers.activities.FullScreenVideoActivity
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.activities.TrimmerActivity
import com.gm.controllers.adapter.FeedBackListAdapter
import com.gm.controllers.adapter.FeedBackListAdapter.Companion.chickBirdRating
import com.gm.controllers.adapter.FeedBackListAdapter.Companion.feedRating
import com.gm.controllers.adapter.FeedBackListAdapter.Companion.medicineRating
import com.gm.controllers.adapter.FeedBackListAdapter.Companion.supportRating
import com.gm.controllers.adapter.FeedBackRatingHistoryAdapter
import com.gm.controllers.adapter.MediaTypeAdapter
import com.gm.controllers.adapter.SupportMediaTypeAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.*
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.gmcoreui.utils.Keys
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.alert_media_upload.*
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.fragment_feedback_rating.*
import kotlinx.android.synthetic.main.item_audio_player.view.*
import kotlinx.android.synthetic.main.item_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.item_media_list.*
import kotlinx.android.synthetic.main.media_view.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class FeedbackRatingFragment : GMBaseFragment(), OnItemClickListener {


    private var dialog: Dialog? = null
    private var mediaType = 1

    val MEDIA_GALLERY_REQUEST_CODE = 112
    private var deleteStatus: Boolean? = false
    private var deletefiles = ArrayList<Model.UploadMedia>()
    private val mediaListImage = ArrayList<Model.UploadMedia>()
    private val mediaListAudio = ArrayList<Model.UploadMedia>()
    private val mediaListVideo = ArrayList<Model.UploadMedia>()
    private val mediaList = ArrayList<Model.UploadMedia>()
    val mediaUploaded = ArrayList<Model.SupportTicketsResponse>()
    private var imageAdapter: MediaTypeAdapter? = null
    private var audioAdapter: MediaTypeAdapter? = null
    private var videoAdapter: MediaTypeAdapter? = null
    private var imageHistoryAdapter: SupportMediaTypeAdapter? = null
    private var audioHistoryAdapter: SupportMediaTypeAdapter? = null
    private var videoHistoryAdapter: SupportMediaTypeAdapter? = null

    var finalTime = 0
    private var audioPlayer: MediaPlayer? = null
    var playOrPauseAudioType = 0
    var timeElapsed: Int? = 0
    private var imageList = ArrayList<Model.SupportTicketsResponse>()
    private var videoList = ArrayList<Model.SupportTicketsResponse>()
    private var audioList = ArrayList<Model.SupportTicketsResponse>()
    private var questionList = ArrayList<Model.FeedBackQuestionList>()
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private var offlineFeedBack = Model.OfflineFeedBackMediaList()

    private var isInitialStartAndResume = true

    override fun onPermissionDenied(requestCode: Int) {

    }

    companion object {
        fun newInstance(args: Bundle): FeedbackRatingFragment {
            val fragment = FeedbackRatingFragment()
            fragment.arguments = args
            return fragment
        }
    }
    var saveDetails = Model.FeedBackSaveDetails()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feedback_rating, container, false)
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.UploadMedia) {
            if (item.status == 0) {
                deletefiles.remove(item)
            } else if (item.status == 1) {
                deletefiles.add(item)
            }
        } else if (item is Model.SupportTicketsResponse) {
            if (item.mediaType == GMKeys.AUDIO_ID) {

                showConfirmationDialog(Uri.parse(item.mediaLocation), item.mediaLocation!!, 1)
            } else if (item.mediaType == GMKeys.VIDEO_ID) {
                var intent = Intent(context, FullScreenVideoActivity::class.java)
                intent.putExtra("VideoURL", URLUtils.baseUrl + item.mediaLocation)
                intent.putExtra("ResumePosition", 0L)
                intent.putExtra("ResumeWindow", 0)
                startActivity(intent)
            } else {
                showConfirmationDialog(Uri.parse(item.mediaLocation), item.mediaLocation!!, 3)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveDetails = Model.FeedBackSaveDetails()
    }



    private fun setResourceString()
    {
        newFeedbackTextView?.text=getResourceString("new_feedback")
        historyTextView?.text=getResourceString("history")
        mediaTextView?.text=getResourceString("upload_media")
        addItemTextView?.text=getResourceString("add_item")
        deleteTextView?.text=getResourceString("delete")
        uploadMediaTextView?.text=getResourceString("add_item")
        noItemTextView?.text=getResourceString("no_media_found")
        videoLabelTextView?.text=getResourceString("video")
        photoLabelTextView?.text=getResourceString("photos")
        audioLabelTextView?.text=getResourceString("select_audio")
        noQuestionsFoundTextView?.text=getResourceString("no_questions_found")
        commentHintGMTextInputLayout?.hint=getResourceString("comment")
        uploadTextView1?.text=getResourceString("submit")


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
        playOrPauseAudioType = 0
        audioPlayer?.pause()
    }

    private fun forwardAudioPlayer() {
        val time = timeElapsed?.plus(GMKeys.forwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.plus(GMKeys.forwardTime)
                timeElapsed?.let { audioPlayer?.seekTo(it) }
            }
        }
    }

    private fun backwardAudioPlayer() {
        val time = timeElapsed?.minus(GMKeys.backwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.minus(GMKeys.backwardTime)
                timeElapsed?.let { audioPlayer?.seekTo(it) }
            }
        }
    }

    private var player: SimpleExoPlayer? = null
    private var playWhenReady: Boolean = false

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


    private fun showConfirmationDialog(uri: Uri, image: String, types: Int) {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_audio_player, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.currentPositiontTextView
        if (types == 1) {
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
                if (playOrPauseAudioType == 0) {
                    confirmationDialogView.media_play.setImageResource(android.R.drawable.ic_media_pause)
                    playOrPauseAudioType = 1
                    playAudioPlayer(confirmationDialog, confirmationDialogView)

                } else {
                    confirmationDialogView.media_play.setImageResource(android.R.drawable.ic_media_play)
                    playOrPauseAudioType = 0
                    pauseAudioPlayer(confirmationDialog, confirmationDialogView)
                }
            }
            playAudio(Uri.parse(URLUtils.baseUrl + uri.toString()), confirmationDialog, confirmationDialogView)
            confirmationDialog?.show()
            confirmationDialogView.cancel1.setOnClickListener {
                stopPlaying()
                confirmationDialog?.cancel()
            }
        } else {

            confirmationDialogView.imageLayout.visibility = View.VISIBLE
            confirmationDialogView.audioPlayerLayout.visibility = View.GONE
            releasePlayer(false)
            stopAudioPlayer()
            ImageUtils.loadImageToGlide(confirmationDialogView.displayImageView, image)
            confirmationDialogView.cancel2.setOnClickListener {
                confirmationDialog?.cancel()
            }
            confirmationDialog?.show()
        }

    }



    private fun showConfirmationDeleteDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_confirmation_dialog, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.confirmationMsgTextView.text=getResourceString("alter_message")
        confirmationDialogView.confirmTextView?.text=getResourceString("yes")
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

    override fun onDestroy() {
        super.onDestroy()
        saveDetails = Model.FeedBackSaveDetails()
    }

    private fun resetDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.delete_media)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.red_shade_1)!!)
        toggleClickableAction(true)
    }


    private fun initPhotoAdapter(imageFile: ArrayList<Model.SupportTicketsResponse>, mediaType: Int) {

        if (imageFile.size != 0)
            when (mediaType) {
                GMKeys.IMAGE_ID -> {
                    photoLinearLayout1.visibility = View.VISIBLE
                    imageHistoryAdapter = SupportMediaTypeAdapter(imageFile, context!!, this)
                    photosRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    photosRecyclerView1?.adapter = imageAdapter
                }
                GMKeys.VIDEO_ID -> {
                    videoLinearLayout1.visibility = View.VISIBLE
                    videoHistoryAdapter = SupportMediaTypeAdapter(imageFile, context!!, this)
                    videoRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    videoRecyclerView1?.adapter = videoAdapter
                }
                GMKeys.AUDIO_ID -> {
                    audioLinearLayout1.visibility = View.VISIBLE
                    audioHistoryAdapter = SupportMediaTypeAdapter(imageFile, context!!, this)
                    audioRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    audioRecyclerView1?.adapter = audioAdapter
                }
            }
    }

    private fun setDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete_selected)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.button_background)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.white)!!)
        toggleClickableAction(false)
    }

    private fun toggleClickableAction(status: Boolean) {
        addItemTextView?.isEnabled = status
        commentEditText?.isEnabled = status
        uploadTextView1?.isEnabled = status
        if (status) {
            uploadDisableLayout?.visibility = View.GONE
            addDisableLayout?.visibility = View.GONE
        } else {
            uploadDisableLayout?.visibility = View.VISIBLE
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
        uploadTextView1?.visibility = View.VISIBLE
        newUploadLayout?.visibility = View.VISIBLE
        uploadMediaTextView?.visibility = View.VISIBLE
    }

    override fun setResultImage(outputFileUri: Uri) {
        val data = Model.UploadMedia()
        data.mediaUri = outputFileUri
        data.mediaTypeId = MediaType.Image.toString()
        mediaListImage.add(data)
        initPhotoAdapter(mediaListImage, MediaType.Image)
        val media = Model.UploadMedia()
        media.MediaTypeId = GMKeys.IMAGE_ID
        media.mediaUri = outputFileUri
        media.file = File(outputFileUri.path)
        mediaList.add(media)
    }

    private fun initPhotoAdapter(imageFile: ArrayList<Model.UploadMedia>, mediaType: MediaType) {
        mediaTitleLayout?.visibility = View.VISIBLE
        emptyImage?.visibility = View.GONE
        uploadTextView1?.visibility = View.VISIBLE
        newUploadLayout?.visibility = View.GONE
        uploadMediaTextView?.visibility = View.VISIBLE
        addItemTextView?.visibility = View.VISIBLE
        deleteTextView?.visibility = View.VISIBLE
        if (imageFile.size != 0)
            mediaTitleLayout?.visibility = View.VISIBLE
        when(mediaType) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        historyLineView?.visibility = View.GONE
        recyclerView?.isNestedScrollingEnabled = false
        mediaLayoutView?.visibility = View.GONE
        cmdUploadLayout?.visibility = View.GONE
        getFeedbackQuestion()
        mediaListImage?.clear()
        mediaListVideo.clear()
        mediaListAudio.clear()
        initPhotoAdapter(imageList, GMKeys.IMAGE_ID)
        initPhotoAdapter(videoList, GMKeys.VIDEO_ID)
        initPhotoAdapter(audioList, GMKeys.AUDIO_ID)
        setListener()
        initToolbar(R.menu.menu_fliter, getResourceString("feedback"), true)
        val navMenu = toolbar.menu
        navMenu.findItem(R.id.menu_notification).isVisible = resources.getBoolean(R.bool.visible)
        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            getFeedBackHistory(true)
        }

    }


    fun setListener() {
        feedback.setOnClickListener {
            getFeedbackQuestion()
            newFeedbackTextView.setTextColor(resources.getColor(R.color.white))
            historyTextView.setTextColor(resources.getColor(R.color.unselected))
            if (NetworkAvailability.isNetworkAvailable(getContext()!!) && questionList.size==0) {
                cmdUploadLayout?.visibility = View.VISIBLE
            }
            feedbackRatingLayout?.visibility = View.VISIBLE
            reportHistoryView?.visibility = View.GONE
            historyLineView?.visibility = View.GONE
            newFeedbackLineView?.visibility = View.VISIBLE

        }
        history.setOnClickListener {
            moveToHistory()
        }


        uploadTextView1.setOnClickListener {
            saveFeedBack()
            if (saveDetails?.chick_Bird != 0 || saveDetails?.feed != 0 || saveDetails?.medicine != 0 || saveDetails?.supportCenter != 0) {
                showConfirmationDialog()
            } else {
                showSnackBar(getResourceString("message_submit_feedback"))
            }
        }
        uploadMediaTextView.setOnClickListener {
            showUploadTypeDialog()
        }
        addItemTextView.setOnClickListener {
            if (mediaList.size < 5)
                showUploadTypeDialog()
            else
                showSnackBar(getResourceString("message_upload_media"))
        }

        deleteTextView.setOnClickListener {
            if (deleteStatus == true) {
                if (deletefiles.size!=0) {
                    showConfirmationDeleteDialog()
                }else{
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

    fun deleteMedia()
    {
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

    fun initViewAdapter() {
        context?.let {
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter = FeedBackListAdapter(questionList,this)
        }
    }

    fun moveToHistory(isShowProgess: Boolean = true) {
        resetFormDatas()
        historyTextView.setTextColor(resources.getColor(R.color.white))
        newFeedbackTextView.setTextColor(resources.getColor(R.color.unselected))
        getFeedBackHistory(isShowProgess)
        feedbackRatingLayout?.visibility = View.GONE
        reportHistoryView?.visibility = View.VISIBLE
        newFeedbackLineView?.visibility = View.GONE
        historyLineView?.visibility = View.VISIBLE
    }


    fun resetFormDatas(){
        mediaListImage?.clear()
        mediaListVideo.clear()
        mediaListAudio.clear()
        mediaList.clear()
        mediaUploaded.clear()
        saveDetails = Model.FeedBackSaveDetails()
        chickBirdRating = 0
        feedRating = 0
        medicineRating = 0
        supportRating = 0
        commentEditText?.text=SpannableStringBuilder("")
    }

    fun resetAfterSubmitInOffline(){
        resetFormDatas()
        initPhotoAdapter(mediaListImage, MediaType.Image)
        initPhotoAdapter(mediaListVideo, MediaType.Video)
        initPhotoAdapter(mediaListAudio, MediaType.Audio)
        toggleVisibility()
        toggle()
        initViewAdapter()
        mediaLayoutView?.visibility = View.VISIBLE
        cmdUploadLayout?.visibility = View.VISIBLE
        scrollView?.fullScroll(View.FOCUS_UP)
    }

    private fun changeMediaUri(sourcePath: String?): String? {
        val destinationPath = activity?.externalCacheDir?.path + File.separator + "compressedVideo"
        val desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedFeedback" + System.currentTimeMillis() + ".mp4"
        }
        val VC = VideoCompressor(sourcePath, destinationPath + File.separator + desFileName)
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


    fun changeMediaUriFromGallery(sourcePath: String?,isTrimmedVideo:Boolean=false){
        val destinationPath =   activity?.externalCacheDir?.path+File.separator+"compressedVideo"
        val desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedFeedback" + System.currentTimeMillis() + ".mp4"
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
                    mediaList.add(Model.UploadMedia(mediaTypeId = GMKeys.VIDEO_ID.toString(), mediaUri = Uri.parse(destinationFilePath), MediaTypeId = GMKeys.VIDEO_ID, file = File(destinationFilePath)))
                    toggleVisibility()
                    toggle()
                    if (isTrimmedVideo){
                        val file=File(sourcePath)
                        if (file.exists()) file.delete()
                    }
                    dismissProgressDialog()
                }

                override fun onFail() {
                    if (isTrimmedVideo){
                        val file=File(sourcePath)
                        if (file.exists()) file.delete()
                    }
                    showSnackBar(getResourceString("error_not_supported_video"))
                    dismissProgressDialog()
                }

                override fun onProgress(percent: Float) {

                }
            })
        }catch (e:Exception){
            if (isTrimmedVideo){
                val file=File(sourcePath)
                if (file.exists()) file.delete()
            }
            showSnackBar(getResourceString("error_not_supported_video"))
            dismissProgressDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode==TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE){
                data?.getStringExtra(TrimmerActivity.EXTRA_OUTPUT_URI)?.let {path->
                    changeMediaUriFromGallery(path,isTrimmedVideo = true)
                }

            }else if (requestCode == GMKeys.VIDEO_CAPTURED) {
                val videoUri: Uri? = data?.data
                videoUri?.let {
                    val videoDetails = Model.UploadMedia()
                    val media = Model.UploadMedia()
                    media.MediaTypeId = GMKeys.VIDEO_ID
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
                                    mediaList.add(Model.UploadMedia(mediaTypeId = GMKeys.IMAGE_ID.toString(), mediaUri = uri, MediaTypeId = GMKeys.IMAGE_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggleVisibility()
                                    toggle()
                                } else if (mime.contains("video/")) {
                                    val filePath=RealPathUtil.getRealPathFromUri(context!!, uri)
                                    try {
                                        val  retriever = MediaMetadataRetriever()
                                        retriever.setDataSource(filePath)
                                        val timeInMinutes= retriever.extractMetadata(
                                                MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?.let { it1 -> TimeUnit.MILLISECONDS.toMinutes(it1) }
                                        if (timeInMinutes?.toInt()!! <=GMKeys.MEDIA_MINUTES){
                                            changeMediaUriFromGallery(filePath)
                                        }else{
                                            startVideoTrimmerActivity(uri)
                                        }
                                    }catch (e:Exception){
                                        showSnackBar(getResourceString("error_not_supported_video"))
                                    }
                                } else if (mime.contains("audio/")) {
                                    mediaListAudio.add(Model.UploadMedia(mediaUri = uri, mediaTypeId = MediaType.Audio.toString()))
                                    initPhotoAdapter(mediaListAudio, MediaType.Audio)
                                    //  mediaList.add(Model.UploadMedia(mediaTypeId = AUDIO_ID.toString(), file = File(uri.getPath())))
                                    mediaList.add(Model.UploadMedia(mediaTypeId = GMKeys.AUDIO_ID.toString(), mediaUri = uri, MediaTypeId = GMKeys.AUDIO_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggleVisibility()
                                    toggle()
                                } else {

                                }
                            }
                        }
                    }
                }
            }
        } else if (resultCode == GMKeys.AUDIO_CAPTURED) {

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
                    media.MediaTypeId = GMKeys.AUDIO_ID
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

    fun startVideoTrimmerActivity(uri:Uri){
        val intent=Intent(context, TrimmerActivity::class.java)
        intent.putExtra(TrimmerActivity.EXTRA_INPUT_URI,uri)
        intent.putExtra(TrimmerActivity.IS_VIDEO_URI,true)
        startActivityForResult(intent, TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE)
    }


    private fun uploadMedia(position: Int) {
        if (position < mediaList.size) {
            val mediaItem = mediaList[position]
            if (position == 0) {
                mediaUploaded.clear()
                showProgressBar()
            }
            mediaItem.let {
                val mediaTypeId = it.MediaTypeId
                offlineFeedBack.mediaType = it.MediaTypeId
                offlineFeedBack.mediaPath = it.file?.absolutePath
                DataProvider.uploadFeedBackMedia(offlineFeedBack, object : ServiceRequestListener {
                    override fun onRequestCompleted(responseObject: Any?) {
                        if (responseObject is Model.FeedbackResultResponse) {
                            val response = responseObject as Model.FeedbackResultResponse
                            response.let {
                                it.response?.let { it1 ->
                                    it1.mediaType = mediaTypeId
                                    mediaUploaded.add(it1)
                                }
                            }
                        }
                        uploadMedia(position + 1)
                    }

                    override fun onRequestFailed(responseObject: String) {
                        showSnackBar(getResourceString("error_upload"))
                        dismissProgressBar()
                    }
                })
            }
        } else {
            saveDetails.medias = ArrayList(mediaUploaded)
            saveFeedbackSaveMedia()

        }
    }

    fun getFeedBackHistory(isShowProgess: Boolean) {
        if (isShowProgess) showProgressBar()
        DataProvider.getFeedBackHistory(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                dismissProgressBar()
                swipeContainer?.isRefreshing = false
                if (responseObject is Model.FeedbackHistoryResponse) {
                    responseObject.response?.let { initViewAdapterHistory(it) }
                }

            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                swipeContainer?.isRefreshing = false
                dismissProgressBar()

            }


        })
    }


    fun initViewAdapterHistory(list: ArrayList<Model.FeedBackRatingHistory>) {
        context?.let {
            feedbackHistoryRecyclerView?.layoutManager = LinearLayoutManager(context)
            Collections.reverse(list)
            feedbackHistoryRecyclerView?.adapter = FeedBackRatingHistoryAdapter( list, this, context!!,this)
        }
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
        confirmationDialogView.cancelTextView.text=getResourceString("cancel")
        confirmationDialogView.confirmTextView.text=getResourceString("ok")
        confirmationDialogView.confirmationMsgTextView.text=getResourceString("confirm_submission")
        confirmationDialogView.confirmTextView.setOnClickListener {
            confirmationDialogView.confimationStartImageView?.visibility = View.GONE
            confirmationDialogView.confimationEndImageView?.visibility = View.VISIBLE
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    if (mediaList.size != 0) {
                        uploadMedia(0)
                    } else {
                        saveFeedbackSaveMedia(true)
                    }
                    confirmationDialog.dismiss()
                }
            }
            handler.postDelayed(runnable, 1000)
        }
        confirmationDialog?.show()
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                var bundle = Bundle()
                (activity as HomeActivity).replaceFragment(NotificationFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    private fun showUploadTypeDialog() {
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


    override fun onPermissionGranted(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_CAMERA) {
            checkAndGetPermission()
        } else if (requestCode == Keys.PERMISSION_REQUEST_MICRO_PHONE) {
            startAudioRecordActivity()
        } else if (requestCode == Keys.PERMISSION_REQUEST_STORAGE) {
            checkAndGetPermission()
        }
    }


    private fun startAudioRecordActivity() {
        val audioCaptureIntent = Intent(context, AudioCaptureActivity::class.java)
        startActivityForResult(audioCaptureIntent, GMKeys.AUDIO_CAPTURED)
    }

    private fun startVideoRecordActivity() {
        val captureVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, GMKeys.MEDIA_MINUTES*60)
        startActivityForResult(captureVideoIntent, GMKeys.VIDEO_CAPTURED)
    }


    private fun setGallerySelection(dialog: Dialog) {
        if (dialog.selectionLayout?.visibility == View.GONE) {
            dialog.selectionLayout?.visibility = View.VISIBLE
            dialog.galleryImageView?.setImageResource(R.drawable.ic_gallery_placeholder)
            dialog.galleryTitleTextView?.setTextColor(ContextCompat.getColor(context!!, R.color.activity_text_color))
            dialog.gallery_selector.setImageResource(R.drawable.ic_checked)

        } else {
            dialog.selectionLayout.visibility = View.GONE
            dialog.galleryImageView.setImageResource(R.drawable.ic_gallery_placeholder)
            dialog.galleryTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
            dialog.gallery_selector.setImageResource(R.drawable.ic_unchecked)
        }
        dialog.mediaCaptureLayout.visibility = View.GONE
        dialog.mediaImageView.setImageResource(R.drawable.ic_video)
        dialog.mediaTitleTextView?.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
        dialog.media_selector.setImageResource(R.drawable.ic_unchecked)
    }

    private fun setMediaCaptureSelection(dialog: Dialog) {
        if (dialog.mediaCaptureLayout?.visibility == View.GONE) {
            dialog.mediaCaptureLayout?.visibility = View.VISIBLE
            dialog.mediaImageView.setImageResource(R.drawable.ic_media)
            dialog.mediaTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.activity_text_color))
            dialog.media_selector.setImageResource(R.drawable.ic_checked)

        } else {
            dialog.mediaCaptureLayout?.visibility = View.GONE
            dialog.mediaImageView.setImageResource(R.drawable.ic_video)
            dialog.mediaTitleTextView?.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
            dialog.media_selector.setImageResource(R.drawable.ic_unchecked)
        }
        dialog.selectionLayout?.visibility = View.GONE
        dialog.galleryImageView.setImageResource(R.drawable.ic_gallery_placeholder)
        dialog.galleryTitleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.text_gray))
        dialog.gallery_selector.setImageResource(R.drawable.ic_unchecked)
    }

    private fun startGalleryIntent(type: MediaType) {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        when (type) {
            MediaType.Image -> intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            MediaType.Video -> {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                val videosMimeTypes = ArrayList<String>()
                val mimeTypeMap = MimeTypeMap.getSingleton()
                val mimeTypeFromExtension = mimeTypeMap.getMimeTypeFromExtension("mp4")
                if (mimeTypeFromExtension != null)
                    videosMimeTypes.add(mimeTypeFromExtension)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, videosMimeTypes.toTypedArray())
            }
            MediaType.Audio -> intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            MediaType.Pdf -> {
            }
        }
        startActivityForResult(Intent.createChooser(intent, getResourceString("select_files")), MEDIA_GALLERY_REQUEST_CODE)
    }

    private fun checkAndGetPermission() {
        if (mediaType == GMKeys.TAKE_PHOTO) {
            if (activity is HomeActivity) {
                if ((activity as HomeActivity).checkCameraPermission()) {
                    if ((activity as HomeActivity).checkStoragePermission()) {
                        takePhoto()
                    }
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

    fun getFeedbackQuestion() {
        showProgressBar()
        DataProvider.getFeedbackQuestion(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.FeedBackQuestionListResponse) {
                    questionList.clear()
                    responseObject.response.let {
                        it?.let { it1 -> questionList.addAll(it1) }
                    }
                    initViewAdapter()
                    dismissProgressBar()
                    toggleVisibility()
                    mediaLayoutView?.visibility = View.VISIBLE
                    cmdUploadLayout?.visibility = View.VISIBLE
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                dismissProgressBar()
            }


        })
    }


    fun saveFeedBack() {
        saveDetails.chick_Bird = chickBirdRating
        saveDetails.feed = feedRating
        saveDetails.medicine = medicineRating
        saveDetails.userId = GMApplication.loginUserId
        saveDetails.supportCenter = supportRating
        saveDetails.reason = commentEditText.text.toString()
        questionList.forEachIndexed { index, feedBackQuestionList ->
            if (feedBackQuestionList.feedBackCategoryId?.toInt() == 1) {
                 saveDetails.chick_Bird_Questions = ArrayList(feedBackQuestionList.stars?.filter { it.star==chickBirdRating }?.singleOrNull()?.questions?.mapNotNull { if (it.isSelected==true) it.questionId else null }?: arrayListOf())
            } else if(feedBackQuestionList.feedBackCategoryId?.toInt() == 2){
                saveDetails.feed_Questions = ArrayList(feedBackQuestionList.stars?.filter { it.star==feedRating }?.singleOrNull()?.questions?.mapNotNull { if (it.isSelected==true) it.questionId else null }?: arrayListOf())
            }else if(feedBackQuestionList.feedBackCategoryId?.toInt() == 3){
                saveDetails.medicine_Questions = ArrayList(feedBackQuestionList.stars?.filter { it.star==medicineRating }?.singleOrNull()?.questions?.mapNotNull { if (it.isSelected==true) it.questionId else null }?: arrayListOf())
            }else if(feedBackQuestionList.feedBackCategoryId?.toInt() == 4){
                saveDetails.supportCenter_Questions = ArrayList(feedBackQuestionList.stars?.filter { it.star==supportRating }?.singleOrNull()?.questions?.mapNotNull { if (it.isSelected==true) it.questionId else null }?: arrayListOf())
            }
        }
    }


    fun saveFeedbackSaveMedia(showProgress: Boolean = false) {
        if (showProgress) showProgressBar()
        saveDetails.let {
            it?.let { it1 ->
                DataProvider.getFeedbackSaveRating(it1, object : ServiceRequestListener {
                    override fun onRequestCompleted(responseObject: Any?) {
                        if (responseObject is Model.FeedBackResponse1) {
                            showSuccessDialog(getResourceString("feed_back_successfully"), DialogInterface.OnClickListener { _, _ ->
                                moveToHistory(false)
                            })
                        } else {
                            showSuccessDialog(responseObject.toString(), DialogInterface.OnClickListener { _, _ ->
                                resetAfterSubmitInOffline()
                            })
                            dismissProgressBar()
                        }
                    }

                    override fun onRequestFailed(responseObject: String) {
                        showErrorSnackBar(responseObject)
                        dismissProgressBar()
                    }

                }
                )
            }
        }
    }
}
