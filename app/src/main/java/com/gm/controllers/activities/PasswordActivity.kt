package com.gm.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.core.utils.AppPreferences
import com.gm.R
import com.gm.WebServices.ServiceWrapper
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gmcoreui.controllers.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_password.*


class PasswordActivity : BaseActivity() {

    companion object {
        const val ARG_USER_DETAILS = "ARG_USER_DETAILS"
    }

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }


    fun setResourceString()
    {
        changeLanguage?.text=getResourceString("password_creation")
        farmerCodeEditText?.hint=getResourceString("farm_id")
        mobileNumberEditText?.hint=getResourceString("mobileNumberEditText")
        passwordEditText?.hint=getResourceString("creatre_password")
        reEnteredPasswordEditText?.hint=getResourceString("re_enter_password")
        submitButton?.text=getResourceString("submit")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_password)
        setResourceString()
        farmerCodeEditText?.isEnabled = false
        mobileNumberEditText?.isEnabled = false
        val userInfo = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.KEY_LOGIN_DATA, "")
        if (!userInfo.isNullOrEmpty()){
            val userDetails = Gson().fromJson(userInfo, Model.LoginData::class.java)
            if (userDetails is Model.LoginData) {
                farmerCodeEditText?.text = SpannableStringBuilder(userDetails.farmCode?:"")
                userDetails?.mobileNumber?.let {
                    mobileNumberEditText?.text = SpannableStringBuilder(it)

                }
            }
        }
        submitButton?.setOnClickListener {
            checkInputs()
        }
        reEnteredPasswordEditText?.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                hideKeyboard(this)
                checkInputs()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun checkInputs() {
        val password = passwordEditText?.text?.toString()
        if (password.isNullOrEmpty()) {
            passwordEditText?.error = getResourceString("error_field_required")
            passwordEditText?.requestFocus()
        } else {
            val reEnteredPassword = reEnteredPasswordEditText?.text?.toString()
            if (reEnteredPassword.isNullOrEmpty()) {
                reEnteredPasswordEditText?.error =getResourceString("error_field_required")
                reEnteredPasswordEditText?.requestFocus()
            } else if (password != reEnteredPassword) {
                reEnteredPasswordEditText?.error =getResourceString("error_password_mismatch")
                reEnteredPasswordEditText?.requestFocus()
            } else {
                authorizeLogin(password!!)
            }
        }
    }

    private fun authorizeLogin(password: String) {
        hideKeyboard(this)
        showProgressBar()
        val clientKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.APP_KEY)
        if (clientKey.isNullOrEmpty()) {
            showSnackBar(getResourceString("error_client_key_missing"))
            dismissProgressBar()
            return
        }
        ServiceWrapper.getLevel1Token(clientKey!!, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                val tokenResponse = responseObject as gm.service.model.Model.Response
                savePassowrd(tokenResponse.result, password)
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)

                dismissProgressBar()
            }
        })
    }

    fun savePassowrd(token: String?, password: String) {
        if (!token.isNullOrEmpty()) {
            ServiceWrapper.savePassowrd(token!!, password, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    dismissProgressBar()
                    loadNext()
                }

                override fun onRequestFailed(responseObject: String) {
                    dismissProgressBar()
                    showErrorSnackBar(responseObject)
                }
            })
        }else{
            dismissProgressBar()
            showSnackBar(getResourceString("token_missing"))
        }
    }

    fun loadNext() {
        val bundle = Bundle()
        val intent = Intent(this, LoginActivity::class.java)
        bundle.putString(ValidateUserActivity.ARG_FARMER_CODE, farmerCodeEditText?.text?.toString())
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}
