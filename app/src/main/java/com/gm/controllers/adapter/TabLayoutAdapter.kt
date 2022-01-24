package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import java.util.*

class TabLayoutAdapter(var onItemClickListener: OnItemClickListener, var langaugeList: ArrayList<Model.Language>,
                       var context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return LanguageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_weather, parent, false))
    }

    override fun getItemCount(): Int {
        return langaugeList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

    }

    inner class LanguageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {

        }


    }
}