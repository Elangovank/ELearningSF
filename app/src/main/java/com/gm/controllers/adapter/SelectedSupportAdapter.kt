package com.gm.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gm.R
import com.gm.WebServices.URLUtils
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import kotlinx.android.synthetic.main.item_medicine_type.view.*


class SelectedSupportAdapter(private var itemList: ArrayList<Model.Support>, var onItemClickListener: OnItemClickListener)
    : PagingAdapter<RecyclerView.ViewHolder>() {
    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_medicine_type, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        viewHolder.itemView.medicineNameTextView?.text = item.name
        loadImageToGlide(viewHolder.itemView.medicineImageView, item.supportImageUrl)
        viewHolder.itemView.medicineLayout.setOnClickListener {
            onItemClickListener.onItemSelected(item, position)
        }
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    private fun loadImageToGlide(imageView: ImageView, url: String?) {
        url?.let {
            val imageUrl = URLUtils.baseUrl + url
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.placeholder_image)
            requestOptions.error(R.drawable.placeholder_image)

            Glide.with(imageView.context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageUrl)
                    .into(imageView)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.medicineImageView
            itemView.medicineNameTextView
            itemView.medicineLayout
        }
    }
}