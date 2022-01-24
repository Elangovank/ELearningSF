package com.gm.controllers.activities

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.utils.DateUtils
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import kotlinx.android.synthetic.main.activity_audio_capture.*
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*
import kotlin.collections.ArrayList


class AudioCaptureActivity : BaseActivity(), OnItemClickListener {

    private var myAudioRecorder: MediaRecorder? = null
    private var outputFile: String? = null
    private var destinationPlayFile: String? = null
    private var outputFileList = ArrayList<String>()
    var outputFileFolderPath: String? = null
    var destinationFileFolderPath: String? = null
    private var mediaPlayer: MediaPlayer? = null
    var AUDIO_CAPTURED = 2
    var audioDecision = 0
    var playDecision = 0
    var timeElapsed: Int? = 0
    var finalTime = 0

    var minute = 0
    var seconds = 0
    var timeWhenStopped: Long = 0
    override fun onItemSelected(item: Any?, selectedIndex: Int) {
    }

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }
   fun setReasourceString()
   {
       startTextView?.text=getResourceString("label_start")
       restartTextView?.text=getResourceString("label_re_start")
       recordCancelTextView?.text=getResourceString("cancel")
       cancelTextView?.text=getResourceString("cancel")
       nextTextView?.text=getResourceString("label_next")
       recordTextView?.text=getResourceString("record_again")
       uploadAudioTextView?.text=getResourceString("upload")
       uploadAudioCancelTextView?.text=getResourceString("cancel")
   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_capture)
        setRecordLayout()
        setReasourceString()
        startIamgeView.setOnClickListener {
            when (audioDecision) {
                0 -> {
                    toggleVisibility(true)
                    startIamgeView.setImageDrawable(this?.resources?.getDrawable(R.drawable.bt_pause))
                    startTextView?.text = getResourceString("pause")
                    nextTextView.visibility = View.GONE
                    audioDecision = 1
                    startAudioRecorder()
                    chronometer1.base = SystemClock.elapsedRealtime()
                    chronometer1.start()
                    reStartImageViw?.visibility = View.GONE
                    restartTextView?.visibility = View.GONE
                }
                1 -> {
                    startIamgeView.setImageDrawable(this?.resources?.getDrawable(R.drawable.bt_resume))
                    startTextView?.text = getResourceString("resume")
                    nextTextView.visibility = View.VISIBLE
                    audioDecision = 2
                    resumeAudioRecorder()
                    timeWhenStopped = chronometer1.base - SystemClock.elapsedRealtime();
                    chronometer1.stop()
                    reStartImageViw?.visibility = View.VISIBLE
                    restartTextView?.visibility = View.VISIBLE
                }
                2 -> {
                    startIamgeView.setImageDrawable(this?.resources?.getDrawable(R.drawable.bt_pause))
                    startTextView?.text =getResourceString("pause")
                    nextTextView.visibility = View.GONE
                    audioDecision = 1
                    startAudioRecorder()
                    chronometer1.base = SystemClock.elapsedRealtime() + timeWhenStopped
                    chronometer1.start()
                    reStartImageViw?.visibility = View.GONE
                    restartTextView?.visibility = View.GONE
                }
            }
        }
        chronometer1.setOnChronometerTickListener {
            val currentTime = chronometer1.text.toString()
            if (currentTime == "0${GMKeys.MEDIA_MINUTES}:00") {
                chronometer1.stop()
                startIamgeView?.visibility = View.GONE
                startTextView?.visibility = View.GONE
                nextTextView.isEnabled = true
                audioDecision = 2
                timeWhenStopped = chronometer1.base - SystemClock.elapsedRealtime();
                chronometer1.stop()
                playDecision = 0
                resumeAudioRecorder()
                toggleVisibility(true)
                nextTextView.visibility = View.VISIBLE
            }
        }
        reStartImageViw.setOnClickListener {
            stopAudioPlayer()
            setRecordLayout()
        }
        cancelTextView.setOnClickListener {
            resumeAudio()
            stopAudioPlayer()
            setRecordLayout()
        }
        recordCancelTextView.setOnClickListener {
            stopAudioPlayer()
            finish()
        }
        nextTextView.setOnClickListener {
            recordLayout.visibility = View.GONE
            playLayout.visibility = View.VISIBLE
            if (playDecision == 0) {
                try {
                    destinationPlayFile = destinationFileFolderPath + "/recording" + System.currentTimeMillis().toString() + "_destination" + ".mp3"
                    val file = File(destinationPlayFile)
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    destinationPlayFile?.let {
                        mergeMediaFiles(true, outputFileList, it)
                    }
                    deleteRecursive(File(outputFileFolderPath))
                    media_play.setImageResource(android.R.drawable.ic_media_pause)
                    playDecision = 1
                    mediaPlayer?.let {
                        mediaPlayer?.setDataSource(destinationPlayFile)
                        mediaPlayer?.setOnPreparedListener {
                            mediaPlayer?.start()
                            dismissProgressBar()
                            seekBar.max = mediaPlayer!!.duration
                            finalTime = mediaPlayer!!.duration
                            initializeSeekBar()
                        }
                        mediaPlayer?.prepareAsync()
                    }
                } catch (e: Exception) {
                }
            }
        }

        recordAgainTextView.setOnClickListener {
            stopAudioPlayer()
            setRecordLayout()
        }

        media_play.setOnClickListener {
            if (playDecision == 1) {
                try {
                    media_play.setImageResource(android.R.drawable.ic_media_pause)
                    playDecision = 0
                    playAudioPlayer()
                } catch (e: Exception) {
                    // make something
                }
            } else if (playDecision == 0) {
                try {
                    media_play.setImageResource(android.R.drawable.ic_media_play)
                    playDecision = 1
                    mediaPlayer?.pause()
                } catch (e: Exception) {
                    // make something
                }
            }
        }
        media_rew.setOnClickListener {
            backwardAudioPlayer()
        }
        media_forward.setOnClickListener {
            forwardAudioPlayer()
        }
        uploadAudioCancelTextView.setOnClickListener {
            setRecordLayout()
        }
        uploadAudioTextView.setOnClickListener {
            stopAudioPlayer()
            val intent = Intent()
            intent.putExtra("AUDIO", destinationPlayFile)
            setResult(AUDIO_CAPTURED, intent)
            finish()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mediaPlayer!!.isPlaying) {
                    resetAudioPlayer(seekBar.progress)
                } else {
                    resetAudioPlayerPosition(seekBar.progress)
                }
            }
        })
    }

    private fun resumeAudio() {
        startIamgeView.setImageDrawable(this?.resources?.getDrawable(R.drawable.bt_resume))
        startTextView?.text = getResourceString("resume")
        nextTextView.isEnabled = true
        audioDecision = 2
        resumeAudioRecorder()
        timeWhenStopped = chronometer1.base - SystemClock.elapsedRealtime();
        chronometer1.stop()
        reStartImageViw?.visibility = View.VISIBLE
        restartTextView?.visibility = View.VISIBLE
    }

    fun setRecordLayout() {
        playLayout.visibility = View.GONE
        recordLayout.visibility = View.VISIBLE
        startIamgeView?.visibility = View.VISIBLE
        startTextView?.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer()
        outputFileList = ArrayList<String>()
        playDecision = 0
        createFolder()
        toggleVisibility(false)
        startIamgeView.setImageDrawable(this.resources?.getDrawable(R.drawable.bt_start))
        startTextView?.text = getResourceString("label_start")
        chronometer1.stop()
        chronometer1?.text = "00:00"
        audioDecision = 0
        outputFile = ""
    }

    override fun onDestroy() {
        stopAudioPlayer()
        super.onDestroy()
    }

    private fun toggleVisibility(status: Boolean) {
        if (status) {
            recordCancelLayout.visibility = View.GONE
            reStartImageViw.visibility = View.VISIBLE
            restartTextView.visibility = View.VISIBLE
            reStartLayout.visibility = View.VISIBLE
        } else {
            recordCancelLayout.visibility = View.VISIBLE
            reStartImageViw.visibility = View.GONE
            restartTextView.visibility = View.GONE
            reStartLayout.visibility = View.GONE
        }
    }

    private fun startAudioRecorder() {
        try {
            setUpRecorder()
            myAudioRecorder?.prepare()
            myAudioRecorder?.start()
        } catch (ise: IllegalStateException) {
            // make something ...
            ise.printStackTrace()
        } catch (ioe: IOException) {
            // make something
            ioe.printStackTrace()
        }
    }

    private fun pauseAudioRecorder() {
        try {
            myAudioRecorder?.stop()
        } catch (ise: IllegalStateException) {
            // make something ...
            ise.printStackTrace()
        } catch (ioe: IOException) {
            // make something
            ioe.printStackTrace()
        }
    }

    private fun resumeAudioRecorder() {
        try {
            myAudioRecorder?.reset()
        } catch (ise: IllegalStateException) {
            // make something ...
            ise.printStackTrace()
        } catch (ioe: IOException) {
            // make something
            ioe.printStackTrace()
        }
    }

    private fun forwardAudioPlayer() {
        val time = timeElapsed?.plus(GMKeys.forwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.plus(GMKeys.forwardTime)
                timeElapsed?.let { mediaPlayer?.seekTo(it) }
            }
        }
    }

    private fun backwardAudioPlayer() {
        val time = timeElapsed?.minus(GMKeys.backwardTime)
        if (time != null) {
            if (time <= finalTime) {
                timeElapsed = timeElapsed?.minus(GMKeys.backwardTime)
                timeElapsed?.let { mediaPlayer?.seekTo(it) }
            }
        }
    }

    private fun stopAudioPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun playAudioPlayer() {
        mediaPlayer?.start()
        timeElapsed = mediaPlayer?.currentPosition
        seekBar?.progress = timeElapsed as Int
        initializeSeekBar()
    }

    private fun resetAudioPlayer(progress: Int) {
        mediaPlayer?.start()
        mediaPlayer?.seekTo(progress)
        seekBar?.progress = progress
        initializeSeekBar()
    }

    private fun resetAudioPlayerPosition(progress: Int) {
        mediaPlayer?.seekTo(progress)
        seekBar?.progress = progress
        val totalDuration = mediaPlayer!!.duration
        val currentDuration = mediaPlayer!!.currentPosition
        totalDurationTextView.text = DateUtils.milliSecondsToTimer(totalDuration)
        currentPositiontTextView.text = DateUtils.milliSecondsToTimer(currentDuration)
    }

    fun initializeSeekBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    val totalDuration = mediaPlayer?.duration
                    val currentDuration = mediaPlayer?.currentPosition
                    totalDurationTextView.text = DateUtils.milliSecondsToTimer(totalDuration ?: 0)
                    currentPositiontTextView.text = DateUtils.milliSecondsToTimer(currentDuration
                            ?: 0)
                    val mCurrentPosition = mediaPlayer?.currentPosition
                    seekBar?.progress = mCurrentPosition ?: 0
                    initializeSeekBar()
                }
            }
        }
        handler.postDelayed(runnable, 10)
    }


    private fun setUpRecorder() {

        outputFile = outputFileFolderPath + "/recording" + System.currentTimeMillis().toString() + outputFileList.size + ".mp4"
        outputFile?.let {
            outputFileList.add(it)
        }
        myAudioRecorder = MediaRecorder()
        myAudioRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        myAudioRecorder?.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        myAudioRecorder?.setOutputFile(outputFile)
    }

    private fun mergeMediaFiles(isAudio: Boolean, sourceFiles: ArrayList<String>, targetFile: String): Boolean {
        try {
            val mediaKey = if (isAudio) "soun" else "vide"
            val listMovies = ArrayList<Movie>()
            for (filename in sourceFiles) {
                try {
                    var movieCreated: Movie = MovieCreator.build(filename)
                    if (movieCreated != null)
                        listMovies.add(movieCreated)
                } catch (e: Exception) {
                    Log.d("#######MOVIE INNER", e.printStackTrace().toString())
                }
            }

            val listTracks = LinkedList<Track>()
            for (movie in listMovies) {
                for (track in movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track)
                    }
                }
            }
            val outputMovie = Movie()
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(AppendTrack(*listTracks.toTypedArray()))
            }
            val container = DefaultMp4Builder().build(outputMovie)
            val fileChannel = RandomAccessFile(String.format(targetFile), "rw").getChannel()
            container.writeContainer(fileChannel)
            fileChannel.close()
            return true
        } catch (e: IOException) {
            Log.i("#######MOVIE", e.printStackTrace().toString())
            return false
        }
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }else{
            fileOrDirectory.delete()
        }
    }

    fun createFolder() {
        destinationFileFolderPath = (this.externalCacheDir?.path?:"")+File.separator + "suguna_audio"
        val destinationFolder = File(destinationFileFolderPath)
        if (!destinationFolder.exists()) {
            destinationFolder.mkdir()
        }

        outputFileFolderPath = (this.externalCacheDir?.path?:"")+File.separator + "suguna_temp_audio"
        val outputFolder = File(outputFileFolderPath)
        if (!outputFolder.exists()) {
            outputFolder.mkdir()
        }
    }

    fun setTimer() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (myAudioRecorder != null) {
                    if (seconds >= 60) {
                        minute++
                        seconds = 0
                    }
                    MinuteTextView.text = minute.toString()
                    SecondsTextView.text = seconds.toString()
                    seconds++
                    setTimer()
                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }


}


