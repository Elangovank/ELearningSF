package com.gm.controllers.adapter


import android.content.Context
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.FeedbackRatingFragment
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_media_list.view.*

import kotlinx.android.synthetic.main.item_report_history.view.*
import kotlinx.android.synthetic.main.layout_no_record.view.*


class FeedBackRatingHistoryAdapter(var feedBackList: ArrayList<Model.FeedBackRatingHistory>,
                                   private var onItemClickListener: OnItemClickListener, private var context: Context,var fragment: FeedbackRatingFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val TYPE_FOUND = 1
    private val TYPE_ERROR = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOUND) {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_report_history, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_record, parent, false)
            ErrorViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if (feedBackList.size != 0) {
            return feedBackList.size
        } else {
            return 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (feedBackList.size != 0) {
            TYPE_FOUND
        } else {
            TYPE_ERROR
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder){
        holder.itemView.pdfLinearLayout1?.visibility = View.GONE
        /* feedBackList.get(position).proofs?.forEach {
             when (it.mediaType) {
                 GMKeys.AUDIO_ID -> audioList.add(it)
                 GMKeys.VIDEO_ID -> videoList.add(it)
                 GMKeys.IMAGE_ID -> imageList.add(it)
             }
         }*/
        holder.itemView.expandView.setOnClickListener {
            if (holder.itemView.reportLayout.isVisible) {
                holder.itemView.reportLayout.visibility = View.GONE
            } else {
                val audioList = ArrayList((feedBackList.get(position).proofs?.filter { it.mediaType == GMKeys.AUDIO_ID }
                        ?: arrayListOf<Model.SupportTicketsResponse>()))
                val videoList = ArrayList((feedBackList.get(position).proofs?.filter { it.mediaType == GMKeys.VIDEO_ID }
                        ?: arrayListOf<Model.SupportTicketsResponse>()))
                val imageList = ArrayList((feedBackList.get(position).proofs?.filter { it.mediaType == GMKeys.IMAGE_ID }
                        ?: arrayListOf<Model.SupportTicketsResponse>()))
                holder.itemView.reportLayout.visibility = View.VISIBLE
                initPhotoAdapter(audioList, GMKeys.AUDIO_ID, holder)
                initPhotoAdapter(videoList, GMKeys.VIDEO_ID, holder)
                initPhotoAdapter(imageList, GMKeys.IMAGE_ID, holder)
                toggleLayoutVisibility(audioList, GMKeys.AUDIO_ID, holder)
                toggleLayoutVisibility(videoList, GMKeys.VIDEO_ID, holder)
                toggleLayoutVisibility(imageList, GMKeys.IMAGE_ID, holder)

                if (audioList.size == 0 && videoList.size == 0 && imageList.size == 0) {
                    holder.itemView.mediaAdded.visibility = View.GONE
                } else {
                    holder.itemView.mediaAdded.visibility = View.VISIBLE
                }
            }
        }




        context?.let {

            var feedBackList1 = ArrayList<Model.FeedBackRating>()
            if (context != null) {
                feedBackList1.add(Model.FeedBackRating(R.drawable.ic_chick_bird_feedback,fragment.getResourceString("chick_bird"),fragment.getResourceString("chick_bird")
                        , feedBackList.get(position).chick_Bird, feedBackList.get(position).chick_Bird_Questions))
                feedBackList1.add(Model.FeedBackRating(R.drawable.ic_feed_feedback, fragment.getResourceString("feed"), fragment.getResourceString("feed"), feedBackList.get(position).feed, feedBackList.get(position).feed_Questions)
                )
                feedBackList1.add(Model.FeedBackRating(R.drawable.ic_medicine_feedback, fragment.getResourceString("medicine"), fragment.getResourceString("medicine"), feedBackList.get(position).medicine, feedBackList.get(position).medicine_Questions)
                )
                feedBackList1.add(Model.FeedBackRating(R.drawable.ic_support_center_feedback, fragment.getResourceString("title_farmer"),
                        fragment.getResourceString("title_farmer"), feedBackList.get(position).supportCenter, feedBackList.get(position).supportCenter_Questions)
                )

            }
            holder.itemView.historyRecyclerView?.layoutManager = LinearLayoutManager(context)
            holder.itemView.historyRecyclerView?.adapter = FeedBackRatingHistoryItemAdapter(feedBackList1, onItemClickListener, context!!)


            //  holder.itemView.updateTimeTextView.text=con
            if (!feedBackList.get(position).reason.isNullOrEmpty()) {
                holder.itemView.reasonHistoryEditText.visibility = View.VISIBLE
                holder.itemView.reasonHistoryEditText?.setText(feedBackList.get(position)?.reason)
            } else {
                holder.itemView.reasonHistoryEditText.visibility = View.GONE
            }
        }

            var ti=DateUtils.toDisplayTimeMaterialAM(feedBackList.get(position).reportedDate)
            if (ti.trim().equals("AM"))
            {
                holder.itemView.dateTextView.text = DateUtils.toDisplayDate(feedBackList.get(position).reportedDate)
                        .plus(" ").plus(DateUtils.toDisplayTimeMaterial(feedBackList.get(position).reportedDate)).plus(fragment?.getResourceString("am"))
            }else{
                holder.itemView.dateTextView.text = DateUtils.toDisplayDate(feedBackList.get(position).reportedDate)
                        .plus(" ").plus(DateUtils.toDisplayTimeMaterial(feedBackList.get(position).reportedDate)).plus(fragment?.getResourceString("am"))
            }



        }else if(holder is ErrorViewHolder){
            holder.itemView.noDataLayout.visibility=View.VISIBLE
        }

    }


    private fun initPhotoAdapter(imageFile: ArrayList<Model.SupportTicketsResponse>, mediaType: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        if (imageFile.size != 0)
            when (mediaType) {
                GMKeys.IMAGE_ID -> {
                    holder.itemView.photoLinearLayout1.visibility = View.VISIBLE
                    holder.itemView.photosRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.photosRecyclerView1?.adapter = SupportMediaTypeAdapter(imageFile, context, onItemClickListener)
                }
                GMKeys.VIDEO_ID -> {
                    holder.itemView.videoLinearLayout1.visibility = View.VISIBLE
                    holder.itemView.videoRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.videoRecyclerView1?.adapter = SupportMediaTypeAdapter(imageFile, context, onItemClickListener)
                }
                GMKeys.AUDIO_ID -> {
                    holder.itemView.audioLinearLayout1.visibility = View.VISIBLE
                    holder.itemView.audioRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.audioRecyclerView1?.adapter = SupportMediaTypeAdapter(imageFile, context, onItemClickListener)
                }
            }
    }


    private fun toggleLayoutVisibility(list: ArrayList<Model.SupportTicketsResponse>, mediaType: Int, holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        when (mediaType) {
            GMKeys.AUDIO_ID -> {
                if (list.size == 0) {
                    holder.itemView.audioLinearLayout1?.visibility = View.GONE
                } else {
                    holder.itemView.audioLinearLayout1?.visibility = View.VISIBLE
                }
            }
            GMKeys.VIDEO_ID -> {
                if (list.size == 0) {
                    holder.itemView.videoLinearLayout1?.visibility = View.GONE
                } else {
                    holder.itemView.videoLinearLayout1?.visibility = View.VISIBLE
                }
            }
            GMKeys.IMAGE_ID -> {
                if (list.size == 0) {
                    holder.itemView.photoLinearLayout1?.visibility = View.GONE
                } else {
                    holder.itemView.photoLinearLayout1?.visibility = View.VISIBLE
                }
            }
        }
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {

            itemView.historyRecyclerView
            itemView.dateTextView
            itemView.reasonHistoryEditText
            itemView.photoLinearLayout1
            itemView.videoLinearLayout1
            itemView.audioLinearLayout1
            itemView.pdfLinearLayout1
            itemView.mediaAdded

        }
    }


    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.noDataLayout
        }
    }

}