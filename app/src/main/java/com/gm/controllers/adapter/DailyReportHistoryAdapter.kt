package com.gm.controllers.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.DailyReportFragment
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_media_list.view.*
import kotlinx.android.synthetic.main.item_report_history.view.*
import kotlinx.android.synthetic.main.layout_no_record.view.*


class FeedBackHistoryAdapter(var feedBackList1: ArrayList<Model.DailyReportDateHistory>,
                             private var onItemClickListener: OnItemClickListener, private var context: Context, var fragment: DailyReportFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_FOUND = 1
    private val TYPE_ERROR = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOUND) {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_report_history, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_record, parent, false)
            ErrorViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if (feedBackList1.size != 0) {
            return feedBackList1.size
        } else {
            return 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (feedBackList1.size != 0) {
            TYPE_FOUND
        } else {
            TYPE_ERROR
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            var feedBackList = java.util.ArrayList<Model.FeedBack>()
            holder.itemView.mediaAdded.text = fragment.getResourceString("media_uploded")
            holder.itemView.reasonGMTextInputLayout.hint = fragment.getResourceString("reason")
            holder.itemView.videoTextView?.text = fragment.getResourceString("video")
            holder.itemView.photoTextView?.text = fragment.getResourceString("photos")
            holder.itemView.audioTextView?.text = fragment.getResourceString("select_audio")
            holder.itemView.documentTextview?.text = fragment.getResourceString("document")
            holder.itemView.addItemTextView1?.text = fragment.getResourceString("add_item")
            holder.itemView.uploadTextView?.text = fragment.getResourceString("upload")
            holder.itemView.deleteTextView1?.text = fragment.getResourceString("delete")
            holder.itemView.addItemTextView2?.text = fragment.getResourceString("add_item")
            holder.itemView.uploadMediaTextView1?.text = fragment.getResourceString("add_item")
            holder.itemView.deleteTextView2?.text = fragment.getResourceString("delete")
            holder.itemView.noItemTextView1?.text = fragment.getResourceString("no_media_found")
            holder.itemView.audioTextView?.text = fragment.getResourceString("select_audio")
            holder.itemView.reasonTextView?.text = feedBackList1?.get(position).reports?.feedConsumptionReason
            holder.itemView.mediaAdded.visibility = View.GONE
            holder.itemView.mediaList.visibility = View.GONE
            holder.itemView.updateTimeTextView.visibility = View.VISIBLE
            holder.itemView.reasonHistoryEditText.visibility = View.VISIBLE
            if (feedBackList1?.get(position).reports?.feedConsumptionReason != null) {
                holder.itemView.feedConsumptionReasonLayout.visibility = View.VISIBLE
            } else {
                holder.itemView.feedConsumptionReasonLayout.visibility = View.GONE
            }
            holder.itemView.updateTimeTextView.text = fragment.getResourceString("updated_string").plus(" ").plus(feedBackList1.get(position).count.toString()) + fragment.getResourceString("times")
            context?.let {
                if (context != null) {
                    feedBackList.add(Model.FeedBack(R.drawable.ic_feedstock, fragment.getResourceString("feed_stock"), fragment.getResourceString("lable_bags"), decimalCount = feedBackList1.get(position).reports?.feedStock?.toString()
                            ?: "0"))

                    feedBackList.add(Model.FeedBack(R.drawable.ic_recieot, fragment.getResourceString("reciept"), fragment.getResourceString("lable_bags"), decimalCount = feedBackList1.get(position).reports?.receipt?.toString()
                            ?: "0"))

                    feedBackList.add(Model.FeedBack(R.drawable.ic_mortality, fragment.getResourceString("mortality"), fragment.getResourceString("lable_chickens"), decimalCount = feedBackList1.get(position).reports?.mortality?.toString()
                            ?: "0"))

                    feedBackList.add(Model.FeedBack(R.drawable.ic_feedcomsumption, fragment.getResourceString("feed_consumption"), fragment.getResourceString("lable_bags"), decimalCount = feedBackList1.get(position).reports?.feedConsumption?.toString()
                            ?: "0"))

                    feedBackList.add(Model.FeedBack(R.drawable.ic_bodyweight, fragment.getResourceString("body_weight"), fragment.getResourceString("lable_gm"), decimalCount = feedBackList1.get(position).reports?.bodyWeight?.toString()
                            ?: "0.0"))
                    feedBackList.add(Model.FeedBack(R.drawable.ic_feedtransferin, fragment.getResourceString("feed_transfer_in"), fragment.getResourceString("lable_bags"), decimalCount = feedBackList1.get(position).reports?.feedTransferIn?.toString()
                            ?: "0"))

                    feedBackList.add(Model.FeedBack(R.drawable.ic_feedtransferout, fragment.getResourceString("feed_transfer"), fragment.getResourceString("lable_bags"), decimalCount = feedBackList1.get(position).reports?.feedTransferOut?.toString()
                            ?: "0"))

                }

                var feedBackHistoryAdapter: FeedBackHistoryItemAdapter? = null
                holder.itemView.historyRecyclerView?.layoutManager = GridLayoutManager(context, 2) as RecyclerView.LayoutManager?
                feedBackHistoryAdapter = FeedBackHistoryItemAdapter(feedBackList, onItemClickListener, context!!)
                holder.itemView.historyRecyclerView?.adapter = feedBackHistoryAdapter

                //  holder.itemView.updateTimeTextView.text=con
                if (!feedBackList1.get(position).reports?.reason.isNullOrEmpty()) {
                    holder.itemView.reasonHistoryEditText.visibility = View.VISIBLE
                    holder.itemView.reasonHistoryEditText?.setText(feedBackList1.get(position).reports?.reason)
                } else {
                    holder.itemView.reasonHistoryEditText.visibility = View.GONE
                }

                holder.itemView.dateTextView?.text = DateUtils.toDisplayDate(feedBackList1.get(position).reports?.createdDate)
            }
            holder.itemView.expandView.setOnClickListener {
                if (holder.itemView.reportLayout.isVisible) {
                    holder.itemView.reportLayout.visibility = View.GONE
                } else {
                    holder.itemView.reportLayout.visibility = View.VISIBLE
                }
            }
        } else if (holder is ErrorViewHolder) {
            holder.itemView.noDataLayout.visibility = View.VISIBLE
        }
    }


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.reportLayout
            itemView.dateTextView
            itemView.arrowImageView
            itemView.expandView
            itemView.historyRecyclerView
            itemView.updateTimeTextView
            itemView.reasonHistoryEditText
            itemView.mediaAdded
            itemView.mediaList
            itemView.reasonGMTextInputLayout
            itemView.reasonTextView
            itemView.feedConsumptionReasonLayout


        }
    }


    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.noDataLayout
        }
    }
}