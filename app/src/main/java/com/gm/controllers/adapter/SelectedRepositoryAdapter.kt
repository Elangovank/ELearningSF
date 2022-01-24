package com.gm.controllers.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.DataRepository
import com.gm.utilities.ImageUtils
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_selected_repository.view.*

class SelectedRepositoryAdapter(var itemList: ArrayList<Model.Repository>, var onItemClickListener: OnItemClickListener)
    : PagingAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_selected_repository, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {
            val item = itemList[position]
            if (item.status!! || item.repositoryId==DataRepository.repositoryId) {
                viewHolder.itemView.maincard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
            } else {
                viewHolder.itemView.maincard.setCardBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.card_bg))
            }
            viewHolder.itemView.titleView.text = item.title ?: ""
            viewHolder.itemView.descriptionView.text = Html.fromHtml(Html.fromHtml(item.message!!).toString()).toString()
            viewHolder.itemView.setOnClickListener {
                onItemClickListener.onItemSelected(item, position)
            }
            ImageUtils.loadImageToGlide(viewHolder.itemView.imageView, item.repositoryImageUrl)


        }
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.titleView
            itemView.dateView.visibility = View.GONE
            itemView.imageView
            itemView.maincard
            itemView.descriptionView
        }
    }
}