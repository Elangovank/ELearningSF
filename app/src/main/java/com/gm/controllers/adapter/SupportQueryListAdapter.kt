package com.gm.controllers.adapter

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import kotlinx.android.synthetic.main.item_media_list.view.*
import kotlinx.android.synthetic.main.item_selected_support.view.*
import kotlinx.android.synthetic.main.layout_no_record.view.*
import java.util.*
import kotlin.collections.ArrayList

class SupportQueryListAdapter(private var optionList: ArrayList<Model.SupportQueries>, val context: Context, var onItemClickListener: OnItemClickListener, var resources: Resources) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_FOUND = 1
    private val TYPE_ERROR = 2

    private var imageAdapter: SupportMediaTypeAdapter? = null
    private var audioAdapter: SupportMediaTypeAdapter? = null
    private var videoAdapter: SupportMediaTypeAdapter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOUND) {
            return QueriesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_support, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_record, parent, false)
            ErrorViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if (optionList.size != 0) {
            return optionList.size
        } else {
            return 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (optionList.size != 0) {
            TYPE_FOUND
        } else {
            TYPE_ERROR
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QueriesViewHolder) {
            val item = optionList[position]

            holder.itemView.userNameLayout?.text = item.commentPostBy
            var displayTime = "-"
            item.commentedDate?.let {
                val time = com.gmcoreui.utils.DateUtils.convertStringToDate(it, com.gmcoreui.utils.DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)
                displayTime = DateUtils.getRelativeTimeSpanString(time.time, Date().time, 0).toString()
            }
            holder.itemView.timeLayout?.text = displayTime
            holder.itemView.contentWebView.loadData(Html.fromHtml(item.comment
                    ?: "").toString(), "text/html", "utf-8")
            holder.itemView.userImageView?.setImageResource(if (item.isFarmer == true) R.drawable.ic_farmer else R.drawable.ic_ets)


            val imageList = ArrayList<Model.SupportTicketsResponse>()
            val videoList = ArrayList<Model.SupportTicketsResponse>()
            val audioList = ArrayList<Model.SupportTicketsResponse>()
            item.mediaContent?.forEach {
                when (it.mediaType) {
                    GMKeys.AUDIO_ID -> audioList.add(it)
                    GMKeys.VIDEO_ID -> videoList.add(it)
                    GMKeys.IMAGE_ID -> imageList.add(it)
                }
            }
            toggleLayoutVisibility(GMKeys.IMAGE_ID, imageList, holder)
            toggleLayoutVisibility(GMKeys.VIDEO_ID, videoList, holder)
            toggleLayoutVisibility(GMKeys.AUDIO_ID, audioList, holder)

            initPhotoAdapter(imageList, GMKeys.IMAGE_ID, holder)
            initPhotoAdapter(videoList, GMKeys.VIDEO_ID, holder)
            initPhotoAdapter(audioList, GMKeys.AUDIO_ID, holder)

            /*  holder.itemView.setOnClickListener {
                  onItemClickListener.onItemSelected(item, position)
              }*/
        } else {
            holder.itemView.noDataLayout?.visibility = View.VISIBLE
        }
    }

    inner class QueriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.userImageView
            itemView.userNameLayout
            itemView.timeLayout
            itemView.contentWebView
            itemView.mediaLayoutView1
            itemView.pdfLinearLayout1?.visibility = View.GONE
        }
    }

    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.noDataLayout
        }
    }

    fun toggleLayoutVisibility(mediaType: Int, list: ArrayList<Model.SupportTicketsResponse>, holder: RecyclerView.ViewHolder) {
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

    private fun initPhotoAdapter(imageFile: ArrayList<Model.SupportTicketsResponse>, mediaType: Int, holder: RecyclerView.ViewHolder) {
        if (imageFile.size != 0)
            when (mediaType) {
                GMKeys.IMAGE_ID -> {
                    holder.itemView.photoLinearLayout1.visibility = View.VISIBLE
                    imageAdapter = SupportMediaTypeAdapter(imageFile, context!!, onItemClickListener)
                    holder.itemView.photosRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.photosRecyclerView1?.adapter = imageAdapter
                }
                GMKeys.VIDEO_ID -> {
                    holder.itemView.videoLinearLayout1.visibility = View.VISIBLE
                    videoAdapter = SupportMediaTypeAdapter(imageFile, context!!, onItemClickListener)
                    holder.itemView.videoRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.videoRecyclerView1?.adapter = videoAdapter
                }
                GMKeys.AUDIO_ID -> {
                    holder.itemView.audioLinearLayout1.visibility = View.VISIBLE
                    audioAdapter = SupportMediaTypeAdapter(imageFile, context!!, onItemClickListener)
                    holder.itemView.audioRecyclerView1?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.audioRecyclerView1?.adapter = audioAdapter
                }
            }
    }

}