package com.gm.controllers.adapter

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.fragments.ActivityFragment
import com.gm.db.SingleTon
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import kotlinx.android.synthetic.main.default_fragment_bottom_sheet_item.view.*
import kotlinx.android.synthetic.main.item_default_activity.view.*
import kotlinx.android.synthetic.main.item_today_activities.view.*


class ActivityAdapter(var nameList: ArrayList<Model.ActivityList>, var mContext: Context,
                      private var onItemClickListener: OnItemClickListener, var isPending: Boolean? = null, val resources: Resources,var fargment:ActivityFragment?=null)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    companion object {
        var isPendingFlag: Boolean = true
        //private var resourceString= SingleTon.readThefile()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_today_activities, parent, false))
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val item = nameList[position]
        holder.itemView.mediaRecyclerView?.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        holder.itemView.commentGMTextInputLayout?.hint=fargment?.getResourceString("comment")
        holder.itemView.offline?.text=fargment?.getResourceString("lable_offline")
        if (isPending == false) {
            holder.itemView.history_status.visibility = View.VISIBLE
            if (item.isCompleted == true) {
                holder.itemView.history_status.text =fargment?.getResourceString("completed")
                holder.itemView.history_status.setTextColor(resources.getColor(R.color.text_completed))
                holder.itemView.history_status.setBackgroundResource(R.drawable.background_completed)
            } else {
                holder.itemView.history_status.text = fargment?.getResourceString("incomplete")
                holder.itemView.history_status.setTextColor(resources.getColor(R.color.text_pending))
                holder.itemView.history_status.setBackgroundResource(R.drawable.background_pending)
            }
        } else {
            holder.itemView.history_status.visibility = View.GONE
        }

        DataProvider.threadBlock {
            DataProvider.application?.database?.addActivityDetailsDao()?.getActivityById(item.activityId
                    ?: 0)?.let {
                mContext.applicationContext?.let {
                    if (mContext.applicationContext is HomeActivity) {
                        (mContext.applicationContext as HomeActivity).runOnUiThread {
                            holder.itemView.offline?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        val list = ArrayList<Model.Media>()
        item.videos.let {
            list.addAll(it)
        }
        item.images.let {
            list.addAll(it)
        }
        item.audios.let {
            list.addAll(it)
        }
        item.pdfs.let {
            list.addAll(it)
        }

        if (list.size != 0) {
            val adapter = MediaAdapter(list, mContext, object : OnItemClickListener {
                override fun onItemSelected(item: Any?, selectedIndex: Int) {
                    onItemClickListener.onItemSelected(nameList[position], -1)
                }
            }, 0)
            if (isPending!!) {
                holder.itemView.mediaRecyclerView?.adapter = adapter
                holder.itemView.mediaRecyclerView.visibility = View.VISIBLE
            } else {
                holder.itemView.mediaRecyclerView.visibility = View.GONE

            }
        } else {
            holder.itemView.mediaRecyclerView.visibility = View.GONE
        }

        holder.itemView.daysTextView.text = if (item.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) fargment?.getResourceString("shed_ready_activity_label")
                ?: "-" else fargment?.getResourceString("day_label")?.format((item.activityAge
                ?: 0).toString())



        holder.itemView.title.text = item.title ?: "-"

        if (item.activityCategoryId == GMKeys.KEY_BASEACTIVITY) {
            holder.itemView.activityType.visibility = View.VISIBLE
            holder.itemView.activityType.text =fargment?.getResourceString("special_activity")
        } else if (item.activityCategoryId == GMKeys.KEY_DEFAULT_ACTIVITY) {
            holder.itemView.activityType.text = fargment?.getResourceString("regular_activity")
            holder.itemView.activityType.visibility = View.VISIBLE
        } else if (item.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) {
            holder.itemView.activityType.visibility = View.GONE
        }

        holder.itemView.clickButton.setOnClickListener {
            onItemClickListener.onItemSelected(item, -1)
        }

        holder.itemView.daysTextView?.setCompoundDrawablesWithIntrinsicBounds(if (item.activityCategoryId == GMKeys.KEY_SHED_READY_ACTIVITY) R.drawable.ic_shed else R.drawable.ic_day, 0, 0, 0)

    }

    fun updateList(list: ArrayList<Model.ActivityList>, isPendingList: Boolean) {
        nameList = list
        isPending = isPendingList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            isPendingFlag = isPending ?: true
            /*itemView.dailyOrWeeklyTextView*/
            itemView.title
            itemView.daysTextView
            itemView.history_status
            itemView.quantityEditText
            itemView.description
            itemView.radioRecyclerView
            itemView.rangeLayout
            itemView.activityType
            itemView.mediaRecyclerView
            itemView.default_activity_layout
            itemView.clickButton
            itemView.offline
            itemView.commentGMTextInputLayout
        }
    }


}
