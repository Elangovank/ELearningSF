package com.gm.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.DownloadAdapterListener

import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_download_list.view.*


class DownloadAdapter(private var downloadList: ArrayList<Model.DownloadQueue>, var onItemClickListener: DownloadAdapterListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_download_list, parent, false))
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        holder.itemView.title.text=downloadList[position].moduleType.toString()
        holder.itemView.title.append(" - ")
        holder.itemView.title.append((downloadList[position].title?:""))
        holder.itemView.percentageView.text=(downloadList[position].downloadProgress).toString()
        holder.itemView.progressBar.progress=downloadList[position].downloadProgress!!.toInt()
        if (downloadList.get(position).isDownloaded==true) {
            holder.itemView.cancelDownloadImageView.visibility=View.GONE
            holder.itemView.percentageLayout.visibility=View.GONE
            holder.itemView.dateTextView.visibility=View.VISIBLE
            holder.itemView.dateTextView.text = DateUtils.toDisplayDate(downloadList[position].downloadDate)
        }else{
            holder.itemView.percentageLayout.visibility=View.VISIBLE
            holder.itemView.dateTextView.visibility=View.GONE
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.title
            itemView.percentageView
            itemView.progressBar
            itemView.percentageLayout
            itemView.dateTextView
            itemView.cancelDownloadImageView.setOnClickListener {
                onItemClickListener.onItemSelected(downloadList.get(adapterPosition),adapterPosition,isCancel = true)
            }
            itemView.setOnClickListener {
                onItemClickListener.onItemSelected(downloadList.get(adapterPosition),adapterPosition,isCancel = false)
            }

        }
    }
}