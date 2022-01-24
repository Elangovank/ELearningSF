package com.gmcoreui.controllers.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.db.SingleTon
import kotlinx.android.synthetic.main.alert_dialog.*



class CustomDialog(context: Context?) : Dialog(context!!) {

    companion object {
        const val ALERT_TYPE_SUCCESS = 1
        const val ALERT_TYPE_ERROR = 2
        const val ALERT_TYPE_WARNING = 3
    }

    init {
        setContentView(R.layout.alert_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        alertOk.visibility = View.GONE
    }

    fun setType(alertType: Int): CustomDialog {
        when (alertType) {
            ALERT_TYPE_SUCCESS -> {
                alertTitle.text = SingleTon.getResourceStringValue("label_success")
            }
            ALERT_TYPE_ERROR -> {
                alertTitle.text =SingleTon.getResourceStringValue("error")
            }
            ALERT_TYPE_WARNING -> {
                alertTitle.text =SingleTon.getResourceStringValue("warning")
            }
        }
        return this
    }

    fun setMessage(message: String): CustomDialog {
        alertMessage.text = message
        return this
    }

    fun setPositiveButton(listener: DialogInterface.OnClickListener): CustomDialog {
        alertOk.visibility = View.VISIBLE
        alertOk.setOnClickListener { listener.onClick(this, -1) }
        return this
    }

    fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener): CustomDialog {
        alertOk.visibility = View.VISIBLE
        alertOk.text = text
        alertOk.setOnClickListener { listener.onClick(this, -1) }
        return this
    }

}