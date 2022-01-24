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
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.item_media_view.view.*

class MediaTypeActivityAdapter(var mediaList: ArrayList<Model.Media>, var context: Context,
                               private var onItemClickListener: OnItemClickListener, var type: MediaType, var status: Boolean = false, private var deleteType: Int = -1)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_view, parent, false))
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val item = mediaList.get(position)
        when (type) {
            MediaType.Image -> {
                holder.itemView.playImageView.visibility = View.GONE
                ImageUtils.loadImageToGlide(holder.itemView.mediaImageView,item.url)
            }
            MediaType.Audio -> {
                holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder_audio))
            }
            MediaType.Video -> {
                holder.itemView.mediaImageView.setImageResource(R.drawable.placeholder_video)
               // ImageUtils.loadVideoToGlide(holder.itemView.mediaImageView,item.url)
            }
            MediaType.Pdf->{
                holder.itemView.playImageView.visibility = View.GONE
                holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder_pdf))
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