package com.gm.controllers.activities

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gm.R
import com.gm.utilities.GMKeys
import com.gm.utilities.MyCipherFactory
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import fr.maxcom.http.LocalSingleHttpServer
import kotlinx.android.synthetic.main.full_scree_video.*


class FullScreenVideoActivity : AppCompatActivity() {

    private val STATE_RESUME_WINDOW = "resumeWindow"
    private val STATE_RESUME_POSITION = "resumePosition"
    private val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
    var mServer: LocalSingleHttpServer? = null
    private var mVideoSource: MediaSource? = null
    private var mExoPlayerFullscreen = false
    private var mFullScreenButton: FrameLayout? = null
    private var mFullScreenIcon: ImageView? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private var mResumeWindow: Int = 0
    private var mResumePosition: Long = 0
    var player: SimpleExoPlayer? = null
    private var path: String? = null
    var isEncryptedVideo:Boolean=false
    companion object {
        private var uri: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_scree_video)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        uri = intent.getStringExtra("VideoURL")

        path= intent.getStringExtra("VideoPath")
        mResumePosition = intent.getLongExtra("ResumePosition", 0L)
        mResumeWindow = intent.getIntExtra("ResumeWindow", 0)
        mediaDataSourceFactory = DefaultDataSourceFactory(applicationContext, Util.getUserAgent(applicationContext, GMKeys.mediaPlayerSample), DefaultBandwidthMeter() as TransferListener<in DataSource>)
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW)
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION)
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow)
        outState.putLong(STATE_RESUME_POSITION, mResumePosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen)
        super.onSaveInstanceState(outState)
    }


    private fun closeFullscreenDialog() {
        val intent = intent
        intent.putExtra("URL", uri)
        intent.putExtra("RESUME_POSITION", exoplayer!!.player.contentPosition)
        intent.putExtra("CURRENT_INDEX", mResumeWindow)
        setResult(Activity.RESULT_OK, intent)
        finish()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    private fun initFullscreenButton() {
        val controlView = exoplayer!!.findViewById<View>(R.id.exo_controller)
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon)
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button)
        mFullScreenButton!!.setOnClickListener {
            closeFullscreenDialog()
        }
        var mExoDownloadButton = controlView.findViewById<View>(R.id.exo_download_button)
        mExoDownloadButton.visibility = View.GONE
    }


    private var trackSelector: DefaultTrackSelector? = null

    private fun initExoPlayer() {


        if (player == null) {
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
            trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            lastSeenTrackGroupArray = null
            player = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector)
            exoplayer.player = player
            player?.playWhenReady = true
            //player?.seekTo(currentWindow, playbackPosition)
            player?.let {
                with(player!!) {
                    addListener(PlayerEventListener())
                    playWhenReady = true
                }
            }
            val mediaSource =  if (uri==null) buildMediaSource(path=path) else buildMediaSource(Uri.parse(uri))
            val haveStartPosition = mResumeWindow != C.INDEX_UNSET
            if (haveStartPosition) {
                player?.seekTo(mResumeWindow, mResumePosition)
            }
            player?.prepare(mediaSource, !haveStartPosition, false)
        }
    }

    //path is for play encrypted online and offline and uri is play media without encryption
    private fun buildMediaSource(uri: Uri?=null,path: String?=null): MediaSource {
        mServer?.stop()
        mServer = LocalSingleHttpServer()
        mServer?.setCipherFactory(MyCipherFactory())
        mServer?.start()

        /*  val userAgent = "exoplayer-codelab"
          if (uri.lastPathSegment.contains("mp3") || uri.lastPathSegment.contains("mp4")) {*/
        return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                .createMediaSource(  if (uri==null)
                    Uri.parse( mServer?.getURL(path))
                else
                    uri)
        /*  } else if (uri.lastPathSegment.contains("m3u8")) {
              return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                      .createMediaSource(uri)
          } else {
              return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                      .createMediaSource(uri)
          }*/
    }
/*    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent = "exoplayer-codelab"
        if (uri.lastPathSegment.contains("mp3") || uri.lastPathSegment.contains("mp4")) {
            return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(uri)
        } else if (uri.lastPathSegment.contains("m3u8")) return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri) else {
            return ExtractorMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(uri)
        }
    }*/

    override fun onPause() {
        super.onPause()
        releeseExoPlayer()

    }

    private fun releeseExoPlayer() {
        if (exoplayer != null && exoplayer!!.player != null) {
            mResumeWindow = exoplayer!!.player.currentWindowIndex
            mResumePosition = Math.max(0, exoplayer!!.player.contentPosition)
            player?.stop()
            player?.release()
            player = null
        }
    }

    private var lastSeenTrackGroupArray: TrackGroupArray? = null

    private inner class PlayerEventListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> fullscreen_progress_bar?.visibility = View.VISIBLE     // The player does not have any media to play yet.
                Player.STATE_BUFFERING -> fullscreen_progress_bar?.visibility = View.VISIBLE // The player is buffering (loading the content)
                Player.STATE_READY -> fullscreen_progress_bar?.visibility = View.GONE   // The player is able to immediately play
                Player.STATE_ENDED -> fullscreen_progress_bar?.visibility = View.GONE    // The player has finished playing the media
            }

        }

        override fun onSeekProcessed() {
            super.onSeekProcessed()
            Log.e("call back", "Progress seek bar")
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            // The video tracks are no supported in this device.
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector!!.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(applicationContext, "Error unsupported track", Toast.LENGTH_SHORT).show()
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }

    }


    override fun onStop() {
        releeseExoPlayer()
        super.onStop()
    }

    override fun onDestroy() {
        releeseExoPlayer()
        super.onDestroy()
    }

    override fun onStart() {
        if (true) {
            initExoPlayer()
        }
        super.onStart()
    }


    override fun onResume() {
        super.onResume()
        initFullscreenButton()
       /* if (uri == null) {
            uri = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8"
        }*/
        initExoPlayer()
        mFullScreenIcon!!.setImageDrawable(ContextCompat.getDrawable(this@FullScreenVideoActivity, R.drawable.ic_fullscreen_skrink))


    }


}
