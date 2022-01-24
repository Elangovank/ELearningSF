package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.listener.OnItemSelectedListener
import com.gm.models.Model
import kotlinx.android.synthetic.main.item_text.view.*

class SingleChoiceAdapter(private var optionList: ArrayList<Model.SingleChoice>, var context: Context,
                          var listener: OnItemSelectedListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false))
    }

    override fun getItemCount(): Int {
        return optionList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = optionList.get(position)
        holder.itemView.single_choice_text.text = item.option ?: ""
        if (item.isSelected == true) {
          holder.itemView.optionLayout?.setBackgroundDrawable(context.resources.getDrawable(R.drawable.text_bg))
            holder.itemView.single_choice_text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.item_radio_button_selected, 0, 0, 0)
        } else {
            holder.itemView.single_choice_text?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.item_radio_button_unselected, 0, 0, 0)
            holder.itemView.optionLayout?.setBackgroundDrawable(null)
        }
        holder.itemView.optionLayout.setOnClickListener {
            listener.onItemSelected(optionList[position], position, position, holder.itemView.single_choice_text)
        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.single_choice_text
        }
    }


}