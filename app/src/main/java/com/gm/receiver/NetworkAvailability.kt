package com.gm.receiver

import android.content.Context
import android.net.ConnectivityManager

class NetworkAvailability{
    companion object {
        fun isNetworkAvailable(context: Context): Boolean
        {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return if (netInfo != null && netInfo.isConnectedOrConnecting) {
                true
            } else {
                return false
            }

        }
    }

}