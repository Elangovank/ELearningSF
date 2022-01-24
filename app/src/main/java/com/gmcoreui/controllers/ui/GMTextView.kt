package com.gmcoreui.controllers.ui

import android.content.Context
import android.util.AttributeSet

open class GMTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
