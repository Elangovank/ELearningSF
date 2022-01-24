package com.gm.controllers.adapter

import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.gm.R
import com.gm.listener.OnItemClickListener
import kotlinx.android.synthetic.main.item_menu.view.*

class NavigationMenuAdapter(var itemList: ArrayList<String>, private var iconList: TypedArray,
                            private var onItemClickListener: OnItemClickListener)
    : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var selectedMenuIndex: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val menu = itemList.get(position)
        holder.itemView.menuItem.text = menu
        holder.itemView.menuItem.setCompoundDrawablesWithIntrinsicBounds(iconList.getDrawable(position), null, null, null)
//        if (position == selectedMenuIndex) {
//            holder.itemView.menuItem.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.montserrat_bold)
//            holder.itemView.menuItem.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
//            holder.itemView.menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.itemView.context.getResources().getDimension(R.dimen.font_size_x_large))
//        } else {
//            holder.itemView.menuItem.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.montserrat_regular)
//            holder.itemView.menuItem.setTextColor(holder.itemView.context.resources.getColor(R.color.lightGrayShade))
//            holder.itemView.menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.itemView.context.getResources().getDimension(R.dimen.font_size_medium))
//        }
        holder.itemView.menuItem.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.worksans_regular)
        holder.itemView.menuItem.setTextColor(holder.itemView.context.resources.getColor(R.color.lightGrayShade))
        holder.itemView.menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.itemView.context.getResources().getDimension(R.dimen.font_size_medium))
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemSelected(menu, position)
        }
    }


    fun selectedMenu(position: Int) {
        selectedMenuIndex = position
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.menuItem
        }
    }
}