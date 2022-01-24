package com.gmcoreui.controllers.ui

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.gm.R
import java.util.ArrayList


/**
 * Created by ganeshkanna.subraman on 16-02-2018.
 */

class GMTextInputLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TextInputLayout(context, attrs, defStyleAttr), GMWidget {
    internal var jsonKey: String? = ""
    private var mObservers: ArrayList<WidgetObserver>? = null

    init {
        init(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                notifyObservers()
            }
        })
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
        return editText!!.text.toString()
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
