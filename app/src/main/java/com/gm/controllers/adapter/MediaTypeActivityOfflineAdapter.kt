package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Resource
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.item_media_view.view.*

class MediaTypeActivityOfflineAdapter(var mediaList: ArrayList<Model.OfflineMediaActivity>, var context: Context,
                                      private var onItemClickListener: OnItemClickListener, var type: MediaType)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_view, parent, false))
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val item = mediaList.get(position)
        when (item.mediaTypeId) {
            GMKeys.IMAGE_ID -> {
                holder.itemView.playImageView.visibility = View.GONE
                ImageUtils.loadImageToGlide(holder.itemView.mediaImageView,item.url)
            }
            GMKeys.AUDIO_ID-> {
                holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder_audio))
            }
            GMKeys.VIDEO_ID -> {
                ImageUtils.loadVideoToGlide(holder.itemView.mediaImageView,item.url)
            }

        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemSelected(item, position)
        }


    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.mediaImageView
            itemView.playImageView
            itemView.selectImageView
        }
    }
}