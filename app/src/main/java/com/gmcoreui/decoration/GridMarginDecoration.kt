package com.gmcoreui.decoration

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class GridMarginDecoration(context: Context, private val margin: Float)
    : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(margin.toInt(), margin.toInt(), margin.toInt(), margin.toInt())
    }
}
