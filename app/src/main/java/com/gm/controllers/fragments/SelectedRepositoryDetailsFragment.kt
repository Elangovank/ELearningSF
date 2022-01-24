package com.gm.controllers.fragments


import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.WebServices.URLUtils
import com.gm.controllers.activities.FullScreenVideoActivity
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.MediaTypeAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.models.ModuleType
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.*
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
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
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import kotlinx.android.synthetic.main.fragment_selected_repository_detail.*
import kotlinx.android.synthetic.main.media_view.*
import java.io.File
import java.net.URL


class SelectedRepositoryDetailsFragment : GMBaseFragment(), OnItemClickListener {

    private var data = Model.Repository()

    private var currentUrl:String?=null

    private var imageAdapter: MediaTypeAdapter? = null
    private var audioAdapter: MediaTypeAdapter? = null
    private var videoAdapter: MediaTypeAdapter? = null
    var audioList = ArrayList<Model.UploadMedia>()
    var videoList = ArrayList<Model.UploadMedia>()
    var imageList = ArrayList<Model.UploadMedia>()
    var player: SimpleExoPlayer? = null
    var playWhenReady = true
    var mServer: LocalSingleHttpServer? = null
    private var mediaType = GMKeys.TAKE_PHOTO
    private var audioPlayer1: MediaPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var shouldAutoPlay: Boolean = true
    private val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
    // var mediaPlayer: MediaPlayer? = null
    var timeElapsed: Int? = 0
    var finalTime = 0
    var forwardTime = 2000
    var backwardTime = 2000
    var repositoryId: Long? = 0
    var type = 0
    val FULL_SCREEN_REQUEST_CODE = 111
    private var isInitialStartAndResume = true

