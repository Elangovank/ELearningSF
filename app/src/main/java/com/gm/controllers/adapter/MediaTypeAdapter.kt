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
import com.gm.models.ModuleType
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.item_media_view.view.*

class MediaTypeAdapter(var mediaList: ArrayList<Model.UploadMedia>, var context: Context,
                       private var onItemClickListener: OnItemClickListener, var type: MediaType, var status: Boolean = false, private var deleteType: Int = -1,var moduleType:ModuleType?=null)
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
                if (deleteType == 1) {
                    ImageUtils.loadImageToGlide(holder.itemView.mediaImageView, url = item.url)
                } else {
                    ImageUtils.loadImageToGlide(holder.itemView.mediaImageView, uri = item.mediaUri)
                }
            }
            MediaType.Audio -> {
                holder.itemView.mediaImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_audio))
            }
            MediaType.Video -> {
                if (deleteType == 1) {
                    if (moduleType!=null){
                        if (moduleType==ModuleType.Repository)holder.itemView.mediaImageView.setImageResource(R.drawable.placeholder_video)
                    }else{
                        ImageUtils.loadVideoToGlide(holder.itemView.mediaImageView, url = item.url)
                    }
                } else {
                    ImageUtils.loadVideoToGlide(holder.itemView.mediaImageView, uri = item.mediaUri)
                }
            }
        }

        holder.itemView.mediaLayouts.setOnClickListener {
            if ( item.status == 2 || item.status == 0 || item.status == 1) {
                if (holder.itemView.selectImageView.visibility == View.VISIBLE) {
                    item.status = 0
                    holder.itemView.selectImageView.visibility = View.GONE
                    holder.itemView.mediaImageView.setColorFilter(context.resources.getColor(R.color.transperent))
                } else {
                    item.status = 1
                    holder.itemView.selectImageView.visibility = View.VISIBLE
                    holder.itemView.mediaImageView.setColorFilter(context.resources.getColor(R.color.delete_selector))
                }
            }

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