package com.gmcoreui.controllers.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatSpinner
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import com.gm.R
import java.util.ArrayList


class GMSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatSpinner(context, attrs), GMWidget {
    internal var jsonKey: String? = ""
    internal var onItemSelectedListener: AdapterView.OnItemSelectedListener? = null
    private var mObservers: ArrayList<WidgetObserver>? = null


    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mObservers = ArrayList()
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.GMWidget,
                    0, 0)

            try {
                jsonKey = a.getString(R.styleable.GMWidget_jsonkey)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        super.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onItemSelected(parentView, selectedItemView, position, id)
                }
                notifyObservers()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onNothingSelected(parentView)
                }
                notifyObservers()
            }

        })
    }

    override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?) {
        this.onItemSelectedListener = listener

    }

    override fun getJsonKey(): String? {
        return jsonKey
    }

    override fun getJsonObject(): Any {
        return selectedItemPosition
    }

    override fun registerObserver(observer: WidgetObserver) {
        if (!mObservers!!.contains(observer)) {
            mObservers!!.add(observer)
        }
    }

    override fun removeObserver(observer: WidgetObserver) {
        if (mObservers!!.contains(observer)) {
            mObservers!!.remove(observer)
        }
    }

    override fun notifyObservers() {
        for (observer in mObservers!!) {
            observer.onDataChanged(this, jsonObject)
        }
    }

}
