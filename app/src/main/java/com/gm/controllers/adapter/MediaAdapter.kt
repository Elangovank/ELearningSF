package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.item_media_view.view.*

class MediaAdapter(var nameList: ArrayList<Model.Media>, var cmContext: Context,
                   private var onItemClickListener: OnItemClickListener, var flag: Int? = 0)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var selectedMenuIndex: Int = 0
    val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_view, parent, false))
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        val item = nameList.get(position)
        holder.itemView.playImageView.visibility = View.GONE
        if (item.mediaType == MediaType.Image) {
            ImageUtils.loadImageToGlide(holder.itemView.mediaImageView, item.url)
        } else if (item.mediaType == MediaType.Video) {
          //  ImageUtils.loadVideoToGlide(holder.itemView.mediaImageView, item.url)
            holder.itemView.mediaImageView.setImageResource(R.drawable.placeholder_video)
            holder.itemView.playImageView.visibility = View.GONE
        } else if (item.mediaType == MediaType.Audio){
            holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(cmContext, R.drawable.placeholder_audio))
        }
        else{
            holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(cmContext, R.drawable.placeholder_pdf))
        }
        if (flag == 1) {
            if (item.isSelected == true) {
                holder.itemView.media_frame_layout.background = cmContext!!.resources.getDrawable(R.drawable.select_media_bg)

                params.setMargins(0, 0, 0, 40)
                holder.itemView.media_frame_layout.layoutParams = params

            } else {
                holder.itemView.media_frame_layout.background = cmContext!!.resources.getDrawable(R.drawable.select_media_transperent_bg)
                params.setMargins(0, 40, 0, 0)
                holder.itemView.media_frame_layout.layoutParams = params
            }
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemSelected(item, position)
            }
        }
    }

    fun selectedMenu(position: Int) {
        selectedMenuIndex = position
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.mediaImageView
            itemView.playImageView
            itemView.media_frame_layout
        }
    }
}