package com.gm.controllers.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_rating_history.view.*


class FeedBackRatingHistoryItemAdapter(var feedBackList: ArrayList<Model.FeedBackRating>,
                                       private var onItemClickListener: OnItemClickListener, private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rating_history, parent, false))
    }

    override fun getItemCount(): Int {
        return feedBackList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        holder.itemView.feedbackImageView.setImageResource(feedBackList[position].iconDrawableId)
        holder.itemView.feedNameTextView.text=feedBackList[position].feedName.toString()
        holder.itemView.starCountTextView.text=feedBackList[position].count.toString()
        holder.itemView.questionListRecyclerView?.layoutManager = LinearLayoutManager(context)
        holder.itemView.questionListRecyclerView?.adapter = feedBackList.get(position).list?.let { SelectedQuestionAdapter(it) }
    }


    fun setErrorMessage(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.feedbackImageView
            itemView.starCountTextView
            itemView.feedNameTextView
        }
    }
}