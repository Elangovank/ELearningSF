package com.gm.controllers.adapter

import android.content.res.TypedArray
import android.graphics.ColorSpace
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.NotificationFragment
import com.gm.listener.LastItemReachedListener
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.ImageUtils
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.android.synthetic.main.item_repository.view.*

class NotificationAdapter(private var notificationList: ArrayList<Model.Notificaiton>, private var unreadedCount: Int,
                          private var onItemClickListener: OnItemClickListener,
                          private var listener: LastItemReachedListener,
                             var fragment:NotificationFragment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        holder.itemView.unReadLayout.visibility = View.GONE
        var item = notificationList[position]
        var messageString = Html.fromHtml(item.message!!)
        if (messageString.length > 110)
            messageString = SpannableString(messageString.substring(0, 60).plus(" ... more "))
        holder.itemView.message.text = messageString
        holder.itemView.unreadTextView.text=fragment.getResourceString("unread")
        holder.itemView.title.text = item.title.toString()
      //  holder.itemView.date.text = DateUtils.toDisplayDate(item.notificationDate!!, DateUtils.DISPLAY_DATE)
        holder.itemView.date.text = DateUtils.toDisplayDateTH(item.notificationDate!!)
        ImageUtils.loadImageToGlide(holder.itemView.imageView, item.icon)
        if ((unreadedCount-1) == position) {
            holder.itemView.unReadLayout.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemSelected(notificationList[position], position)
        }
        if ((position + 1) == notificationList.size) {
            listener.onItemSelected()
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.message
            itemView.title
            itemView.date
            itemView.unReadLayout
            itemView.unreadTextView
        }

    }
}