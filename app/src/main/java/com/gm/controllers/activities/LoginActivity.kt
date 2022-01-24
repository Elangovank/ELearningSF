package com.gm.controllers.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.core.utils.AppPreferences
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.db.SingleTon
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gm.utilities.LocaleHelper
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.utils.Keys
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {
    var farmerCode: String? = ""
    var password: String? = ""
    var isChecked: Boolean? = false
    private var loginPreferences: SharedPreferences? = null
    private var loginPrefsEditor: SharedPreferences.Editor? = null
    private var saveLogin: Boolean? = null

    companion object {

     //   private var resourceString = SingleTon.readThefile()
    }

    override fun onPermissionGranted(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_PHONE_STATE) {
            checkPermissionAndLogin()
        }
    }

    override fun onPermissionDenied(requestCode: Int) {
        if (requestCode == Keys.PERMISSION_REQUEST_PHONE_STATE) {
          
                getResourceString("error_in_permission").let { showSnackBar(it) }
            makeSubmitButtonClickable()
        }
    }

    private fun setTheResourceString() {
        loginTextView?.text = getResourceString("login")
        farmerCodeEditText?.hint =getResourceString("farm_code")
        otpEditText?.hint = getResourceString("otp")
        resendOtpButton?.text = getResourceString("resend_otp")
        changeLanguage?.text = getResourceString("change_language")
        forgotPasssword?.text =getResourceString("forgot_password")
        rememberTextView?.text=getResourceString("remember_me")
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Full screen window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)
        setTheResourceString()
        farmerCode = intent?.getStringExtra(ValidateUserActivity.ARG_FARMER_CODE)
        farmerCodeEditText?.text = SpannableStringBuilder(farmerCode ?: "")
        farmerCodeEditText?.isEnabled = false
        resendOtpButton?.visibility = View.GONE
        forgotPasssword?.visibility = View.VISIBLE
        submitButton?.text = getResourceString("login")
        remembermeLayout.visibility = View.VISIBLE
        otpEditText?.hint = ""
        otpEditText?.setHint(getResourceString("password"))
        otpEditText?.hint = getResourceString("password")
        otpEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences?.edit()
        saveLogin = loginPreferences?.getBoolean("saveLogin", false)
        if (saveLogin == true) {
            farmerCodeEditText.setText(loginPreferences?.getString("username", ""));
            otpEditText.setText(loginPreferences?.getString("password", ""));
            checked.visibility = View.VISIBLE
            ischecked.visibility = View.GONE
        } else {
            checked.visibility = View.GONE
            ischecked.visibility = View.VISIBLE
        }


        ischecked.setOnClickListener {
            isChecked = true
            ischecked.visibility = View.GONE
            checked.visibility = View.VISIBLE
        }
        checked.setOnClickListener {
            isChecked = false
            ischecked.visibility = View.VISIBLE
            checked.visibility = View.GONE
        }
        submitButton?.setOnClickListener {
            submitAction()
        }
        changeLanguage?.setOnClickListener {
            startLangaugeActivity()
        }
        otpEditText?.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                submitAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        forgotPasssword?.setOnClickListener {
            startForgetPasswordActivity()
        }

    }

    fun submitAction() {
        hideKeyboard(this)
        makeSubmitButtonClickable(false)

        validateLogin()

    }

    fun makeSubmitButtonClickable(status: Boolean = true) {
        submitButton?.isClickable = status
    }


    override fun onBackPressed() {
        goToValidationActivity()
        super.onBackPressed()
    }

    fun goToValidationActivity() {
        val intent = Intent(this, ValidateUserActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toSaveLanguage() {

        GMApplication.languageID?.let {
            ServiceWrapper.saveLanguage(it, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.LanguageResponse1) {
                        dismissProgressBar()
                        makeSubmitButtonClickable()
                        startHomeActivity()
                        // finish()
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    dismissProgressDialog()
                    showErrorSnackBar(responseObject)
                    makeSubmitButtonClickable()
                }
            })
        }
    }


    fun setRememberMe() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(farmerCodeEditText.getWindowToken(), 0)

        farmerCode = farmerCodeEditText.getText().toString()
        password = otpEditText.getText().toString()

        if (isChecked!!) {
            loginPrefsEditor?.putBoolean("saveLogin", true)
            loginPrefsEditor?.putString("username", farmerCode)
            loginPrefsEditor?.putString("password", password)
            loginPrefsEditor?.commit()
        } else {
            loginPrefsEditor?.clear()
            loginPrefsEditor?.commit()
        }
    }

    private fun authorizeLogin() {
        hideKeyboard(this)
        val clientKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.APP_KEY)
        if (clientKey.isNullOrEmpty()) {
            //Logger.e("Login", getString(R.string.error_client_key_missing))
            getResourceString("error_client_key_missing")?.let { showSnackBar(it) }
            makeSubmitButtonClickable()
            dismissProgressBar()
            return
        }
        ServiceWrapper.getLevel1Token(clientKey!!, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                val tokenResponse = responseObject as gm.service.model.Model.Response
                AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_TOKEN_1, tokenResponse.result)
                checkLogin(tokenResponse.result)
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                makeSubmitButtonClickable()
                dismissProgressBar()
            }
        })
    }

    private fun checkLogin(token: String?) {
        if (!(token.isNullOrEmpty() || farmerCode.isNullOrEmpty() || password.isNullOrEmpty())) {
            ServiceWrapper.login(token, farmerCode!!, password!!, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    val response = responseObject as gm.service.model.Model.Response
                    val loginResponse = Gson().fromJson(response.result, Model.LoginResponse::class.java)
                    if (loginResponse?.loginData != null) {
                        loginResponse?.loginData?.let {
                            if (!it.loginToken.isNullOrEmpty()) {
                                try {
                                    val loginData = Gson().toJson(it).toString()
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_LOGIN_DATA, loginData)
                                    AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, it.userId
                                            ?: 0)
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_TOKEN_2, it.loginToken
                                            ?: "")
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.userName, it.name
                                            ?: "")
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.farmCode, it.farmCode
                                            ?: "")
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.LATITUDE, it.latitude
                                            ?: "")
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.LONGITUDE, it.longitude
                                            ?: "")
                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.Village, it.village
                                            ?: "")

                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.Village, it.village
                                            ?: "")

                                    AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.postalCode, it.postalCode
                                            ?: "")
                                    /*  AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.languageId, it.languageId
                                              ?: 0)*/
                                    clearOfflineData(it.userId ?: 0)

                                    Log.i("token", token)

                                    GMApplication.level2Token = it.loginToken!!
                                    GMApplication.loginUserId = it.userId ?: 0
                                    GMApplication.userName = it.name ?: ""
                                    GMApplication.farmCode = it.farmCode ?: ""
                                    //GMApplication.languageID = it.languageId ?: 0
                                    var languageToLoad = IntentUtils.assignLanguage(GMApplication.languageID?.toInt())
                                    LocaleHelper.setLocale(applicationContext, languageToLoad)
                                    setRememberMe()
                                    toSaveLanguage()

                                } catch (e: Exception) {
                                    Log.i("Exceptioonndnvnf11", e.printStackTrace().toString())

                                }
                            }
                        }
                    } else {
                        showInvalidUserAlert()
                    }
                    makeSubmitButtonClickable()


                }

                override fun onRequestFailed(responseObject: String) {
                    makeSubmitButtonClickable()
                    dismissProgressBar()
                    showErrorSnackBar(responseObject)

                }
            })
        } else {
            dismissProgressBar()
            makeSubmitButtonClickable()
            getResourceString("missing_data")?.let { showSnackBar(it) }
        }
    }


    fun clearOfflineData(id: Long) {
        try {
            val existingUser = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.exsistingLoginUserId, 0)
                    ?: 0
            if (!existingUser.equals(id)) {
                DataProvider.clearDatabase()
            }
            AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.exsistingLoginUserId, id)

        } catch (e: Exception) {
            Log.i("Exceptioonndnvnf", e.printStackTrace().toString())

        }
    }


    fun startLangaugeActivity() {
        val intent = Intent(this, LanguageActivity::class.java)
        intent.putExtra(ValidateUserActivity.ARG_FARMER_CODE, farmerCode)
        intent.putExtra(LanguageActivity.NAVIGATION, INDENT_NAVIGATION.Login)
        startActivity(intent)
        finish()
    }

    fun showInvalidUserAlert() {
      getResourceString("invalid_user")?.let { showSnackBar(it) }
    }

    private fun validateLogin() {
        password = otpEditText?.text.toString()
        if (password.isNullOrEmpty()) {
            otpEditText?.error =  getResourceString("error_field_required")
            otpEditText?.requestFocus()
            makeSubmitButtonClickable()
        } else {
            checkPermissionAndLogin()
        }
    }


    fun checkPermissionAndLogin() {
        // if(checkPhoneStatePermission()){
        //    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //    val deviceId:String
        /*  if(Build.VERSION.SDK_INT < 26){
              deviceId=telephonyManager.deviceId
          }else{
              deviceId=telephonyManager.imei
          }*/
        showProgressBar()
        authorizeLogin()
        //  }
    }


    private fun startForgetPasswordActivity() {
        val bundle = Bundle()
        bundle.putBoolean(ValidateUserActivity.ARG_IS_FORGOT_PASSWORD, true)
        bundle.putString(ValidateUserActivity.ARG_FARMER_CODE, farmerCode)
        val intent = Intent(this, ValidateUserActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }


    fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
