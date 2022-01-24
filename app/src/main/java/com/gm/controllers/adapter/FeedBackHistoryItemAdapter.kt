package com.gm.controllers.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_feedback_history_list.view.*


class FeedBackHistoryItemAdapter(var feedBackList: ArrayList<Model.FeedBack>,
                                 private var onItemClickListener: OnItemClickListener, private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feedback_history_list, parent, false))
    }

    override fun getItemCount(): Int {
        return feedBackList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        holder.itemView.feedbackTypetextView.text = feedBackList[position].feedName
        if (feedBackList[position].iconDrawableId == R.drawable.ic_feedcomsumption)
        {
            holder.itemView.feedbackQuantityTypetextView.text=feedBackList[position].decimalCount
        }
        else{
            holder.itemView.feedbackQuantityTypetextView.text =
                    (if(feedBackList[position].iconDrawableId == R.drawable.ic_mortality)
                        (feedBackList[position].decimalCount?.toInt() ?: 0).toString() else
                        DateUtils.toRoundOff(feedBackList[position].decimalCount?.toDouble() ?: 0.0))
                            .plus(" "+feedBackList[position].feedBackCount)
        }


        holder.itemView.feedbackItemIcon.setImageResource(feedBackList[position].iconDrawableId)
    }


    fun setErrorMessage(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.feedbackItemIcon
            itemView.feedbackTypetextView
            itemView.feedbackQuantityTypetextView
        }
    }
}