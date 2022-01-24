package com.gm.controllers.fragments


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.MimeTypeMap
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
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
import com.gm.controllers.adapter.MediaTypeActivityAdapter
import com.gm.controllers.adapter.MediaTypeAdapter
import com.gm.controllers.adapter.SingleChoiceAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.OnItemSelectedListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.models.ModuleType
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.*
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.gmcoreui.utils.DateUtils.SERVER_DATE_FORMAT
import com.gmcoreui.utils.Keys
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import fr.maxcom.http.LocalSingleHttpServer
import kotlinx.android.synthetic.main.alert_media_upload.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.fragment_activity_view.*
import kotlinx.android.synthetic.main.item_basic_activity.*
import kotlinx.android.synthetic.main.item_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.item_media_list.*
import kotlinx.android.synthetic.main.media_view.*
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit


class ActivityViewFragment : GMBaseFragment(), OnItemClickListener, OnItemSelectedListener {
    lateinit var activityModel: Model.ActivityList

    val MEDIA_GALLERY_REQUEST_CODE = 112
    val FULL_SCREEN_REQUEST_CODE = 111

    //for play encrypted Video
    var mServer: LocalSingleHttpServer? = null

    val mediaListImage = ArrayList<Model.UploadMedia>()
    val mediaListAudio = ArrayList<Model.UploadMedia>()
    val mediaListVideo = ArrayList<Model.UploadMedia>()

    var dataList = ArrayList<Model.SaveActivityDetails>()
    val mediaList = ArrayList<Model.UploadMedia>()

    private var imageActivityAdapter: MediaTypeActivityAdapter? = null
    private var audioActivityAdapter: MediaTypeActivityAdapter? = null
    private var pdfActivityAdapter: MediaTypeActivityAdapter? = null
    private var videoActivityAdapter: MediaTypeActivityAdapter? = null

    var isCompleted = false
    var isFromNotification = false

    var answerStr: String? = null
    var timeElapsed: Int? = 0
    var dialogFragment1 = PdfViewerFragment()
    var finalTime = 0
    var selectedMedia: Model.UploadMedia? = null

    var playOrPauseAudioType = 0


    private var mediaType = GMKeys.TAKE_PHOTO

    private var audioPlayer: MediaPlayer? = null

    private var imageAdapter: MediaTypeAdapter? = null
    private var audioAdapter: MediaTypeAdapter? = null
    private var videoAdapter: MediaTypeAdapter? = null


    private var player: SimpleExoPlayer? = null
    private var shouldAutoPlay: Boolean = true
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
    private val singleChoiceList = ArrayList<Model.SingleChoice>()
    private var playWhenReady: Boolean = false
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private var dialog: Dialog? = null
    private var deletefiles = ArrayList<Model.UploadMedia>()
    private var deleteStatus: Boolean? = false
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var isInitialStartAndResume = true

    var currentItem = Model.UploadMedia()

