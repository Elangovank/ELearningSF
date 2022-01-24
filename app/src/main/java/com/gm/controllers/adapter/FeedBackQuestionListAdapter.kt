package com.gm.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.models.Model
import kotlinx.android.synthetic.main.item_check_list.view.*


class FeedBackQuestionListAdapter(var itemList: ArrayList<Model.QuestionListDetails>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_check_list, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemView.listTextView.text = item.question

        holder.itemView.listTextView.setOnClickListener {

            if (item.isSelected == false) {
                item.isSelected = true
            } else {
                item.isSelected = false
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.listTextView
        }
    }
}