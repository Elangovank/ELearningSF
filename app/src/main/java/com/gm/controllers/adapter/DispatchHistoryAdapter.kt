package com.gm.controllers.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.MaterialHistoryFragment
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_history_dispatch_status.view.*


class DispatchHistoryAdapter(var itemList: ArrayList<Model.HistoryMaterialArrival>, var onItemClickListener: OnItemClickListener,var fargment:MaterialHistoryFragment)
    : PagingAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_history_dispatch_status, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        viewHolder.itemView.chickLayout?.visibility = View.VISIBLE
        if (item.chicksCount != null) {
            viewHolder.itemView.chickLayout?.visibility = View.VISIBLE

        } else {
            viewHolder.itemView.chickLayout?.visibility = View.GONE
        }
        if (item.feedQuantity != null) {
            viewHolder.itemView.feedLayout?.visibility = View.VISIBLE
        } else {
            viewHolder.itemView.feedLayout?.visibility = View.GONE
        }
        viewHolder.itemView.arrivedDateTextView?.text = DateUtils.toDisplayDate(item.arrivalDate)
        viewHolder.itemView.chickTextView?.text = fargment.getResourceString("Chicks")
        viewHolder.itemView.feedTextView?.text=fargment.getResourceString("feeds")
        viewHolder.itemView.lableBagsTextView?.text=fargment.getResourceString("lable_bags")
        viewHolder.itemView.bagsTextView?.text=fargment.getResourceString("lable_bags")
        viewHolder.itemView.itemCountTextView?.text = item.feedItemCount.toString()
        viewHolder.itemView.chickQty?.text = item.chicksCount.toString()
        viewHolder.itemView.feedQty?.text = item.feedQuantity.toString()
        viewHolder.itemView.setOnClickListener {
            onItemClickListener.onItemSelected(item, position)
        }


    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemSelected(itemList[adapterPosition], adapterPosition)
            }
        }
    }
}