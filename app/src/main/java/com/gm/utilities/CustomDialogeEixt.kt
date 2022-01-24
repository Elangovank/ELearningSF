package com.gm.utilities


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.db.SingleTon
import kotlinx.android.synthetic.main.layout_exit_dialog.*


class CustomDialogeEixt(context: Context?) : Dialog(context!!) {

    companion object {
        const val ALERT_TYPE_SUCCESS = 1
        const val ALERT_TYPE_ERROR = 2
        const val ALERT_TYPE_WARNING = 3
    }

    init {
        setContentView(R.layout.layout_exit_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        alertOk1?.visibility = View.GONE
    }

    fun setType(alertType: Int): CustomDialogeEixt {
        when (alertType) {
            ALERT_TYPE_SUCCESS -> {
                alertTitle1?.text = SingleTon.getResourceStringValue("label_success")
            }
            ALERT_TYPE_ERROR -> {
                alertTitle1?.text =SingleTon.getResourceStringValue("error")
            }
            ALERT_TYPE_WARNING -> {
                alertTitle1?.text =SingleTon.getResourceStringValue("warning")
            }
        }
        return this
    }

    fun setMessage(message: String): CustomDialogeEixt {
        alertMessage1.text = message
        return this
    }
    fun setPositiveButtonMessage(message: String): CustomDialogeEixt {
        alertOk1.text = message
        return this
    }
    fun setNegativeButtonMessage(message: String): CustomDialogeEixt {
        cancelButton.text = message
        return this
    }


    fun setPositiveButton(listener: DialogInterface.OnClickListener): CustomDialogeEixt {
        alertOk1?.visibility = View.VISIBLE
        alertOk1?.setOnClickListener { listener.onClick(this, -1) }
        return this
    }

    fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener): CustomDialogeEixt {
        alertOk1?.visibility = View.VISIBLE
        alertOk1?.text = text
        alertOk1?.setOnClickListener { listener.onClick(this, -1) }
        return this
    }
    fun setNegativeButton(listener: DialogInterface.OnClickListener): CustomDialogeEixt {
        cancelButton?.visibility = View.VISIBLE
        cancelButton?.setOnClickListener { listener.onClick(this, -1) }
        return this
    }
}