package com.gm.controllers.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemClickListener
import com.gm.models.Model

class CheckBoxAdapter(var itemList: ArrayList<Model.SingleChoice>, var onItemClickListener: OnItemClickListener)
    : PagingAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_check_box, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {
            val button = viewHolder.itemView as? CheckBox
            button?.text = itemList[position].option ?: "-"
            button?.isChecked = itemList[position].isSelected==true
        }
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val button = itemView as CheckBox
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(button, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.text_color)))
            } else {
                button.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.text_color))
            }
            button.setOnCheckedChangeListener { _, isChecked ->
                onItemClickListener.onItemSelected(isChecked,adapterPosition)
            }
        }
    }
}