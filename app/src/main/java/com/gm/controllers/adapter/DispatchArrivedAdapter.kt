package com.gm.controllers.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.DispatchArrivedFragment
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_arrived_material.view.*


class DispatchArrivedAdapter(var itemList: ArrayList<Model.HistoryMaterialArrivalDetail>,var context: Context,var fragment:DispatchArrivedFragment)
    : PagingAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_arrived_material, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList.get(position)
        viewHolder.itemView.vehicleNumberTextView?.text = item.vehicleNumber
        viewHolder.itemView.arrivalTimeTextView?.text = DateUtils.toDisplayTimeHistory(item.expectedTime)

        viewHolder.itemView.estimatedArrivalTextView?.text=fragment?.getResourceString("label_estimated_arrival")
        viewHolder.itemView.itemTextView?.text=fragment?.getResourceString("label_item")
        viewHolder.itemView.itemLabelTextView?.text=fragment?.getResourceString("label_item")
        viewHolder.itemView.dispatchedTextView?.text=fragment?.getResourceString("label_dispatched_qty")
        viewHolder.itemView.arrivalTextView?.text=fragment?.getResourceString("arrived_qty")
        viewHolder.itemView.commentLabelTextView?.text=fragment?.getResourceString("comment")

        var ti=DateUtils.toDisplayTimeHistoryAM(item.expectedTime)
        if (ti.trim().equals("AM"))
        {
            viewHolder.itemView.timeTextView?.text=" ".plus(fragment?.getResourceString("am"))
        }else{
            viewHolder.itemView.timeTextView?.text=" ".plus(fragment?.getResourceString("pm"))
        }
        viewHolder.itemView.materialNameTextView?.text = item.materialName.plus("-").plus(item.itemName)
        viewHolder.itemView.arrivedQtyTextView?.text = item.arrivalQuantity.toString()
        viewHolder.itemView.dispatchedQtyTextView?.text = item.dispatchedQuantity.toString()
        viewHolder.itemView.commentTextView?.text = item.comments
        viewHolder.itemView.phoneNumberTextView?.text=item.driverMobileNo

        if (!item.comments.isNullOrEmpty()) {
            viewHolder.itemView.commentLayout?.visibility = View.VISIBLE
        } else {
            viewHolder.itemView.commentLayout?.visibility = View.GONE
        }

    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.vehicleNumberTextView
            itemView.arrivalTimeTextView
            itemView.materialNameTextView
            itemView.arrivedQtyTextView
            itemView.dispatchedQtyTextView
            itemView.commentTextView
            itemView.commentLayout
            itemView.phoneNumberTextView
            itemView.timeTextView
            itemView.dispatchedTextView
            itemView.estimatedArrivalTextView
            itemView.itemTextView
            itemView.itemLabelTextView
            itemView.commentLabelTextView

        }
    }
}