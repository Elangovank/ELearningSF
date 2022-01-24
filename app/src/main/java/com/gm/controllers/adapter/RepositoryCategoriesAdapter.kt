package com.gm.controllers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.item_repos_categories.view.*
import kotlinx.android.synthetic.main.layout_no_record.view.*

class RepositoryCategoriesAdapter(var list: ArrayList<Model.RepositoryCategories>,
                                  private var onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var searchResult = -1
    private val TYPE_FOUND = 1
    private val TYPE_ERROR = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOUND) {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repos_categories, parent, false))
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_no_record, parent, false)
            ErrorViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        if(searchResult==0){
            return 1
        }
        if (list != null) {
            return list.size
        }
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (searchResult !=0) {
            TYPE_FOUND
        } else {
            TYPE_ERROR
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder){
            if(list!=null){
                list[position].name?.let {
                    holder.itemView.medicineNameTextView.text = it
                }
                list[position].mediaPath?.let {
                    ImageUtils.loadImageToGlide(holder.itemView.medicineImageView, it)
                }
                holder.itemView.setOnClickListener {
                    onItemClickListener.onItemSelected(list[position], position)
                }
            }
        }else if(holder is ErrorViewHolder){
            holder.itemView.noDataLayout.visibility=View.VISIBLE
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.medicineImageView
            itemView.medicineNameTextView
        }
    }
    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.noDataLayout
        }
    }
}