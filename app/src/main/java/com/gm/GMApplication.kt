package com.gm

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager


import androidx.multidex.MultiDexApplication
import androidx.room.Room

import com.gm.WebServices.DataProvider
import com.gm.db.AppDatabase
import com.gm.receiver.InternetConnectionReceiver
import com.gm.utilities.GMKeys
import gm.service.security.AESCrypt
import androidx.multidex.MultiDex
import com.core.utils.AppPreferences
import com.core.utils.Config

import com.gm.utilities.LocaleHelper


class GMApplication : MultiDexApplication() {


    companion object {
        var appContext: Context? = null
        var loginUserId: Long = 0
        var userName: String = ""
        var farmCode: String = ""
        var level2Token: String = ""
        var languageID: Long? = 0
        var languageCode:String?=""
        var resourceStringMap:HashMap<String,String>?=null
    }

    var receiver: InternetConnectionReceiver? = null
    var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.BUILD_TYPE == "debug"){
            Config.LOG_ENABLED=true
            Config.FIREBASE_APPLICATION_NAME = "Suguna"
            Config.FIREBASE_STORAGE_ENABLED_SUCCESS_CASES = true
            Config.FIREBASE_STORAGE_ENABLED = true
            Config.FIREBASE_STORAGE_URL="https://suguna-b9167.firebaseio.com"
        }
        appContext = applicationContext
         database = Room.databaseBuilder(applicationContext,
                 AppDatabase::class.java, packageName)
                 .fallbackToDestructiveMigration()
                 .build()
        DataProvider.application = this
        AppPreferences.initInstance(applicationContext, packageName)
        val encryptionKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.ENCRYPTION_KEY_DEV, "")
        val vectorKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.VECTOR_KEY_DEV, "")
        AESCrypt.configure(encryptionKey!!, vectorKey!!)
        registerReceiver()
    }


    private fun registerReceiver() {
        receiver = InternetConnectionReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, intentFilter)
    }


    override fun onTerminate() {
        super.onTerminate()
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {

        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(context,"en"))
        MultiDex.install(this)
    }

}
