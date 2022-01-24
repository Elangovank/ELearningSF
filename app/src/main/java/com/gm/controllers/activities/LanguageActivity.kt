package com.gm.controllers.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.core.utils.AppPreferences
import com.core.utils.Logger
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.ServiceWrapper
import com.gm.WebServices.URLUtils
import com.gm.controllers.adapter.LanguageAdapter
import com.gm.db.SingleTon
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gm.utilities.LocaleHelper
import com.gmcoreui.controllers.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_language.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class LanguageActivity : BaseActivity(), OnItemClickListener {
    val languageList = ArrayList<Model.Language>()
    var languageCode:String?="en"

    companion object {
        const val NAVIGATION="NAVIGATION_TO"
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item!=true){
            val previousIndexValue=languageList.singleOrNull { it.isActive==true }
            languageList.singleOrNull { it.isActive==true }?.isActive=false
            if (item is Boolean?){
                languageList[selectedIndex].isActive=true
                languageCode=languageList[selectedIndex].languageCode
            }
            if (previousIndexValue!=null){
                val previousIndexPosition=languageList.indexOf(previousIndexValue)
                if (previousIndexPosition==selectedIndex){
                    recyclerView?.adapter?.notifyItemChanged(selectedIndex)
                }else{
                    recyclerView?.adapter?.notifyItemChanged(previousIndexPosition)
                    recyclerView?.adapter?.notifyItemChanged(selectedIndex)
                }
            }else{
                recyclerView?.adapter?.notifyItemChanged(selectedIndex)
            }
        }
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_language)
        topLevelLayouts.visibility = View.GONE
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        recyclerView.adapter = LanguageAdapter(this, languageList, applicationContext)
        if (NetworkAvailability.isNetworkAvailable(this)) {
            authorizeLogin()
        } else {
            showExitDialog()
        }

        languageSubmit.setOnClickListener {
            getResourceString()

        }
    }

    private fun submitButtonAction(){
        if (!languageList.isEmpty()) {
            performActionBasedOnNavigation()
            }
    }


    private fun performActionBasedOnNavigation(){
        val from=intent.getSerializableExtra(NAVIGATION) as? INDENT_NAVIGATION
        var isChanged=false
        languageList.singleOrNull { it.isActive==true}?.languageId?.let {
            if(GMApplication.languageID!=it.toLong()){
                isChanged=true
                AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.languageId, it.toLong())
                val languageToLoad = IntentUtils.assignLanguage(it)
                GMApplication.languageID = it.toLong()
                LocaleHelper.setLocale(applicationContext, languageToLoad)
            }
        }

        if (from!=null){
            if (from == INDENT_NAVIGATION.Home) {
                if (isChanged){
                    toSaveLanguage()
                }else{
                    super.onBackPressed()
                }
            } else if (from==INDENT_NAVIGATION.Login) {
                startLoginActivity()
            }else{
                startValidationActivity()
            }
        }else{
            startValidationActivity()
        }
    }



    private fun getResourceString() {
        languageCode?.let {
            GMApplication.languageCode=it
            ServiceWrapper.getResourceString(it,object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.ResourceStringResponse) {
                    GMApplication.resourceStringMap= HashMap()
                    val file=File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt")
                    val jsonString:String = Gson().toJson(responseObject)
                    file.deleteOnExit()
                    file.writeText(jsonString)
                   SingleTon.clearMapValue()
                    submitButtonAction()
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                //  dismissProgressDialog()
            }
        })
        }
    }

    private fun toSaveLanguage() {
        showProgressDialog(getString(R.string.exo_download_downloading ))
        ServiceWrapper.saveLanguage(GMApplication.languageID?:0L, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.LanguageResponse1) {
                    showSnackBar(getString(R.string.language_updated))
                    dismissProgressDialog()
                    startHomeActivity()
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                dismissProgressDialog()
            }
        })
    }

    private fun showExitDialog() {

        val factory = LayoutInflater.from(this)
        val confirmationDialogView = factory.inflate(R.layout.alert_dialog, null)
        val confirmationDialog = AlertDialog.Builder(this).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.alertMessage?.text=getResourceString("error_network_connection")
        confirmationDialogView.alertOk.setOnClickListener {
            confirmationDialog?.dismiss()
            finish()
        }
        confirmationDialog?.show()
    }


    private fun authorizeLogin() {
        hideKeyboard(this)
        showProgressBar()
        val clientKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.APP_KEY)
        if (clientKey.isNullOrEmpty()) {
            Logger.e("Login", getResourceString("error_client_key_missing"))
            showSnackBar(getResourceString("error_client_key_missing"))
            dismissProgressBar()
            return
        }
        if (clientKey != null) {
            ServiceWrapper.getLevel1Token(clientKey, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    val tokenResponse = responseObject as gm.service.model.Model.Response
                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_TOKEN_1, tokenResponse.result)
                    getLanguageList(tokenResponse.result)
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)

                    dismissProgressBar()
                }
            })
        }
    }

    fun getLanguageList(token: String?) {
        var loginUserID: Long = 0
        GMApplication.loginUserId?.let {
            loginUserID = it
        }
        ServiceWrapper.getLanguageList(loginUserID, token!!, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                val responseList = responseObject as Model.LanguageList
                languageList.addAll(responseList.response!!)
                recyclerView?.adapter?.notifyDataSetChanged()
                topLevelLayouts.visibility = View.VISIBLE
                dismissProgressBar()
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                dismissProgressBar()
            }
        })
    }

    fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    fun startLoginActivity() {
        val bundle=intent.extras
        val intent = Intent(this, LoginActivity::class.java)
        bundle?.let { intent.putExtras(bundle) }
        startActivity(intent)
        finish()
    }
    fun startValidationActivity() {
        val intent = Intent(this, ValidateUserActivity::class.java)
        startActivity(intent)
        finish()
    }
}

enum class INDENT_NAVIGATION{
    Home,UserValidation,Login
}