    var handler = Handler()


    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.UploadMedia) {
            currentItem = item as Model.UploadMedia
            if ((item.status != 2) && (item.status != 1) && (item.status != 0)) {
                currentWindow = 0
                playbackPosition = 0L
                checkAndDisplayMedia(item)

            } else if (!isCompleted) {
                if (item.status == 0) {

                    deletefiles.remove(item)
                } else if (item.status == 1) {
                    deletefiles.add(item)
                }
            }
        } else if (item is Model.Media) {
            currentWindow = 0
            playbackPosition = 0L
            checkAndDisplayMedia(Model.UploadMedia(url = item.url, mediaUri = Uri.parse(URLUtils.baseUrl + item.url), mediaTypeId = item.mediaType.toString()))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity_view, container, false)
    }


    fun setResourceString() {
        dayTextView?.text = getResourceString("day_range")
        documentTextview?.text = getResourceString("document")
        subTitle?.text = getResourceString("activity_status")
        rangeText?.hint = getResourceString("message_enter")
        standardTextView?.text = getResourceString("standard")
        mediaTextView?.text = getResourceString("upload_media")
        addItemTextView?.text = getResourceString("add_item")
        deleteTextView?.text = getResourceString("delete")
        uploadMediaTextView?.text = getResourceString("add_item")
        noItemTextView?.text = getResourceString("no_media_found")
        videoLabelTextView?.text = getResourceString("video")
        photoLabelTextView?.text = getResourceString("photos")
        audioLabelTextView?.text = getResourceString("select_audio")
        videoTextView?.text = getResourceString("video")
        photoTextView?.text = getResourceString("photos")
        audioTextView?.text = getResourceString("select_audio")
        documentTextview?.text = getResourceString("document")
        commentGMTextInputLayout?.hint = getResourceString("comment")
        videoDownloadLabel?.text = getResourceString("downloading")
        offline?.text = getResourceString("lable_offline")
        subTitle1?.text = getResourceString("activity_status")
        answers?.text = getResourceString("answer")
        or?.text = getResourceString("or")
        alternate?.text = getResourceString("or")


    }


    override fun onItemSelected(item: Any?, selectedIndex: Int, previous: Int, textView: TextView) {
        if (item is Model.SingleChoice) {
            val selected = item.isSelected
            singleChoiceList.forEach { it.isSelected = false }
            selected?.let { singleChoiceList.get(selectedIndex).isSelected = !it }
            radioRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }


    fun afterSubmittedInOffline(offlineSubmittedActivity: Model.SaveActivityDetails) {
        DataProvider.threadBlock {
            val mediaList = DataProvider.application?.database?.uploadActivityMediaDao()?.getItemById(offlineSubmittedActivity.activityId?.toInt()
                    ?: 0)
            mediaList?.forEach {
                if (activityModel.activityId == it.activityId) {
                    if (GMKeys.IMAGE_ID == it.mediaTypeId) {
                        mediaListImage.add(Model.UploadMedia(url = it.url, mediaUri = Uri.parse(URLUtils.baseUrl + it.url), mediaTypeId = it.mediaType.toString()))
                    } else if (GMKeys.VIDEO_ID == it.mediaTypeId) {
                        mediaListVideo.add(Model.UploadMedia(url = it.url, mediaUri = Uri.parse(URLUtils.baseUrl + it.url), mediaTypeId = it.mediaType.toString()))
                    } else if (GMKeys.AUDIO_ID == it.mediaTypeId) {
                        mediaListAudio.add(Model.UploadMedia(url = it.url, mediaUri = Uri.parse(URLUtils.baseUrl + it.url), mediaTypeId = it.mediaType.toString()))
                    }
                }
            }

            activity?.runOnUiThread {
                initPhotoAdapter(mediaListVideo, MediaType.Video)
                initPhotoAdapter(mediaListImage, MediaType.Image)
                initPhotoAdapter(mediaListAudio, MediaType.Audio)
                offline.visibility = View.VISIBLE
                answers?.text = offlineSubmittedActivity.textAnswer
                setCompletedLayoutFromPending()
            }
        }
    }


    override fun onPermissionGranted(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_CAMERA) {
            checkAndGetPermission()
        } else if (requestCode == Keys.PERMISSION_REQUEST_MICRO_PHONE) {
            startAudioRecordActivity()
        } else if (requestCode == Keys.PERMISSION_REQUEST_STORAGE) {

            checkAndGetPermission()
        } else if (requestCode == Keys.PERMISSION_REQUEST_LOCATION) {
            setLocation()
        }
    }

    override fun onPermissionDenied(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_LOCATION) {
            setLatitudeLongitude()
        }
    }

    private fun startAudioRecordActivity() {
        val audioCaptureIntent = Intent(context, AudioCaptureActivity::class.java)
        startActivityForResult(audioCaptureIntent, GMKeys.AUDIO_CAPTURED)
    }

    private fun startVideoRecordActivity() {
        val captureVideoIntent = Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE)
        captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, GMKeys.MEDIA_MINUTES * 60)
        captureVideoIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        startActivityForResult(captureVideoIntent, GMKeys.VIDEO_CAPTURED)
    }

    private fun checkAndDisplayMedia(item: Model.UploadMedia) {
        imageFrameLayout.visibility = View.GONE
        selectedMedia = item
        when (item.mediaTypeId) {
            MediaType.Audio.toString() -> {
                if (item.url == null) {
                    scrollUp()
                    imageFrameLayout.visibility = View.VISIBLE
                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.LOCAL_FILE)
                    playAudio(item.mediaUri!!)
                } else {
                    DataProvider.threadBlock {
                        val downloadItem = DataProvider.application?.database?.getManageDownloadDao()?.getItemByUrl(URLUtils.baseUrl + item.url)
                        activity?.runOnUiThread {
                            if (downloadItem == null) {
                                if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.NOT_DOWNLOADED)
                                    playAudio(item.mediaUri!!)
                                } else {
                                    showSnackBar(getResourceString("error_network_connection"))
                                }
                            } else if (downloadItem.isDownloaded == true) {
                                if (File(downloadItem.filePath ?: "").exists()) {
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADED)
                                    playAudio(Uri.parse(downloadItem.filePath))
                                } else {
                                    DataProvider.threadBlock {
                                        DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(downloadItem))
                                    }
                                    if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                        scrollUp()
                                        imageFrameLayout.visibility = View.VISIBLE
                                        setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.NOT_DOWNLOADED)
                                        playAudio(item.mediaUri!!)
                                    } else {
                                        showSnackBar(getResourceString("error_network_connection"))
                                    }
                                }
                            } else {
                                if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADING)
                                    playAudio(item.mediaUri!!)
                                } else {
                                    showSnackBar(getResourceString("error_network_connection"))
                                }
                            }
                        }
                    }
                }
            }
            MediaType.Video.toString() -> {
                if (item.url == null) {
                    scrollUp()
                    imageFrameLayout.visibility = View.VISIBLE
                    setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.LOCAL_FILE)
                    playVideo(uri = item.mediaUri!!)
                } else {
                    DataProvider.threadBlock {
                        val downloadItem = DataProvider.application?.database?.getManageDownloadDao()?.getItemByUrl(URLUtils.baseUrl + item.url)
                        activity?.runOnUiThread {
                            if (downloadItem == null) {
                                setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.NOT_DOWNLOADED)
                                /*uploaded response video in completed is not encrypted*/
                                if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    if (!isEncryptedVideo())
                                        playVideo(uri = Uri.parse(URLUtils.baseUrl + item.url))
                                    else
                                        playVideo(path = URLUtils.baseUrl + item.url)
                                } else {
                                    showSnackBar(getResourceString("error_network_connection"))
                                }

                            } else if (downloadItem.isDownloaded == true) {
                                if (File(downloadItem.filePath ?: "").exists()) {
                                    setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADED)
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    /*uploaded response video in completed is not encrypted*/
                                    if (!isEncryptedVideo())
                                        playVideo(uri = Uri.parse(downloadItem.filePath))
                                    else
                                        playVideo(path = downloadItem.filePath)
                                } else {
                                    DataProvider.threadBlock {
                                        DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(downloadItem))
                                    }
                                    setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.NOT_DOWNLOADED)
                                    /*uploaded response video in completed is not encrypted*/
                                    if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                        scrollUp()
                                        imageFrameLayout.visibility = View.VISIBLE
                                        if (!isEncryptedVideo())
                                            playVideo(uri = Uri.parse(URLUtils.baseUrl + item.url))
                                        else
                                            playVideo(path = URLUtils.baseUrl + item.url)
                                    } else {
                                        showSnackBar(getResourceString("error_network_connection"))
                                    }
                                }
                            } else {
                                setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADING)
                                /*uploaded response video in completed is not encrypted*/
                                if (NetworkAvailability.isNetworkAvailable(context!!)) {
                                    scrollUp()
                                    imageFrameLayout.visibility = View.VISIBLE
                                    if (!isEncryptedVideo())
                                        playVideo(uri = Uri.parse(URLUtils.baseUrl + item.url))
                                    else
                                        playVideo(path = URLUtils.baseUrl + item.url)
                                } else {
                                    showSnackBar(getResourceString("error_network_connection"))
                                }

                            }
                        }
                    }
                }
            }
            MediaType.Image.toString() -> {
                scrollUp()
                imageFrameLayout.visibility = View.VISIBLE
                displayImage(item.mediaUri!!)
            }
            MediaType.Pdf.toString() -> {
                if (checkConnectionExsits()) {
                    scrollUp()
                    goToDialogFragment(item.mediaUri!!)
                } else {
                    showSnackBar(getResourceString("error_network_connection"))
                }

            }
        }
    }

    fun checkConnectionExsits(): Boolean {
        return NetworkAvailability.isNetworkAvailable(context!!)
    }

    fun displayImage(uri: Uri) {
        releasePlayer(false)
        stopAudioPlayer()
        playerView.visibility = View.GONE
        displayImageView.visibility = View.VISIBLE
        audioPlayerLayout.visibility = View.GONE
        ImageUtils.loadImageToGlide(displayImageView, uri = uri)
    }

    var uriForFullScreen: Uri? = null
    var pathForFullScreen: String? = null

    //path is for play encrypted online and offline and uri is play media without encryption
    fun playVideo(uri: Uri? = null, path: String? = null) {
        releasePlayer(false)
        stopAudioPlayer()
        playerView.visibility = View.VISIBLE
        displayImageView.visibility = View.GONE
        audioPlayerLayout.visibility = View.GONE
        uriForFullScreen = uri
        pathForFullScreen = path
        if (uri == null)
            initializePlayer(path = path)
        else
            initializePlayer(uri)
    }


    private fun goToDialogFragment(uri: Uri) {
        val bundle = Bundle()
        bundle.putString("PATH", uri.toString())
        dialogFragment1.arguments = bundle
        val ft = (activity as HomeActivity).supportFragmentManager.beginTransaction()
        val prev = (activity as HomeActivity).supportFragmentManager.findFragmentByTag("dialogh")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment1.show(ft, "dialogh")
    }


    fun playAudio(uri: Uri) {
        releasePlayer(false)
        stopAudioPlayer()
        playerView.visibility = View.GONE
        displayImageView.visibility = View.GONE
        initializeSeekBar()
        audioPlayerLayout.visibility = View.VISIBLE
        media_play.setImageResource(android.R.drawable.ic_media_pause)
        playOrPauseAudioType = 1
        audioPlayer = MediaPlayer()
        audioPlayer?.let {
            context?.let { it1 -> it.setDataSource(it1, uri) }
            it.setOnPreparedListener {
                audioPlayer?.start()
                dismissProgressBar()
                seekBar.max = audioPlayer!!.duration
                finalTime = audioPlayer!!.duration
                initializeSeekBar()
            }
            it.prepareAsync()
        }
    }


    companion object {
        fun newInstance(args: Bundle): ActivityViewFragment {
            val fragment = ActivityViewFragment()
            fragment.arguments = args
            return fragment
        }

        var data = ArrayList<Model.SelectedItem>()
        private const val KEY_PLAY_WHEN_READY = "play_when_ready"
        private const val KEY_WINDOW = "window"
        private const val KEY_POSITION = "position"
    }

    //path is for play encrypted online and offline and uri is play media without encryption
    private fun initializePlayer(uri: Uri? = null, path: String? = null) {
        if (player == null) {
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            lastSeenTrackGroupArray = null
            context?.let {
                player = ExoPlayerFactory.newSimpleInstance(context!!, trackSelector)
                playerView.player = player
                player?.playWhenReady = playWhenReady
                player?.let {
                    with(player!!) {
                        addListener(PlayerEventListener())
                        playWhenReady = shouldAutoPlay
                    }
                }
            }
        }

        val mediaSource = if (uri == null) buildMediaSource(path = path) else buildMediaSource(uri)
        val haveStartPosition = currentWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(currentWindow, playbackPosition)
        }
        player?.prepare(mediaSource, !haveStartPosition, false)

        updateButtonVisibilities()

    }

    //path is for play encrypted online and offline and uri is play media without encryption
    private fun buildMediaSource(uri: Uri? = null, path: String? = null): MediaSource {
        mServer?.stop()
        mServer = LocalSingleHttpServer()
        mServer?.setCipherFactory(MyCipherFactory())
        mServer?.start()
        return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(if (uri == null) Uri.parse(mServer?.getURL(path)) else uri)

    }


    fun initializeSeekBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (audioPlayer != null) {
                    val totalDuration = audioPlayer?.duration
                    val currentDuration = audioPlayer?.currentPosition
                    totalDurationTextView?.text = DateUtils.milliSecondsToTimer(totalDuration ?: 0)
                    currentPositiontTextView?.text = DateUtils.milliSecondsToTimer(currentDuration
                            ?: 0)
                    val mCurrentPosition = audioPlayer?.currentPosition
                    seekBar?.progress = mCurrentPosition ?: 0
                    initializeSeekBar()
                }
            }
        }
        handler.postDelayed(runnable, 10)
    }


    private fun playAudioPlayer() {
        audioPlayer?.start()
        timeElapsed = audioPlayer?.currentPosition
        seekBar?.progress = timeElapsed as Int
        initializeSeekBar()
    }

    private fun resetAudioPlayer(progress: Int) {
        audioPlayer?.start()
        audioPlayer?.seekTo(progress)
        seekBar?.progress = progress
        initializeSeekBar()
    }

    private fun resetAudioPlayerPosition(progress: Int) {
        audioPlayer?.seekTo(progress)
        seekBar?.progress = progress
        val totalDuration = audioPlayer!!.duration
        val currentDuration = audioPlayer!!.currentPosition
        totalDurationTextView.text = DateUtils.milliSecondsToTimer(totalDuration)
        currentPositiontTextView.text = DateUtils.milliSecondsToTimer(currentDuration)
    }


    fun scrollUp() {
        val handler = Handler()
        Thread(Runnable {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
            }

            handler.post { scrollView?.fullScroll(View.FOCUS_UP) }
        }).start()
    }

    private fun pauseAudioPlayer() {
        media_play.setImageResource(android.R.drawable.ic_media_play)
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

    private fun stopAudioPlayer() {
        if (audioPlayer != null) {
            audioPlayer?.stop()
            audioPlayer?.release()
            audioPlayer = null
        }
    }

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
            mServer?.stop()
            player = null
        }
    }

    override fun onPause() {
        releasePlayer()
        stopAudioPlayer()
        super.onPause()
    }

    override fun onStop() {
        releasePlayer()
        stopAudioPlayer()
        super.onStop()
    }

    override fun onDestroy() {
        releasePlayer()
        selectedMedia = null
        stopAudioPlayer()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (!isInitialStartAndResume) {
            selectedMedia?.let {
                checkAndDisplayMedia(it)
            }
        }
    }

    fun download(url: String, mediaType: MediaType, isEncryptedMedia: Boolean = false) {
        val downloader = Downloader.getInstance(null, context!!)
        downloader.download(url, activityModel.title
                ?: "", mediaType, ModuleType.Activity, false, isEncryptedMedia = isEncryptedMedia)
    }

    fun setDownloadButtonVisibilityForMedia(mediaType: MediaType, downloadEnum: DownloadEnum) {
        when (mediaType) {
            MediaType.Audio -> {
                audioDownloadLayout.tag = downloadEnum
                audioDownloadLayout.visibility = View.VISIBLE
                audioDownloadLabel.visibility = View.GONE

                audioDownloadImageView.setColorFilter(ContextCompat.getColor(context!!, R.color.green_shade_1), android.graphics.PorterDuff.Mode.MULTIPLY)
                when (downloadEnum) {
                    DownloadEnum.DOWNLOADING -> {
                        audioDownloadLabel.visibility = View.VISIBLE
                    }
                    DownloadEnum.NOT_DOWNLOADED -> {
                        audioDownloadImageView.setColorFilter(ContextCompat.getColor(context!!, R.color.rating_unselected), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }
                    DownloadEnum.LOCAL_FILE -> {
                        audioDownloadLayout.visibility = View.GONE
                    }
                    else -> {
                    }
                }
            }
            MediaType.Video -> {
                exo_download_button.tag = downloadEnum
                videoDownloadLabel.visibility = View.GONE
                exo_download_button.visibility = View.VISIBLE
                exo_video_download.setColorFilter(ContextCompat.getColor(context!!, R.color.green_shade_1), android.graphics.PorterDuff.Mode.MULTIPLY)

                when (downloadEnum) {
                    DownloadEnum.DOWNLOADING -> {
                        videoDownloadLabel.visibility = View.VISIBLE
                    }

                    DownloadEnum.NOT_DOWNLOADED -> {
                        exo_video_download.setColorFilter(ContextCompat.getColor(context!!, R.color.rating_unselected), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }
                    DownloadEnum.LOCAL_FILE -> {
                        exo_download_button.visibility = View.GONE
                    }
                    else -> {
                    }
                }
            }
            else -> {
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(getResourceString("today_activity"))
        setResourceString()
        videoRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        photosRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        audioRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        mediaListImage.clear()
        mediaListVideo.clear()
        mediaListAudio.clear()
        initPhotoAdapter(mediaListVideo, MediaType.Video)
        initPhotoAdapter(mediaListImage, MediaType.Image)
        initPhotoAdapter(mediaListAudio, MediaType.Audio)
        toggle()

        if (savedInstanceState != null) {
            with(savedInstanceState) {
                playWhenReady = getBoolean(KEY_PLAY_WHEN_READY)
                currentWindow = getInt(KEY_WINDOW)
                playbackPosition = getLong(KEY_POSITION)
            }
        }
        shouldAutoPlay = true
        mediaDataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, GMKeys.mediaPlayerSample),
                bandwidthMeter as TransferListener<in DataSource>)

        setUpListener()

        arguments?.getBoolean(GMKeys.isCompleted)?.let { isCompleted = it }

        arguments?.getBoolean(GMKeys.isFromNotification)?.let {
            isFromNotification = it
        }

        arguments?.getSerializable(GMKeys.selectedActivity)?.let {
            if (it is Model.ActivityList) {
                activityModel = it
                checkActivityStatus()
                val msgString = it.message ?: ""

                if (it.activityCategoryId == GMKeys.KEY_BASEACTIVITY) {
                    activityType?.text = getResourceString("special_activity")
                } else if (it.activityCategoryId == GMKeys.KEY_DEFAULT_ACTIVITY) {
                    activityType?.text = getResourceString("regular_activity")
                } else if (it.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) {
                    activityType?.visibility = View.GONE
                }

                dayTextView?.setCompoundDrawablesWithIntrinsicBounds(if (it.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) R.drawable.ic_shed else R.drawable.ic_day, 0, 0, 0)
                dayTextView?.text = if (it.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) getResourceString("shed_ready_activity_label") else getResourceString("day_label").format((it.activityAge
                        ?: 0).toString())
                descriptionTextView?.loadData(Html.fromHtml(msgString).toString(), "text/html", "utf-8")
                titleTextView?.text = it.title ?: ""


                DataProvider.threadBlock {
                    DataProvider.application?.database?.addActivityDetailsDao()?.getActivityById(activityModel.activityId
                            ?: 0)?.let {
                        answerStr = it.textAnswer
                        afterSubmittedInOffline(it)
                    }
                }
            }
        }

        setLayoutVisibilityByStatusAndData()

        initMediaAdapter(activityModel.videos, MediaType.Video)
        initMediaAdapter(activityModel.images, MediaType.Image)
        initMediaAdapter(activityModel.audios, MediaType.Audio)
        initMediaAdapter(activityModel.pdfs, MediaType.Pdf)
    }


    fun startDownload(isAudio: Boolean) {
        if (isAudio) {
            if ((audioDownloadLayout.tag as DownloadEnum) == DownloadEnum.NOT_DOWNLOADED) {
                setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADING)
                val url = URL(selectedMedia?.mediaUri?.toString())
                download(url.toString(), MediaType.Audio)
            }
        } else {
            if ((exo_download_button.tag as DownloadEnum) == DownloadEnum.NOT_DOWNLOADED) {
                setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADING)
                val url = URL(selectedMedia?.mediaUri?.toString())
                if (activityModel.responseVideos == null) {

                }
                download(url.toString(), MediaType.Video, isEncryptedVideo())
            }
        }
    }

    fun isEncryptedVideo(): Boolean {
        return activityModel.videos.any { it.url == selectedMedia?.url }
    }

    fun setUpListener() {
        //exo_fullscreen_button.visibility=View.GONE
        exo_fullscreen_button.setOnClickListener {
            isInitialStartAndResume = true
            val intent = Intent(context, FullScreenVideoActivity::class.java)
            if (uriForFullScreen == null) {
                intent.putExtra("VideoPath", pathForFullScreen)
            } else {
                intent.putExtra("VideoURL", uriForFullScreen.toString() ?: null)
            }
            intent.putExtra("ResumePosition", player?.contentPosition ?: 0L)
            intent.putExtra("ResumeWindow", player?.currentWindowIndex ?: 0)
            startActivityForResult(intent, FULL_SCREEN_REQUEST_CODE)
        }

        exo_download_button.setOnClickListener {
            mediaType = GMKeys.DOWNLOAD_STORAGE_PERMISSION_VIDEO
            checkAndGetPermission()
        }
        audioDownloadLayout.setOnClickListener {
            mediaType = GMKeys.DOWNLOAD_STORAGE_PERMISSION_AUDIO
            checkAndGetPermission()
        }

        addItemTextView.setOnClickListener {
            if (mediaList.size < 5)
                showUploadDialog(activityModel)
            else
                showSnackBar(getResourceString("message_upload_media"))
        }
        submitButton.setOnClickListener {
            if (submitValidation()) {
                showConfirmationDialog()
            }
        }
        deleteTextView.setOnClickListener {
            updateList()
        }
        uploadMediaTextView.setOnClickListener {
            showUploadDialog(activityModel)
        }

        //Audio Player Functions
        media_play.setOnClickListener {
            if (playOrPauseAudioType == 0) {
                media_play.setImageResource(android.R.drawable.ic_media_pause)
                playOrPauseAudioType = 1
                playAudioPlayer()

            } else {
                media_play.setImageResource(android.R.drawable.ic_media_play)
                playOrPauseAudioType = 0
                pauseAudioPlayer()
            }
        }
        media_pause.setOnClickListener {
            pauseAudioPlayer()
        }

        media_forward.setOnClickListener {
            forwardAudioPlayer()
        }

        media_rewind.setOnClickListener {
            backwardAudioPlayer()
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (audioPlayer!!.isPlaying) {
                    resetAudioPlayer(seekBar.progress)
                } else {
                    resetAudioPlayerPosition(seekBar.progress)
                }
            }
        })

    }

    fun setLayoutVisibilityByStatusAndData() {
        default_activity_layout.visibility = View.VISIBLE

        if (isCompleted) {
            subTitle?.visibility = View.GONE
            mediaTitleLayout?.visibility = View.GONE
            commentEditText?.visibility = View.VISIBLE
            commentEditText?.isEnabled = false
            commentEditText?.setText(activityModel.textAnswer)
            /*Answer Layout  only for completed*/
            if (!activityModel.response.isNullOrEmpty()) {
                resultLayout?.visibility = View.VISIBLE
                questions?.text = activityModel.question ?: ""
                answers.text = activityModel.response ?: ""
            }
            addItemTextView?.visibility = View.GONE
            deleteTextView?.visibility = View.GONE
            submitButton?.visibility = View.GONE


            activityModel.responseVideos?.let {
                it.mapNotNull { it.url }.forEach { url -> mediaListVideo.add(Model.UploadMedia(url = url, mediaUri = Uri.parse(URLUtils.baseUrl + url), mediaTypeId = MediaType.Video.toString())) }
            }

            activityModel.responseImages?.let {
                it.mapNotNull { it.url }.forEach { url -> mediaListImage.add(Model.UploadMedia(url = url, mediaUri = Uri.parse(URLUtils.baseUrl + url), mediaTypeId = MediaType.Image.toString())) }
            }

            activityModel.responseAudios?.let {
                it.mapNotNull { it.url }.forEach { url -> mediaListAudio.add(Model.UploadMedia(url = url, mediaUri = Uri.parse(URLUtils.baseUrl + url), mediaTypeId = MediaType.Audio.toString())) }
            }
            /*Display Completed  Uploaded Response Media*/
            initPhotoAdapter(mediaListVideo, MediaType.Video)
            initPhotoAdapter(mediaListImage, MediaType.Image)
            initPhotoAdapter(mediaListAudio, MediaType.Audio)

            if (mediaListAudio.size == 0 && mediaListImage.size == 0 && mediaListVideo.size == 0) {
                mediaLayoutView.visibility = View.GONE
            }


            val msgString = activityModel.hint ?: ""
            hint.loadData(Html.fromHtml(msgString).toString(), "text/html", "utf-8")
            rangeLayout.visibility = View.GONE


            if (activityModel.activityCategoryId != 3) {
                completedResult?.visibility = View.VISIBLE
                completedQuestion?.text = activityModel.question
                if (activityModel.textAnswer != null) {
                    answer?.text = activityModel.textAnswer
                } else {
                    answer?.text = activityModel.response
                }
            }

        } else {
            mediaTitleLayout?.visibility = View.VISIBLE
            commentEditText?.visibility = View.VISIBLE
            mediaLayoutView?.visibility = View.VISIBLE
            question?.text = activityModel.question

            if (activityModel.hint != null) {
                recommeded?.visibility = View.VISIBLE
                hint?.loadData(Html.fromHtml(activityModel.hint
                        ?: "").toString(), "text/html", "utf-8")
            } else {
                recommeded?.visibility = View.VISIBLE
            }

            if (activityModel.responseTypeId == 1.toLong()) {
                radioRecyclerView?.visibility = View.VISIBLE
                rangeLayout?.visibility = View.GONE
                activityModel.options?.let {
                    if (it.contains(',')) {
                        radioRecyclerView?.layoutManager = LinearLayoutManager(context)
                        val singleChoiceStringArr = activityModel.options?.split(",")
                                ?: arrayListOf()
                        singleChoiceList.clear()
                        singleChoiceStringArr.forEach {
                            singleChoiceList.add(Model.SingleChoice(it, false))
                        }
                        radioRecyclerView?.adapter = SingleChoiceAdapter(singleChoiceList, context!!, this)
                    }
                }

            } else {
                rangeLayout?.visibility = View.VISIBLE
                radioRecyclerView?.visibility = View.GONE
            }

            submitButton?.visibility = View.VISIBLE
            submitButton?.text = getResourceString("submit")

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
        confirmationDialogView.cancelTextView.text = getResourceString("cancel")
        confirmationDialogView.confirmTextView.text = getResourceString("ok")
        confirmationDialogView.confirmationMsgTextView?.text = getResourceString("confirm_submission")
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
            submit()
        }
        confirmationDialog?.show()
    }


    private fun showConfirmationDeleteDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_confirmation_dialog, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.confirmationMsgTextView.text = getResourceString("alter_message")
        confirmationDialogView.cancelTextView.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialogView.confirmationMsgTextView?.text = getResourceString("confirm_submission")
        confirmationDialogView.cancelTextView?.text = getResourceString("cancel")
        confirmationDialogView.confirmTextView?.text = getResourceString("label_confirm")
        confirmationDialogView.confirmTextView.setOnClickListener {
            confirmationDialogView.confirmTextView?.text = getResourceString("yes")
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


    fun submit() {
        if (mediaList.size != 0) {
            (activity as HomeActivity).checkLocationPermission()
        } else {
            pauseMedia()
            showProgressBar()
            sendData()
        }
    }

    fun sendData() {
        if (rangeLayout?.visibility == View.VISIBLE) {
            if (!rangeText?.text.toString().isEmpty()) answerStr = rangeText.text.toString()
        } else {
            if (singleChoiceList.any { it.isSelected == true }) answerStr = singleChoiceList.singleOrNull { it.isSelected == true }?.option
                    ?: ""
        }

        var commendStr: String? = null
        if (!commentEditText?.text.toString().isEmpty()) commendStr = commentEditText?.text.toString()
        saveActivityDetails(activityModel.activityId!!, answerStr, commendStr)
    }


    fun submitValidation(): Boolean {
        var bool = false
        if (commentEditText?.text.toString() != "") {
            bool = true
        } else if (singleChoiceList.any { it.isSelected == true }) {
            bool = true
        } else if (rangeLayout.visibility == View.VISIBLE) {
            if (!rangeText.text.toString().isEmpty()) {
                bool = true
            } else {
                bool = false
                showSnackBar(getResourceString("message_enter"))
            }
        } else {
            showSnackBar(getResourceString("message_enter"))
            bool = false
        }
        if (bool) {
            if (activityModel.isUpload == true) {
                if (mediaList.size > 0) {
                    bool = true
                } else {
                    showSnackBar(getResourceString("upload_error_message"))
                    bool = false
                }
            } else {
                bool = true
            }
        }
        return bool

    }


    private fun startGalleryIntent(type: MediaType) {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        when (type) {
            MediaType.Image -> intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            MediaType.Video -> {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                val videosMimeTypes = java.util.ArrayList<String>()
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


    private fun toggle() {
        toggleVisibility(MediaType.Image, mediaListImage)
        toggleVisibility(MediaType.Video, mediaListVideo)
        toggleVisibility(MediaType.Audio, mediaListAudio)
        if (mediaListImage.size == 0 && mediaListAudio.size == 0 && mediaListVideo.size == 0) {
            uploadMediaTextView?.visibility = View.VISIBLE
            newUploadLayout?.visibility = View.VISIBLE
            //   emptyImage?.visibility = View.VISIBLE
            addItemTextView?.visibility = View.GONE
            deleteTextView?.visibility = View.GONE
            lable_upload?.visibility = View.GONE

        } else {
            uploadMediaTextView?.visibility = View.GONE
            emptyImage?.visibility = View.GONE
            newUploadLayout?.visibility = View.GONE
            addItemTextView?.visibility = View.VISIBLE
            deleteTextView?.visibility = View.VISIBLE
            lable_upload?.visibility = View.VISIBLE
        }

    }


    private fun toggleVisibility1(mediaType: MediaType, list: ArrayList<Model.Media>) {
        when (mediaType) {
            MediaType.Audio -> {
                if (list.size == 0) {
                    audioLinearLayout1?.visibility = View.GONE
                } else {
                    audioLinearLayout1?.visibility = View.VISIBLE
                }
            }
            MediaType.Video -> {
                if (list.size == 0) {
                    videoLinearLayout1?.visibility = View.GONE
                } else {
                    videoLinearLayout1?.visibility = View.VISIBLE
                }
            }
            MediaType.Image -> {
                if (list.size == 0) {
                    photoLinearLayout1?.visibility = View.GONE
                } else {
                    photoLinearLayout1?.visibility = View.VISIBLE
                }
            }
            MediaType.Pdf -> {
                if (list.size == 0) {
                    pdfLinearLayout1?.visibility = View.GONE
                } else {
                    pdfLinearLayout1?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun toggleVisibility(mediaType: MediaType, list: ArrayList<Model.UploadMedia>) {
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

    private fun deleteMedia() {
        resetDeleteSelection()
        deleteStatus = false
        toggleStatus()
        deletefiles.forEach {
            val uri = it.mediaUri
            if (currentItem.url.equals(it?.url)) {
                imageFrameLayout.visibility = View.GONE
            }

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

    private fun updateList() {
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


    private fun resetDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.delete_media)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.red_shade_1)!!)
        toggleClickableAction(true)

    }

    private fun setDeleteSelection() {
        val deleteSelected = context?.resources?.getDrawable(R.drawable.ic_delete_selected)
        val emptyImage: Drawable? = null
        deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteSelected, emptyImage, emptyImage, emptyImage)
        deleteTextView?.setBackgroundResource(R.drawable.button_background)
        deleteTextView?.setTextColor(context?.resources?.getColor(R.color.white)!!)
        toggleClickableAction(false)
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

    private fun setLocation() {
        val gpsTracker = GPSTracker(context)
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.latitude
            longitude = gpsTracker.longitude

            uploadMedia(0)

        } else {
            gpsTracker.showSettingsAlert()
        }

    }

    private fun setLatitudeLongitude() {
        latitude = 0.0
        longitude = 0.0
        uploadMedia(0)
    }


    private fun toggleClickableAction(status: Boolean) {
        addItemTextView?.isEnabled = status
        commentEditText?.isEnabled = status
        submitButton?.isEnabled = status

        if (status) {
            uploadDisableLayout?.visibility = View.GONE
            addDisableLayout?.visibility = View.GONE
        } else {

            uploadDisableLayout?.visibility = View.VISIBLE
            addDisableLayout?.visibility = View.VISIBLE
        }

    }

    private fun uploadMedia(position: Int) {
        if (position == 0) {
            pauseMedia()
            showProgressBar()
        }
        if (position < mediaList.size) {
            val mediaItem = mediaList[position]
            var mediaUpload = Model.OfflineMediaActivity()
            mediaItem.let {
                mediaUpload.activityId = activityModel.activityId ?: 0
                mediaUpload.latitude = latitude.toString()
                mediaUpload.longitude = longitude.toString()
                mediaUpload.mediaTypeId = it.MediaTypeId
                mediaUpload.mediaPath = it.file?.absolutePath
                mediaUpload.url = it.url
                mediaUpload.mediaType = it.mediaType

                DataProvider.uploadMedia(mediaUpload, object : ServiceRequestListener {
                    override fun onRequestCompleted(responseObject: Any?) {
                        uploadMedia(position + 1)
                    }

                    override fun onRequestFailed(responseObject: String) {
                        showSnackBar(getResourceString("error_upload"))
                        resumeMedia()
                        dismissProgressBar()
                    }
                })
            }
        } else {
            sendData()
        }
    }


    fun saveActivityDetails(activityId: Long, textAnswer: String?, comment: String?) {
        var saveDetails = Model.SaveActivityDetails()
        saveDetails.activityId = activityId
        saveDetails.comment = comment
        saveDetails.textAnswer = textAnswer
        saveDetails.userId = GMApplication.loginUserId
        DataProvider.saveActivityDetails(saveDetails, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                setCompletedLayoutFromPending()
                showSnackBar(getResourceString("submittedSucessfully"))
                resumeMedia()
                dismissProgressBar()
            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()
                resumeMedia()
                showErrorSnackBar(responseObject)

            }
        })

    }

    /*after summit the activity*/
    fun setCompletedLayoutFromPending() {
        resultLayout?.visibility = View.VISIBLE
        recommeded?.visibility = View.VISIBLE
        newUploadLayout?.visibility = View.GONE
        titleLayout?.visibility = View.GONE
        submitButton?.visibility = View.GONE
        question?.visibility = View.GONE
        subTitle?.visibility = View.GONE
        radioRecycleLayout?.visibility = View.GONE
        rangeLayout?.visibility = View.GONE
        mediaTitleLayout?.visibility = View.GONE
        emptyImage?.visibility = View.GONE
        emptyImage1?.visibility = View.GONE
        default_activity_layout?.visibility = View.VISIBLE
        questions?.text = activityModel?.question
        answers?.text = answerStr
        commentEditText?.isEnabled = false
        commentEditText?.isFocusable = false
        commentEditText?.visibility = View.GONE
    }


    fun resumeMedia() {
        selectedMedia?.let {
            checkAndDisplayMedia(it)
        }
    }


    fun pauseMedia() {
        pauseAudioPlayer()
        releasePlayer(true)
    }


    private fun showUploadDialog(item: Model.ActivityList) {
        pauseMedia()
        dialog = Dialog(context!!)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.alert_media_upload)
        dialog?.closeImage?.setOnClickListener {
            resumeMedia()
            dialog?.dismiss()
        }
        dialog?.mediaTitleTextView?.text = getResourceString("media_recorder")
        dialog?.galleryTitleTextView?.text = getResourceString("gallery")
        dialog?.uploadDialogTextView?.text = getResourceString("upload")
        dialog?.videoDialogTextView?.text = getResourceString("select_video")
        dialog?.audioDialogTextView?.text = getResourceString("select_audio")
        dialog?.imageDialogTextView?.text = getResourceString("select_image")
        dialog?.recordTextView?.text = getResourceString("record_video")
        dialog?.recordAudioTextView?.text = getResourceString("record_audio")
        dialog?.captureTextView?.text = getResourceString("capture_photo")
        dialog?.galleryLayout?.setOnClickListener {
            mediaType = GMKeys.GALLERY_INTENT
            checkAndGetPermission()
        }
        dialog?.mediaLayout?.setOnClickListener {
            setMediaCaptureSelection(dialog!!, item)
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
        dialog?.media_photo?.setOnClickListener {
            mediaType = GMKeys.TAKE_PHOTO
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


    private fun setGallerySelection(dialog: Dialog, item: Model.ActivityList) {
        if (dialog.selectionLayout.visibility == View.GONE) {
            dialog.selectionLayout.visibility = View.VISIBLE
            if (activityModel.isUpload == true) {
                dialog.select_video.visibility = if (item.allowVideo == true) View.VISIBLE else View.GONE
                dialog.select_audio.visibility = if (item.allowAudio == true) View.VISIBLE else View.GONE
                dialog.select_photo.visibility = if (item.allowImage == true) View.VISIBLE else View.GONE
            } else {
                dialog.select_video.visibility = View.VISIBLE
                dialog.select_audio.visibility = View.VISIBLE
                dialog.select_photo.visibility = View.VISIBLE
            }
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

    private fun setMediaCaptureSelection(dialog: Dialog, item: Model.ActivityList) {
        if (dialog.mediaCaptureLayout.visibility == View.GONE) {
            dialog.mediaCaptureLayout.visibility = View.VISIBLE
            if (activityModel.isUpload == true) {
                dialog.media_record_video?.visibility = if (item.allowVideo == true) View.VISIBLE else View.GONE
                dialog.media_record_audio?.visibility = if (item.allowAudio == true) View.VISIBLE else View.GONE
                dialog.media_photo?.visibility = if (item.allowImage == true) View.VISIBLE else View.GONE
            } else {
                dialog.media_record_video?.visibility = View.VISIBLE
                dialog.media_record_audio?.visibility = View.VISIBLE
                dialog.media_photo?.visibility = View.VISIBLE
            }
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
                setGallerySelection(dialog!!, activityModel)
            }
        } else if (mediaType == GMKeys.DOWNLOAD_STORAGE_PERMISSION_AUDIO) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                startDownload(isAudio = true)
            }
        } else if (mediaType == GMKeys.DOWNLOAD_STORAGE_PERMISSION_VIDEO) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                startDownload(isAudio = false)
            }
        }
    }


    private fun initPhotoAdapter(imageFile: ArrayList<Model.UploadMedia>, mediaType: MediaType) {
        when (mediaType) {
            MediaType.Image -> {
                imageAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType, isCompleted)
                photosRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                photosRecyclerView?.adapter = imageAdapter
            }
            MediaType.Video -> {
                videoAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType, isCompleted)
                videoRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                videoRecyclerView?.adapter = videoAdapter
            }
            MediaType.Audio -> {
                audioAdapter = MediaTypeAdapter(imageFile, context!!, this, mediaType, isCompleted)
                audioRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                audioRecyclerView?.adapter = audioAdapter

            }
        }
        toggle()
    }


    private fun initMediaAdapter(media: ArrayList<Model.Media>, mediaType: MediaType) {

        when (mediaType) {
            MediaType.Image -> {
                imageActivityAdapter = MediaTypeActivityAdapter(media, context!!, this, mediaType, isCompleted)
                photosRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                photosRecyclerView1?.adapter = imageActivityAdapter
            }
            MediaType.Video -> {
                videoActivityAdapter = MediaTypeActivityAdapter(media, context!!, this, mediaType, isCompleted)
                videoRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                videoRecyclerView1?.adapter = videoActivityAdapter
            }
            MediaType.Audio -> {
                audioActivityAdapter = MediaTypeActivityAdapter(media, context!!, this, mediaType, isCompleted)
                audioRecyclerView1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                audioRecyclerView1?.adapter = audioActivityAdapter
            }
            MediaType.Pdf -> {
                pdfLinearLayout1.visibility = View.VISIBLE
                pdfActivityAdapter = MediaTypeActivityAdapter(media, context!!, this, mediaType, isCompleted)
                pdfRecyclerView1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                pdfRecyclerView1?.adapter = pdfActivityAdapter
            }

        }
        toggleVisibility1(mediaType, media)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE) {
                data?.getStringExtra(TrimmerActivity.EXTRA_OUTPUT_URI)?.let { path ->
                    changeMediaUriFromGallery(path, isTrimmedVideo = true)
                }

            } else if (requestCode == FULL_SCREEN_REQUEST_CODE) {
                playbackPosition = data?.getLongExtra("RESUME_POSITION", 0L)!!
                currentWindow = data.getIntExtra("CURRENT_INDEX", 0)
                isInitialStartAndResume = false
            } else if (requestCode == GMKeys.VIDEO_CAPTURED) {
                val videoUriData: Uri? = data?.data
                videoUriData?.let {
                    val videoDetails = Model.UploadMedia()
                    val media = Model.UploadMedia()
                    media.MediaTypeId = GMKeys.VIDEO_ID
                    media.file = File(changeMediaUri(RealPathUtil.getRealPathFromUri(context!!, it)))
                    val videoUri = Uri.parse(media.file!!.absolutePath)
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
                                    mediaList.add(Model.UploadMedia(mediaTypeId = GMKeys.IMAGE_ID.toString(), mediaUri = uri, MediaTypeId = GMKeys.IMAGE_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggle()
                                    toggleVisibility(MediaType.Image, mediaListImage)
                                } else if (mime.contains("video/")) {
                                    val filePath = RealPathUtil.getRealPathFromUri(context!!, uri)
                                    try {
                                        val retriever = MediaMetadataRetriever()
                                        retriever.setDataSource(filePath)
                                        val timeInMinutes = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()?.let { it1 -> TimeUnit.MILLISECONDS.toMinutes(it1) }
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
                                    mediaList.add(Model.UploadMedia(mediaTypeId = GMKeys.AUDIO_ID.toString(), mediaUri = uri, MediaTypeId = GMKeys.AUDIO_ID, file = File(RealPathUtil.getRealPathFromUri(context!!, uri))))
                                    toggle()
                                    toggleVisibility(MediaType.Audio, mediaListAudio)
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

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startVideoTrimmerActivity(uri: Uri) {
        val intent = Intent(context, TrimmerActivity::class.java)
        intent.putExtra(TrimmerActivity.EXTRA_INPUT_URI, uri)
        intent.putExtra(TrimmerActivity.IS_VIDEO_URI, true)
        startActivityForResult(intent, TrimmerActivity.VIDEO_TRIMMER_REQUEST_CODE)
    }

    private fun changeMediaUri(sourcePath: String?): String? {
        val destinationPath = activity?.externalCacheDir?.path + File.separator + "compressedVideo"
        val desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedActivity" + System.currentTimeMillis() + ".mp4"
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


    fun changeMediaUriFromGallery(sourcePath: String?, isTrimmedVideo: Boolean = false) {
        val destinationPath = activity?.externalCacheDir?.path + File.separator + "compressedVideo"
        val desPath = File(destinationPath)
        desPath.mkdirs()
        var desFileName: String
        try {
            desFileName = sourcePath?.substring(sourcePath?.lastIndexOf("/") + 1) ?: ""
        } catch (e: Exception) {
            desFileName = "CompressedActivity" + System.currentTimeMillis() + ".mp4"
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
                    toggle()
                    toggleVisibility(MediaType.Video, mediaListVideo)
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


    override fun onSaveInstanceState(outState: Bundle) {
        updateStartPosition()
        with(outState) {
            putBoolean(KEY_PLAY_WHEN_READY, playWhenReady)
            putInt(KEY_WINDOW, currentWindow)
            putLong(KEY_POSITION, playbackPosition)
        }
        super.onSaveInstanceState(outState)
    }


    private fun updateStartPosition() {
        player?.let {
            with(player!!) {
                playbackPosition = currentPosition
                currentWindow = currentWindowIndex
                playWhenReady = playWhenReady
            }
        }
    }

    private fun updateButtonVisibilities() {
        if (player == null) {
            return
        }
        val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo ?: return
        for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                if (player?.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                }
            }
        }
    }


    private inner class PlayerEventListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> progress_bar?.visibility = View.VISIBLE     // The player does not have any media to play yet.
                Player.STATE_BUFFERING -> progress_bar?.visibility = View.VISIBLE // The player is buffering (loading the content)
                Player.STATE_READY -> progress_bar?.visibility = View.GONE   // The player is able to immediately play
                Player.STATE_ENDED -> progress_bar?.visibility = View.GONE    // The player has finished playing the media
            }
            updateButtonVisibilities()
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            updateButtonVisibilities()
            // The video tracks are no supported in this device.
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(this@ActivityViewFragment.activity, "Error unsupported track", Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    /* Check Activity Status if it is Navigated from Notification List */
    private fun checkActivityStatus() {
        /* For Completed Activity Status is true and Pending Activity status is False */
        if (isFromNotification) {
            if (activityModel.status == true) {
                /* Completed activity - Disable editable and submit button */
                isCompleted = true
            } else {
                /* Pending activity with Past date - Disable editable and submit button */
                val todayDate = DateUtils.getTodayDate(SERVER_DATE_FORMAT)
                var formattedDate = ""
                if (!activityModel.toDate.isNullOrEmpty()) {
                    formattedDate = DateUtils.toDisplayDate(activityModel.toDate!!, SERVER_DATE_FORMAT)
                } else {
                    activityModel.fromDate?.let {
                        formattedDate = DateUtils.toDisplayDate(activityModel.fromDate!!, SERVER_DATE_FORMAT)
                    }
                }
                Log.d("today", todayDate)
                Log.d("from", formattedDate)
                isCompleted = checkDate(todayDate, formattedDate)
            }
        }
    }

    private fun checkDate(todayDate: String, formattedDate: String): Boolean {
        // return todayDate != formattedDate
        var remainingDays = DateUtils.getDayDifference(todayDate, formattedDate)
        var convertedDate = remainingDays?.toInt()
        return convertedDate ?: 0 < 0
    }
}
