package com.gmcoreui.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.core.utils.AppPreferences
import com.gm.BuildConfig
import com.gm.utilities.GMKeys
import com.google.firebase.iid.FirebaseInstanceId
import java.io.File

object ActivityUtils {

    var isTab = false
    var is7inchTab = false

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivity.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun getInternalDocumentFile(context: Context, fileName: String?): File {
        var fileNameTemp = fileName
        if (fileNameTemp != null && fileNameTemp.contains(" ")) {
            fileNameTemp = fileNameTemp.replace(" ", "_")
        }
        val fileDir = File(context.filesDir, "Documents")
        if (!fileDir.exists()) {
            fileDir.mkdir()
        }
        return File(fileDir, fileNameTemp!!)
    }

    fun setIsTab(isTab: Boolean) {
        this.isTab = isTab
    }

    fun setIs7inchTab(is7inchTab: Boolean) {
        this.is7inchTab = is7inchTab
    }

    fun getFCMToken():String?{
        if (AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.FCMKEY, "").isNullOrEmpty()){
            return  FirebaseInstanceId.getInstance().token
        }else{
            return  AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.FCMKEY, "")
        }
    }


    fun getVersionName():String
    {
        return BuildConfig.VERSION_NAME
    }

}