    private lateinit var mediaDataSourceFactory: DataSource.Factory

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.UploadMedia) {
            if (!item.url.equals(currentUrl))
            checkAndDisplayMedia(item)
        }
    }

    companion object {
        fun newInstance(args: Bundle): SelectedRepositoryDetailsFragment {
            val fragment = SelectedRepositoryDetailsFragment()
            fragment.arguments = args
            return fragment
        }

        var videoPath: String? = null
        var selectedMedia: Model.UploadMedia? = null
        var currentWindow = 0
        var playbackPosition = 0L

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataRepository.getServerTime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_selected_repository_detail, container, false)
    }

    fun setResourceString()
    {
        audioDownloadLabel?.text=getResourceString("downloading")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        data = (arguments?.getSerializable(GMKeys.repositoryActivity)

                ?: arguments?.getSerializable("key")) as Model.Repository
        initToolbar(data.title.toString())
        currentWindow = 0
        playbackPosition = 0L

        repositoryId = data.repositoryId
        DataRepository.repositoryId=data.repositoryId
        mediaDataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"),
                bandwidthMeter as TransferListener<in DataSource>)
        dateTextView.visibility = View.GONE
        if (arguments?.getSerializable(GMKeys.repositoryActivity) == null) {
            dismissProgressBar()
        }
        setUpListener()
        toggleVisibility()
        setData(data)
        if (!NetworkAvailability.isNetworkAvailable(context!!))
        {
            imageFrameLayout.visibility=View.GONE
        }
    }


    fun scrollUp() {
        val handler = Handler()
        Thread(Runnable {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
            }

            handler.post { scrollView.fullScroll(View.FOCUS_UP) }
        }).start()
    }


    fun setUpListener() {
        val controlView = playerView!!.findViewById<View>(R.id.exo_controller)
        media_rew.setOnClickListener {
            backward()
        }
        media_pause.setOnClickListener {
            pause()
        }
        media_play.setOnClickListener {
            if (type == 0) {
                media_play.setImageResource(android.R.drawable.ic_media_pause)
                type = 1
                play()

            } else {
                media_play.setImageResource(android.R.drawable.ic_media_play)
                type = 0
                pause()
            }

        }
        media_forward.setOnClickListener {
            forward()
        }

        exo_download_button.setOnClickListener {
            mediaType = GMKeys.DOWNLOAD_STORAGE_PERMISSION_VIDEO
            checkAndGetPermission()
        }

        media_play.setOnClickListener {
            if (type == 0) {
                media_play.setImageResource(android.R.drawable.ic_media_pause)
                type = 1
                playAudioPlayer()

            } else {
                media_play.setImageResource(android.R.drawable.ic_media_play)
                type = 0
                pauseAudioPlayer()
            }
        }
        media_pause.setOnClickListener {
            pauseAudioPlayer()
        }

        media_forward.setOnClickListener {
            forwardAudioPlayer()
        }

        media_rew.setOnClickListener {
            backwardAudioPlayer()
        }

        audioDownloadLayout.setOnClickListener {
            mediaType = GMKeys.DOWNLOAD_STORAGE_PERMISSION_AUDIO
            checkAndGetPermission()
        }


        exo_fullscreen_button.setOnClickListener {
            isInitialStartAndResume = true
            var intent = Intent(context, FullScreenVideoActivity::class.java)
            intent.putExtra("VideoPath", videoPath)
            intent.putExtra("ResumePosition", player?.contentPosition ?: 0L)
            intent.putExtra("ResumeWindow", player?.currentWindowIndex ?: 0)
            startActivityForResult(intent, FULL_SCREEN_REQUEST_CODE)

        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (audioPlayer1!!.isPlaying) {
                    resetAudioPlayer(seekBar.progress)
                } else {
                    resetAudioPlayerPosition(seekBar.progress)
                }
            }
        })
    }


    private fun pauseAudioPlayer() {
        media_play.setImageResource(android.R.drawable.ic_media_play)
        type = 0
        audioPlayer1?.pause()
    }

    private fun forwardAudioPlayer() {
        val time = timeElapsed?.plus(GMKeys.forwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.plus(GMKeys.forwardTime)
                timeElapsed?.let { audioPlayer1?.seekTo(it) }
            }
        }
    }

    private fun backwardAudioPlayer() {
        val time = timeElapsed?.minus(GMKeys.backwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.minus(GMKeys.backwardTime)
                timeElapsed?.let { audioPlayer1?.seekTo(it) }
            }
        }
    }


    private fun playAudioPlayer() {
        audioPlayer1?.start()
        timeElapsed = audioPlayer1?.currentPosition
        seekBar?.progress = timeElapsed as Int
        initializeSeekBar()
    }


    private fun resetAudioPlayer(progress: Int) {
        audioPlayer1?.start()
        audioPlayer1?.seekTo(progress)
        seekBar?.progress = progress
        initializeSeekBar()
    }

    private fun resetAudioPlayerPosition(progress: Int) {
        audioPlayer1?.seekTo(progress)
        seekBar?.progress = progress
        val totalDuration = audioPlayer1!!.duration
        val currentDuration = audioPlayer1!!.currentPosition
        totalDurationTextView.text = DateUtils.milliSecondsToTimer(totalDuration)
        currentPositiontTextView.text = DateUtils.milliSecondsToTimer(currentDuration)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FULL_SCREEN_REQUEST_CODE) {
                //  var requiredURL = data?.getStringExtra("URL")
                playbackPosition = data?.getLongExtra("RESUME_POSITION", 0L)!!
                currentWindow = data.getIntExtra("CURRENT_INDEX", 0)
                isInitialStartAndResume = false

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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

    override fun onPermissionGranted(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_STORAGE) {
            checkAndGetPermission()
        }
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    private fun toggleVisibility() {
        //  topView.visibility = View.GONE
        titleLayout.visibility = View.VISIBLE
        mediaTextView.visibility = View.VISIBLE
        mediaTextView?.text = getResourceString("media")
        mediaTitleLayout.visibility = View.GONE
        addItemTextView.visibility = View.GONE
        deleteTextView.visibility = View.GONE
        uploadMediaTextView.visibility = View.GONE
        //   bottomView.visibility = View.VISIBLE

    }


    private fun checkAndGetPermission() {
        if (mediaType == GMKeys.DOWNLOAD_STORAGE_PERMISSION_AUDIO) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                startDownload(isAudio = true)
            }
        } else if (mediaType == GMKeys.DOWNLOAD_STORAGE_PERMISSION_VIDEO) {
            if ((activity as HomeActivity).checkStoragePermission()) {
                startDownload(isAudio = false)
            }
        }
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

    fun startDownload(isAudio: Boolean) {
        if (isAudio) {
            if ((audioDownloadLayout.tag as DownloadEnum) == DownloadEnum.NOT_DOWNLOADED) {
                selectedMedia?.url?.let {
                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADING)
                    val url = URL(URLUtils.baseUrl + (it))
                    download(url.toString(), MediaType.Audio)
                }
            }
        } else {
            if ((exo_download_button.tag as DownloadEnum) == DownloadEnum.NOT_DOWNLOADED) {
                selectedMedia?.url?.let {
                    setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADING)
                    val url = URL(URLUtils.baseUrl + (it))
                    download(url.toString(), MediaType.Video, true)
                }
            }
        }
    }

    fun download(url: String, mediaType: MediaType, isEncryptedMedia: Boolean = false) {
        val downloader = Downloader.getInstance(null, context!!)
        downloader.download(url, data.title
                ?: "", mediaType, ModuleType.Repository, false, isEncryptedMedia)
    }

    private fun setData(repository: Model.Repository) {
        imageList?.clear()
        audioList?.clear()
        videoList?.clear()
        titleTextView?.text = repository.title
        dateTextView?.text = DateUtils.toDisplayDate(repository.uploadedDate)
        contentTextView.loadData(Html.fromHtml(repository.message).toString(), "text/html", "utf-8")
        // contentTextView?.text =Html.fromHtml(repository.message!!).toString()
        repository.images?.forEach {
            imageList.add(Model.UploadMedia(it.id, it.url, mediaType = MediaType.Image))
        }
        repository.videos?.forEach {
            videoList.add(Model.UploadMedia(it.id, it.url, mediaType = MediaType.Video))
        }
        repository.audios?.forEach {
            audioList.add(Model.UploadMedia(it.id, it.url, mediaType = MediaType.Audio))
        }
        when {
            videoList.size != 0 -> checkAndDisplayMedia(videoList[0])
            imageList.size != 0 -> checkAndDisplayMedia(imageList[0])
            audioList.size != 0 -> checkAndDisplayMedia(audioList[0])
        }
        if (videoList.size == 0 && imageList.size == 0 && audioList.size == 0) {
            imageFrameLayout.visibility = View.GONE
            emptyImage.visibility = View.VISIBLE
            noItemTextView.visibility = View.VISIBLE
        } else {
            emptyImage.visibility = View.GONE
            noItemTextView.visibility = View.GONE
            imageFrameLayout.visibility = View.VISIBLE
        }
        initPhotoAdapter(imageList, videoList, audioList)

    }

    private fun initPhotoAdapter(imageList: ArrayList<Model.UploadMedia>, videoList: ArrayList<Model.UploadMedia>, audioList: ArrayList<Model.UploadMedia>) {
        context?.let {
            //image
            if (imageList.size != 0) {
                imageAdapter = MediaTypeAdapter(imageList, it, this, MediaType.Image, true, 1)
                photosRecyclerView?.layoutManager = LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)
                photosRecyclerView?.adapter = imageAdapter
            } else {
                photoLinearLayout.visibility = View.GONE
            }
            //video
            if (videoList.size != 0) {
                videoAdapter = MediaTypeAdapter(videoList, it, this, MediaType.Video, true, 1,moduleType = ModuleType.Repository)
                videoRecyclerView?.layoutManager = LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)
                videoRecyclerView?.adapter = videoAdapter
            } else {
                videoLinearLayout.visibility = View.GONE
            }
            //audio
            if (audioList.size != 0) {
                audioAdapter = MediaTypeAdapter(audioList, it, this, MediaType.Audio, true, 1)
                audioRecyclerView?.layoutManager = LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)
                audioRecyclerView?.adapter = audioAdapter
            } else {
                audioLinearLayout.visibility = View.GONE
            }
        }
    }


    private fun checkAndDisplayMedia(item: Model.UploadMedia) {
        imageFrameLayout.visibility = View.GONE
        selectedMedia = item
        currentUrl=item.url
        when (item.mediaType) {
            MediaType.Audio -> {
                    DataProvider.threadBlock {
                        val downloadItem = DataProvider.application?.database?.getManageDownloadDao()?.getItemByUrl(URLUtils.baseUrl + item.url)
                        activity?.runOnUiThread {
                            if (downloadItem == null) {
                                setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.NOT_DOWNLOADED)
                                playAudio(Uri.parse(URLUtils.baseUrl + item.url))
                            } else if (downloadItem.isDownloaded == true) {
                                if (File(downloadItem.filePath ?: "").exists()) {
                                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADED)
                                    playAudio(Uri.parse(downloadItem.filePath), true)
                                } else {
                                    DataProvider.threadBlock {
                                        DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(downloadItem))
                                    }
                                    setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.NOT_DOWNLOADED)
                                    playAudio(Uri.parse(URLUtils.baseUrl + item.url))
                                }
                            } else {
                                setDownloadButtonVisibilityForMedia(MediaType.Audio, DownloadEnum.DOWNLOADING)
                                playAudio(Uri.parse(URLUtils.baseUrl + item.url))
                            }
                        }
                }
            }
            MediaType.Video -> {
                DataProvider.threadBlock {
                    val downloadItem = DataProvider.application?.database?.getManageDownloadDao()?.getItemByUrl(URLUtils.baseUrl + item.url)
                    activity?.runOnUiThread {
                        if (downloadItem == null) {
                            setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.NOT_DOWNLOADED)
                            playVideo(path = URLUtils.baseUrl + item.url)
                        } else if (downloadItem.isDownloaded == true) {
                            if (File(downloadItem.filePath ?: "").exists()) {
                                setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADED)
                                playVideo(path = downloadItem.filePath, localFile = true)
                            } else {
                                DataProvider.threadBlock {
                                    DataProvider.application?.database?.getManageDownloadDao()?.deleteItems(arrayListOf(downloadItem))
                                }
                                setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.NOT_DOWNLOADED)
                                playVideo(path = URLUtils.baseUrl + item.url)
                            }
                        } else {
                            setDownloadButtonVisibilityForMedia(MediaType.Video, DownloadEnum.DOWNLOADING)
                            playVideo(path = URLUtils.baseUrl + item.url)
                        }
                    }
                }
            }
            MediaType.Image -> item.url?.let {
                imageFrameLayout.visibility = View.VISIBLE
                displayImage(Uri.parse(URLUtils.baseUrl + it))
            }
            else -> {
            }
        }
    }

    private fun stopAudioPlayer() {
        if (audioPlayer1 != null) {
            audioPlayer1?.stop()
            audioPlayer1?.release()
            audioPlayer1 = null
        }
    }

    fun playAudio(uri: Uri, localAudio: Boolean = false) {
        if (NetworkAvailability.isNetworkAvailable(context!!) || localAudio) {
            scrollUp()
            imageFrameLayout.visibility = View.VISIBLE
            releasePlayer(false)
            stopAudioPlayer()
            playerView.visibility = View.GONE
            displayImageView.visibility = View.GONE
            initializeSeekBar()
            audioPlayerLayout.visibility = View.VISIBLE
            media_play.setImageResource(android.R.drawable.ic_media_pause)
            type = 1
            audioPlayer1 = MediaPlayer()
            audioPlayer1?.let {
                it.setDataSource(context!!, uri)
                it.setOnPreparedListener {
                    audioPlayer1?.start()
                    dismissProgressBar()
                    seekBar.max = audioPlayer1!!.duration
                    finalTime = audioPlayer1!!.duration
                    initializeSeekBar()
                }
                it.prepareAsync()
            }
        } else {
            showSnackBar(getResourceString("error_network_connection"))
        }
    }


    fun displayImage(uri: Uri) {
        releasePlayer(false)
        stopAudioPlayer()
        playerView.visibility = View.GONE
        displayImageView.visibility = View.VISIBLE
        audioPlayerLayout.visibility = View.GONE
        ImageUtils.loadImageToGlide(displayImageView, uri = uri)
    }

    //path is for play encrypted online and offline and uri is play media without encryption
    fun playVideo(uri: Uri? = null, path: String? = null, localFile: Boolean = false) {
        if (NetworkAvailability.isNetworkAvailable(context!!) || localFile) {
            scrollUp()
            imageFrameLayout.visibility = View.VISIBLE
            releasePlayer(false)
            stopAudioPlayer()
            playerView.visibility = View.VISIBLE
            displayImageView.visibility = View.GONE

            audioPlayerLayout.visibility = View.GONE
            if (uri == null) {
                videoPath = path
                initializePlayer(path = path)
            } else
                initializePlayer(uri)
        } else {
            showSnackBar(getResourceString("error_network_connection"))
        }

    }

    fun fileExist(fname: String): Boolean {
        var file = File(fname)
        return file.exists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        DataRepository.getServerTime(false)

    }

    fun initializeSeekBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (audioPlayer1 != null) {
                    val totalDuration = audioPlayer1?.duration
                    val currentDuration = audioPlayer1?.currentPosition
                    totalDurationTextView?.text = DateUtils.milliSecondsToTimer(totalDuration ?: 0)
                    currentPositiontTextView?.text = DateUtils.milliSecondsToTimer(currentDuration
                            ?: 0)
                    val mCurrentPosition = audioPlayer1?.currentPosition
                    seekBar?.progress = mCurrentPosition ?: 0
                    initializeSeekBar()
                }
            }
        }
        handler.postDelayed(runnable, 10)
    }

    private fun play() {
        audioPlayer1?.start()
        if (audioPlayer1?.currentPosition != null) {
            timeElapsed = audioPlayer1?.currentPosition
            seekBar?.progress = timeElapsed as Int
            initializeSeekBar()
        }
    }

    private fun pause() {
        audioPlayer1?.pause()
    }

    private fun stop() {
        if (audioPlayer1 != null) {
            audioPlayer1?.stop()
            audioPlayer1?.release()
            audioPlayer1 = null
        }
    }

    private fun forward() {
        var time = timeElapsed?.plus(forwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.plus(forwardTime)
                timeElapsed?.let { audioPlayer1?.seekTo(it) }
            }
        }
    }

    private fun backward() {
        var time = timeElapsed?.minus(backwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.minus(backwardTime)
                timeElapsed?.let { audioPlayer1?.seekTo(it) }
            }
        }
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


    private fun updateButtonVisibilities() {
        if (player == null) {
            return
        }

        val mappedTrackInfo = trackSelector?.currentMappedTrackInfo ?: return

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
                        Toast.makeText(this@SelectedRepositoryDetailsFragment.activity, getResourceString("unsupported_track"), Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

}

