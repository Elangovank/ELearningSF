package com.gm.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.core.utils.AppPreferences
import com.core.utils.Logger
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.ServiceWrapper
import com.gm.db.SingleTon
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gmcoreui.controllers.BaseActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_validate.*


class ValidateUserActivity : BaseActivity() {
    companion object {
        const val ARG_FARMER_CODE = "ARG_FARMER_CODE"
        const val ARG_IS_FORGOT_PASSWORD = "ARG_IS_FORGOT_PASSWORD"

    }

    private val validateButtonTag = "validate_farm_code"
    private val sendOtpButtontag = "send_otp"
    private val submitButtontag = "submit_button"

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

fun setResourceString()
{
    changeLanguage?.text=getResourceString("change_language")
    messageTextView?.text=getResourceString("message_part2")
    otpEditText?.hint=getResourceString("otp")
    rememberTextView?.text=getResourceString("remember_me")
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate)
        setResourceString()
        setResourceStringValue()
        farmerCodeEditText?.imeOptions = EditorInfo.IME_ACTION_DONE
        submitButton?.tag = validateButtonTag
        submitButton?.text = getResourceString("validate_farm_code")
        otpEditText?.visibility = View.GONE
        otpEditText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
        otpEditText?.inputType = InputType.TYPE_CLASS_NUMBER
        //resendOtpButton?.visibility = View.GONE

        if (intent?.getBooleanExtra(ARG_IS_FORGOT_PASSWORD, false) ?: false) {
            farmerCodeEditText?.text = SpannableStringBuilder(intent?.getStringExtra(ARG_FARMER_CODE)
                    ?: "")

            setOTPScreen()
        }
        submitButton?.setOnClickListener {
            validateData()
        }
        farmerCodeEditText?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    validateData()
                }
                return false
            }
        })
        otpEditText?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    validateData()
                }
                return false
            }
        })
        changeLanguage.setOnClickListener {
            startLanguageActivity()
        }
    }



    fun setResourceStringValue()
    {

            welcomeTextView?.text=getResourceString("welcome_label")
            registerLabelTextView?.text=getResourceString("label_registered")
            nonRegisterLabelTextView?.text=getResourceString("label_non_registered")
            nonRegisterDetailTextView?.text=getResourceString("message_non_reg")
            registerDetailTextView?.text=getResourceString("message_reg")
            farmerCodeEditText?.hint=getResourceString("farm_code")
            submitButton?.text=getResourceString("send_otp")
      //  }

    }

    fun validateData() {
        if (submitButton?.tag.toString().equals(validateButtonTag) && validateEditText(farmerCodeEditText, getResourceString("enter_farm_code"))) {
            authorizeLogin()
        } else if (submitButton?.tag.toString().equals(sendOtpButtontag)) {
            authorizeLogin()
        } else if (getResourceString("enter_otp").let { validateEditText(otpEditText, it) }) {
            if (otpEditText?.text.toString().length != 6) {
                otpEditText?.error = getResourceString("enter_otp")
                otpEditText?.requestFocus()
            } else {
                authorizeLogin()
            }
        }
    }

    fun validateEditText(editText: EditText?, error: String): Boolean {
        if (editText?.text.isNullOrEmpty()) {
            editText?.error = error
            editText?.requestFocus()
            return false
        }
        return true
    }


    private fun authorizeLogin() {
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
                AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_TOKEN_1, tokenResponse.result)
                tokenResponse.result?.let {
                    submitActions(it)
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)

                dismissProgressBar()
            }
        })
    }

    fun setOTPScreen() {
        submitButton?.tag = sendOtpButtontag
        submitButton?.text =getResourceString("send_otp")
        otpEditText?.hint=getResourceString("otp")
        farmerCodeEditText?.isEnabled = false
    }

    fun submitActions(level1Token: String) {
        if (submitButton?.tag.toString().equals(validateButtonTag)) {
            ServiceWrapper.validateFarm(level1Token, farmerCodeEditText.text.toString(), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.ValidateUserResponse) {
                        responseObject.response?.let {
                            it.isRegistered?.let { isRegistered ->
                                if (isRegistered) {
                                    gotoLoginActivity()
                                } else {
                                    setOTPScreen()
                                }
                            }
                        }
                    }
                    dismissProgressBar()
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)

                    dismissProgressBar()
                }
            })
        } else if (submitButton?.tag.toString().equals(sendOtpButtontag)) {
            ServiceWrapper.getOtp(level1Token, farmerCodeEditText.text.toString(), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    submitButton?.tag = submitButtontag
                    submitButton?.text = getResourceString("submit")
                    otpEditText?.visibility = View.VISIBLE
                   // resendOtpButton?.visibility = View.VISIBLE
                    farmerCodeEditText?.nextFocusDownId = otpEditText.id
                    dismissProgressBar()
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)
                    dismissProgressBar()
                }
            })
        } else {
            ServiceWrapper.getVerifyOtp(level1Token, farmerCodeEditText.text.toString(), otpEditText.text.toString(), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.LoginResponse) {
                        responseObject.loginData?.let {
                            GMApplication.loginUserId = it.userId ?: 0
                            val loginData = Gson().toJson(it).toString()
                            AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_LOGIN_DATA, loginData)
                            goToPasswordActivity()
                        }
                    }
                    dismissProgressBar()
                }

                override fun onRequestFailed(responseObject: String) {
                        showErrorSnackBar(responseObject)
                        dismissProgressBar()
                }
            })
        }
    }

    fun gotoLoginActivity() {
        val bundle = Bundle()
        val intent = Intent(this, LoginActivity::class.java)
        bundle.putString(ARG_FARMER_CODE, farmerCodeEditText?.text?.toString())
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }


    fun goToPasswordActivity() {
        val intent = Intent(this, PasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

        if (submitButton?.tag.toString().equals(validateButtonTag)) {
            super.onBackPressed()
        } else if (submitButton?.tag.toString().equals(sendOtpButtontag)) {
            farmerCodeEditText?.isEnabled = true
            farmerCodeEditText?.imeOptions = EditorInfo.IME_ACTION_DONE
            submitButton?.tag = validateButtonTag
            submitButton?.text =getResourceString("validate_farm_code")
        } else {
            submitButton?.tag = sendOtpButtontag
            submitButton?.text =getResourceString("send_otp")
            otpEditText?.visibility = View.GONE
          //  resendOtpButton?.visibility = View.GONE
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    fun startLanguageActivity() {
        val intent = Intent(this, LanguageActivity::class.java)
        intent.putExtra(LanguageActivity.NAVIGATION, INDENT_NAVIGATION.UserValidation)
        startActivity(intent)
        finish()
    }
}