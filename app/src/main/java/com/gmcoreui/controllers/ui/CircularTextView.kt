package com.gmcoreui.controllers.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import com.gm.R


/**
 * A sub class of [TextView] containing circular background around it.
 */

class CircularTextView : GMTextView{

    /**
     * Background color. Default is set to 0.
     */
    private var textBackgroundColor: Int = 0

    /**
     * Selected view background colour.
     */
    private var selectedBackgroundColor: Int = 0

    /**
     * Tells whether view is selcted.
     */
    var isSelectedView: Boolean = false

    private var circlePaint: Paint? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    fun init(context: Context, attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularTextView)

        textBackgroundColor = typedArray?.getColor(R
                .styleable.CircularTextView_background_color, textBackgroundColor) ?: textBackgroundColor

        selectedBackgroundColor = typedArray?.getColor(R
                .styleable.CircularTextView_selected_background_color, textBackgroundColor) ?: textBackgroundColor

        circlePaint = Paint()
        circlePaint!!.flags = Paint.ANTI_ALIAS_FLAG
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val diameter = if (heightMeasureSpec > widthMeasureSpec) heightMeasureSpec else widthMeasureSpec
        super.onMeasure(diameter, diameter)
    }

    override fun draw(canvas: Canvas) {
        if (textBackgroundColor != 0) {
            circlePaint!!.color = if (isSelectedView) selectedBackgroundColor else textBackgroundColor
            val radius = this.height / 2
            canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), circlePaint!!)
        }
        super.draw(canvas)
    }

    fun setBackgroundCircleColor(backgroundCircleColor: Int) {
        this.textBackgroundColor = backgroundCircleColor
    }

    fun toggleSelection() {
        isSelectedView = !isSelectedView
        post { invalidate() }
    }
}