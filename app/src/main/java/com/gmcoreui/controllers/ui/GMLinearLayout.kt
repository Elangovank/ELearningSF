package com.gmcoreui.controllers.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.gm.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class GMLinearLayout @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0) : LinearLayout(context,
        attrs, defStyleAttr), GMWidget, WidgetObserver {
    internal var jsonKey: String? = ""
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

    override fun getJsonKey(): String? {
        return jsonKey
    }

    override fun getJsonObject(): Any {
        val obj = JSONObject()
        val childrenCount = childCount
        if (childrenCount > 0) {
            for (i in 0 until childrenCount) {
                val childView = getChildAt(i)
                if (childView != null) {
                    if (childView is GMWidget) {
                        val jsonKey = (childView as GMWidget).jsonKey
                        val jsonValue = (childView as GMWidget).jsonObject
                        try {
                            obj.put(jsonKey, jsonValue)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
        return obj
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childrenCount = childCount
        if (childrenCount > 0) {
            for (i in 0 until childrenCount) {
                val childView = getChildAt(i)
                if (childView != null) {
                    if (childView is GMWidget) {
                        (childView as GMWidget).registerObserver(this)
                    }
                }
            }
        }
    }

    override fun onDataChanged(widget: GMWidget, value: Any) {
        val jsonKey = widget.jsonKey
        val jsonValue = widget.jsonObject
        Log.i("JSON", jsonKey + ":" + jsonValue)
    }

    fun validate(): Boolean {
        var isValid = true
        if (childCount <= 0) {
            return isValid
        }
        // TODO refactor
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView != null
                    && childView is GMEditText
                    && childView.visibility == View.VISIBLE) {
                if (!childView.validate()) {
                    isValid = false
                    break
                } else {
                    isValid = true
                    continue
                }
            }
        }
        return isValid
    }
}
