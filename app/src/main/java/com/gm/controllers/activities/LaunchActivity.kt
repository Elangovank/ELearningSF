package com.gm.controllers.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.core.network.Connection
import com.core.utils.AppPreferences
import com.core.utils.Config
import com.core.utils.Logger
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.ServiceWrapper
import com.gm.db.SingleTon
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.Downloader
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gm.utilities.LocaleHelper
import com.gmcoreui.controllers.BaseActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import gm.service.network.ProxyHelper
import gm.service.security.AESCrypt
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import java.io.File
import java.util.*


class LaunchActivity : BaseActivity() {
    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    private var cacheExpiration: Long = 3600 // 1 hour in seconds.
    lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(cacheExpiration)
                .setDeveloperModeEnabled(true)
                .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            // cacheExpiration = 0
        }
        /*if (userToken.isNullOrEmpty() || groupKey.isNullOrEmpty()) {
            fetchAPPKey(mFirebaseRemoteConfig)
        } else {
            loadNext()
        }*/
        val handler = Handler()
        starImageView.visibility = View.GONE
        val widthList = resources.getIntArray(R.array.splash_animation_width)
        var count = 0
        runnable = object : Runnable {
            override fun run() {
                if (count == 3) {
                    fetchAPPKey(mFirebaseRemoteConfig)
                } else {
                    val animation = ObjectAnimator.ofFloat(starImageView, "translationX", (widthList.get(count)).toFloat()).apply {
                        duration = 1000
                        start()
                    }
                    animation.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            count++
                            if (count == 1) {
                                progressLaunch.progress = 50
                                starImageView.setImageResource(R.drawable.splash_2)
                                handler.postDelayed(runnable, 200)
                            } else if (count == 2) {
                                progressLaunch.progress = 75
                                starImageView.setImageResource(R.drawable.splash_3)
                                handler.postDelayed(runnable, 200)

                            } else if (count == 3) {
                                progressLaunch.progress = 100
                                starImageView.setImageResource(R.drawable.splash_4)
                                handler.postDelayed(runnable, 1000)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }
                    })
                }
            }
        }
        handler.postDelayed(runnable, 200)

    }




    private fun showExitDialog() {

        val factory = LayoutInflater.from(this)
        val confirmationDialogView = factory.inflate(R.layout.alert_dialog, null)
        val confirmationDialog = AlertDialog.Builder(this).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.alertOk.setOnClickListener {
            confirmationDialog?.dismiss()
            finish()
        }
        confirmationDialog?.show()
    }


    private fun fetchAPPKey(mFirebaseRemoteConfig: FirebaseRemoteConfig) {
        if (ProxyHelper.isProxyEnabled(this)!!) {
            Toast.makeText(this, "Disable Proxy and launch the app again", Toast.LENGTH_LONG).show()
            return
        }
        if (NetworkAvailability.isNetworkAvailable(this)) {
            //  return
            mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mFirebaseRemoteConfig.activateFetched()
                    val appKey = mFirebaseRemoteConfig.getString(GMKeys.APP_KEY)
                    val encryptionKey = mFirebaseRemoteConfig.getString(GMKeys.ENCRYPTION_KEY_DEV)
                    val vectorKey = mFirebaseRemoteConfig.getString(GMKeys.VECTOR_KEY_DEV)
                    val weatherAPIKey = mFirebaseRemoteConfig.getString(GMKeys.WEATHER_API_KEY)
                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.APP_KEY, appKey)
                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.ENCRYPTION_KEY_DEV, encryptionKey)
                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.VECTOR_KEY_DEV, vectorKey)
                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.WEATHER_API_KEY, weatherAPIKey)
                    AESCrypt.configure(encryptionKey, vectorKey)
                    if (getAppVersion()<mFirebaseRemoteConfig.getLong(GMKeys.FORCE_UPDATE_VERSION_CODE_KEY)){
                        showUpdateDialog(mFirebaseRemoteConfig.getBoolean(GMKeys.FORCE_UPDATE_KEY))
                    }else{
                        launchLoginActivity()
                    }

                } else {
                    val encryptionKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.ENCRYPTION_KEY_DEV)
                    val vectorKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.VECTOR_KEY_DEV)
                    if(vectorKey.isNullOrEmpty() || encryptionKey.isNullOrEmpty()){
                        fetchAPPKey(mFirebaseRemoteConfig)
                    }else{
                        fetchFromPreference(encryptionKey,vectorKey)
                    }
                }
            }
        } else {
            val encryptionKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.ENCRYPTION_KEY_DEV)
            val vectorKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.VECTOR_KEY_DEV)
            if(vectorKey.isNullOrEmpty() || encryptionKey.isNullOrEmpty()){
                showExitDialog()
            }else{
                fetchFromPreference(encryptionKey,vectorKey)
            }
        }
    }


    private fun getResourceString() {
        GMApplication.languageCode?.let {
            ServiceWrapper.getResourceString(it,object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.ResourceStringResponse) {
                    GMApplication.resourceStringMap= HashMap()
                    val file= File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt")
                    val jsonString:String = Gson().toJson(responseObject)
                    file.deleteOnExit()
                    file.writeText(jsonString)
                    SingleTon.clearMapValue()

                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                //  dismissProgressDialog()
            }
        })
        }

    }




    private fun fetchFromPreference(encryptionKey:String?, vectorKey:String?){
        encryptionKey?.let { vectorKey?.let { it1 -> AESCrypt.configure(it, it1) } }
        launchLoginActivity()
    }

    private fun showUpdateDialog(isForceUpdate: Boolean) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.update_dialog_title)
                    .setMessage(if (isForceUpdate) R.string.force_update_dialog_message else R.string.update_dialog_message)

            // Add the buttons
            builder.setPositiveButton(R.string.update) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=" + applicationContext.packageName)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton(if (isForceUpdate) R.string.no_thanks else R.string.skip) { dialog, _ ->
                dialog.dismiss()
                if (isForceUpdate) {
                    finish()
                } else {
                    launchLoginActivity()
                }
            }
            // Create the AlertDialog
            val dialog = builder.create()
            dialog.show()
        } catch (e: Exception) {
            Logger.e("", e.localizedMessage)
        }

    }

    private fun getAppVersion(): Long {
        try {
            val manager = packageManager
            val info = manager.getPackageInfo(packageName, 0)
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                    return info.longVersionCode
                }else{
                    return (info.versionCode?:1).toLong()
                }
        } catch (e: Exception) {
            Logger.e("", e.localizedMessage)
        }

        return 1
    }


    fun launchLoginActivity() {
        Downloader.getInstance(this).updateDownloader()
        setLanguagePreference()

        val session=AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID,0L)?:0L
        if (session!=0L){
            GMApplication.loginUserId = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID)
                    ?: 0
            GMApplication.level2Token = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.KEY_TOKEN_2)
                    ?: ""
            GMApplication.userName = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.userName)
                    ?: ""
            GMApplication.farmCode = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.farmCode)
                    ?: ""
            GMApplication.languageID = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId, 0L)
                    ?: 0L
            val next = Intent(applicationContext, HomeActivity::class.java)
            val notificationData = intent?.extras
            notificationData?.let { next.putExtras(it) }
            startActivity(next)
            finish()
        } else {
            val next = Intent(this, LanguageActivity::class.java)
            val notificationData = intent?.extras
            notificationData?.let { next.putExtras(it) }
            startActivity(next)
            finish()
        }

    }

    private fun setLanguagePreference() {
        if (AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId)!!.toInt() != 0) {
            var it = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId)!!.toInt()
            var languageToLoad = IntentUtils.assignLanguage(it!!.toInt())
            // your language
            LocaleHelper.setLocale(applicationContext, languageToLoad)
        } else {
            var languageToLoad = "en"
            // your language
            LocaleHelper.setLocale(applicationContext, languageToLoad)
        }
    }


}
