package com.gm.firebase

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.core.utils.AppPreferences
import com.gm.WebServices.ServiceWrapper
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.BaseActivity.Companion.TAG
import com.gmcoreui.utils.ActivityUtils


class RegistrationIntentService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        sendRegistrationToServer(ActivityUtils.getFCMToken())
        val registrationComplete = Intent(GMKeys.REGISTRATION_COMPLETE)
        registrationComplete.putExtra(GMKeys.token, ActivityUtils.getFCMToken())
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)

    }

    private fun sendRegistrationToServer(token: String?) {
        token?.let {
            AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.FCMKEY, token)
            ServiceWrapper.postFCMKey(it, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.MessageResponse) {

                    }
                }

                override fun onRequestFailed(responseObject: String) {
                }
            })
        }
    }
}