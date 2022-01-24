package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import kotlinx.android.synthetic.main.item_language.view.*
import java.util.*

class LanguageAdapter(var onItemClickListener: OnItemClickListener, var langaugeList: ArrayList<Model.Language>, var context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return LanguageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false))
    }

    override fun getItemCount(): Int {
        return langaugeList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageViewHolder) {
            holder.bind(langaugeList[position])
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemSelected(langaugeList[position].isActive, position)
            }
        }
    }

    inner class LanguageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.languageName
            itemView.doneIcon
        }

        fun bind(language: Model.Language) {
            itemView.languageName.text = language.language
            if (language.isActive==true) {
                itemView.languageName.setTextColor(ContextCompat.getColor(itemView.languageName.context,R.color.text_color))
                itemView.doneIcon.visibility = View.VISIBLE
            } else {
                itemView.doneIcon.visibility = View.INVISIBLE
                itemView.languageName.setTextColor(ContextCompat.getColor(itemView.languageName.context,R.color.text_gray_light_shade1))
            }
        }
    }
}