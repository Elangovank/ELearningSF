package com.gm.controllers.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.RaisedRequestListFragment
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_issue_raisd_list.view.*
import kotlinx.android.synthetic.main.layout_no_record.view.*


class RaisedRequestAdapter(private var optionList: ArrayList<Model.SuppourtDetailList>, val context: Context,
                           var onItemClickListener: OnItemClickListener,
                           var resources: Resources,var fragment: RaisedRequestListFragment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_FOUND = 1
    private val TYPE_ERROR = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOUND) {
            return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_issue_raisd_list, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_record, parent, false)
            ErrorViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if (optionList.size != 0) {
            return optionList.size
        } else {
            return 1
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (optionList.size != 0) {
            TYPE_FOUND
        } else {
            TYPE_ERROR
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ColorViewHolder) {
            val iconList = resources.obtainTypedArray(R.array.supportCategory)
            val item = optionList[position]
            val tamilString = ArrayList<String>()
            tamilString.add(fragment.getResourceString("chick_bird"))
            tamilString.add(fragment.getResourceString("feed_stock"))
            tamilString.add(fragment.getResourceString("medicine"))
            tamilString.add(fragment.getResourceString("marketting"))
            tamilString.add(fragment.getResourceString("management"))
            tamilString.add(fragment.getResourceString("others"))
            holder.itemView.requestIdTextView.text=fragment.getResourceString("request_id")
            holder.itemView.supportNameTextView?.setText(item.supportCategoryId?.minus(1)?.let { tamilString.get(it) })
            holder.itemView.supportName?.text = item.supportSubCategory
            holder.itemView.date?.text = DateUtils.toDisplayDate(item.createdDate)
            holder.itemView.subCatagoryId?.text = item.supportTicketId.toString()
            holder.itemView.notificationIconImageView?.visibility = if (item.isNewResponse == true) View.VISIBLE else View.GONE
            holder.itemView.icone.setCompoundDrawablesWithIntrinsicBounds(iconList.getDrawable(item.supportCategoryId!!), null, null, null)
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemSelected(item, position)
            }
        } else {
            holder.itemView.noDataLayout.visibility = View.VISIBLE
        }

    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.subCatagoryId
            itemView.date
            itemView.icone
            itemView.supportName
            itemView.noDataLayout
            itemView.notificationIconImageView
            itemView.requestIdTextView
        }
    }

    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.noDataLayout
        }
    }

}