package com.gm.firebase

import android.content.Intent
import com.core.utils.AppPreferences
import com.gm.WebServices.ServiceWrapper
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }


    companion object {
        private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    }
}