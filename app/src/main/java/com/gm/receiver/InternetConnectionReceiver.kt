package com.gm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.core.network.Connection
import com.gm.utilities.GMKeys

class InternetConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (NetworkAvailability.isNetworkAvailable(context)) {
                context.startService(Intent(context, SyncService::class.java))
            }
            val actionIntent = Intent(GMKeys.ACTION_CONNECTION)
            context.sendBroadcast(actionIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}