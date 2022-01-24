package com.gmcoreui.controllers.ui

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout


class GMAutoCompleteTextView : AppCompatAutoCompleteTextView {

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val parentView = parent as View
            if (parentView is FrameLayout) {
                val actualParentView = parentView.getParent() as View
                if (actualParentView is TextInputLayout) {
                    actualParentView.error = null
                    actualParentView.isErrorEnabled = false
                }
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        this.addTextChangedListener(textWatcher)
    }

}
