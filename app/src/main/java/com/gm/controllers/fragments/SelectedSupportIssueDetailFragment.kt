package com.gm.controllers.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.GMApplication
import android.content.Context
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.URLUtils
import com.gm.controllers.activities.FullScreenVideoActivity
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.SelectedQuestionAdapter
import com.gm.controllers.adapter.SupportQueryListAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.ImageUtils
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.decoration.DividerItemDecoration
import com.gmcoreui.utils.DateUtils
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_raised_request_details.*
import kotlinx.android.synthetic.main.fragment_raised_request_details.issueDetailLayout
import kotlinx.android.synthetic.main.fragment_raised_request_details.questionRecyclerView
import kotlinx.android.synthetic.main.item_audio_player.view.*


class SelectedSupportIssueDetailFragment : GMBaseFragment(), OnItemClickListener {
    var supportDetail = Model.SupportTicketDetails()

    var finalTime = 0
    private var isInitialStartAndResume = true
    private var audioPlayer: MediaPlayer? = null
    var playOrPauseAudioType = 0
    var timeElapsed: Int? = 0
    var forwardTime = 2000
    var backwardTime = 2000

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    companion object {
        fun newInstance(args: Bundle): SelectedSupportIssueDetailFragment {
            val fragment = SelectedSupportIssueDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_raised_request_details, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        // context= getContext() as? Context
        arguments?.getLong(RaisedRequestListFragment.SUPPORT_TICKET_ID).let {
            getIssueDetails(it!!)
        }
    }

    fun setResourceString()
    {
        requestIdTextView?.text=getResourceString("request_id")
        questionRaised?.text=getResourceString("questions_raised")
    }


    private fun initQuestionAdapter() {
        context?.let {
            questionRecyclerView?.layoutManager = LinearLayoutManager(context)
            questionRecyclerView?.addItemDecoration(DividerItemDecoration(context!!, LinearLayout.HORIZONTAL))
            questionRecyclerView?.adapter = SelectedQuestionAdapter(supportDetail.farmerQuestions!!)
        }
    }

    private fun getIssueDetails(id: Long) {
        showProgressBar()
        DataProvider.getIssueListDetails(id, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.SupportIssueListDetailsResponse) {
                    responseObject.response?.let {
                        supportDetail = it
                    }
                }
                initView()
                supportDetail.supportSubCategory?.let { initToolbar(R.menu.menu_add_queries, it) }
                dismissProgressBar()
            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()
                showErrorSnackBar(responseObject)
            }
        })

    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.SupportTicketsResponse) {
            if (item.mediaType == GMKeys.AUDIO_ID) {
                showConfirmationDialog(Uri.parse(item.mediaLocation), item.mediaLocation!!, 1)
            } else if (item.mediaType == GMKeys.VIDEO_ID) {
                item.mediaLocation
                val intent = Intent(context, FullScreenVideoActivity::class.java)
                intent.putExtra("VideoURL", URLUtils.baseUrl + item.mediaLocation)
                intent.putExtra("ResumePosition", 0L)
                intent.putExtra("ResumeWindow", 0)
                startActivity(intent)
            } else {
                item.mediaLocation
                showConfirmationDialog(Uri.parse(item.mediaLocation), item.mediaLocation!!, 3)
            }
        }
    }

    @SuppressLint("Recycle")
    fun initView() {
        context?.let {
            issueDetailLayout?.visibility = View.VISIBLE
            subCatagoryId?.text = supportDetail.supportTicketId.toString()
            val iconList = context?.resources?.obtainTypedArray(R.array.supportCategory)
            subCatagoryIcon?.setCompoundDrawablesWithIntrinsicBounds(iconList?.getDrawable(supportDetail.supportCategoryId?.toInt()
                    ?: 0), null, null, null)

            val tamilString =ArrayList<String>()
            tamilString.add(getResourceString("chick_bird"))
            tamilString.add(getResourceString("feed_stock"))
            tamilString.add(getResourceString("medicine"))
            tamilString.add(getResourceString("marketting"))
            tamilString.add(getResourceString("management"))
            tamilString.add(getResourceString("others"))
            supportName?.setText(supportDetail.supportCategoryId?.minus(1)?.let { tamilString.get(
                    it.toInt()) })
            supportNameTextView?.setText(supportDetail.supportCategoryId?.minus(1)?.let {
                tamilString.get(it.toInt()) })
            initAdapter()
            initQuestionAdapter()
        }


    }

    private fun initAdapter() {
        if (queriesRecyclerView?.adapter == null) {
            context?.let {
                queriesRecyclerView?.layoutManager = LinearLayoutManager(context)
                queriesRecyclerView?.adapter = SupportQueryListAdapter(supportDetail.commentsList
                        ?: arrayListOf(), context!!, this, resources)
            }
        } else {
            queriesRecyclerView?.adapter?.notifyDataSetChanged()
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
        showProgressBar()
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add -> {
                val bundle = Bundle()
                bundle.putLong(AddCommentFragment.ARG_SUPPORT_ID, supportDetail.supportTicketId
                        ?: 0L)
                (activity as HomeActivity).replaceFragment(AddCommentFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

}
