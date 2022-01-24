package com.gmcoreui.decoration

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.View


class LinearMarginDecoration(context: Context, private val margin: Float,
                             private val isHorizontalScroll: Boolean) :
        androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isHorizontalScroll) {
            outRect.set(0, 0, margin.toInt(), 0)
        } else {
            outRect.set(0, 0, 0, margin.toInt())
        }
    }
}
