package com.gmcoreui.controllers.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Subclass of [android.support.v7.widget.AppCompatImageView] to render drawable in circular layout.
 */
class CircularImageView : androidx.appcompat.widget.AppCompatImageView {
    /**
     * Reference to [RectF] holding image view width and height.
     */
    private val drawableRect = RectF()
    /**
     * Reference to [Matrix]
     */
    private val shaderMatrix = Matrix()
    /**
     * Reference to [Paint]
     */
    private val bitmapPaint = Paint()

    /**
     * Bitmap of image views [Drawable]
     */
    private var bitmap: Bitmap? = null
    /**
     * Reference to [BitmapShader]
     */
    private var bitmapShader: BitmapShader? = null

    /**
     * Radius of image view circle.
     */
    private var drawableRadius: Float = 0.toFloat()
    /**
     * Tells whether shader and matrix can be updated.
     */
    private var canUpdatePaintUtils: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        canUpdatePaintUtils = true
        super.setScaleType(ImageView.ScaleType.FIT_CENTER)
        setup()
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable != null) {
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), drawableRadius, bitmapPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        }
        setup()
    }

    override fun setImageBitmap(imageBitmap: Bitmap?) {
        super.setImageBitmap(imageBitmap)
        bitmap = imageBitmap
        setup()
    }

    /**
     * Update and invalidate shaderMatrix based on drawable bounds.
     */
    private fun setup() {
        if (!canUpdatePaintUtils || bitmap == null) {
            return
        }
        bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.isAntiAlias = true
        bitmapPaint.shader = bitmapShader
        drawableRect.set(RectF(0f, 0f, width.toFloat(), height.toFloat()))
        drawableRadius = Math.min(drawableRect.height() / 2, drawableRect.width() / 2)

        updateShaderMatrix()
        invalidate()
    }

    /**
     * Update shader matrix with respect to active drawable width and height.
     */
    private fun updateShaderMatrix() {
        val scale: Float
        shaderMatrix.set(null)
        if (bitmap!!.width * drawableRect.height() > drawableRect.width() * bitmap!!
                .height) {
            scale = drawableRect.height() / bitmap!!.height.toFloat()
        } else {
            scale = drawableRect.width() / bitmap!!.width.toFloat()
        }

        shaderMatrix.setScale(scale, scale)
        //        shaderMatrix.postTranslate(dx + drawableRect.left, dy + drawableRect.top);

        bitmapShader!!.setLocalMatrix(shaderMatrix)
    }
}
