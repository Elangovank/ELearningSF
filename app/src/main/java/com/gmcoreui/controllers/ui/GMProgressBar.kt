package com.gmcoreui.controllers.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar



class GMProgressBar : FrameLayout {
    private var mProgressBar: ProgressBar? = null
    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,-1)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setBackgroundColor(Color.TRANSPARENT)
        val lp = FrameLayout.LayoutParams(150, 150)
        lp.gravity = Gravity.CENTER
        mProgressBar = ProgressBar(context)
        mProgressBar?.isIndeterminate = true
        addView(mProgressBar, lp)
        setOnClickListener({})
    }
}
