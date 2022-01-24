package com.gmcoreui.controllers.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.EMAIL
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.MOBILE
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.NAME
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.OTP
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.PASSWORD
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.PIN_CODE
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.QUANTITY
import com.gmcoreui.controllers.ui.GMEditText.FIELD_TYPE.VOTER_ID
import com.gm.R
import com.gm.db.SingleTon
import java.util.*

class GMEditText : AppCompatEditText, GMWidget {
    // Field Type   <!-- Caution: This was mapped in default_attrs.xml-->
    object FIELD_TYPE {
        const val NAME = 1
        const val EMAIL = 2
        const val MOBILE = 3
        const val PASSWORD = 4
        const val PIN_CODE = 5
        const val VOTER_ID = 6
        const val QUANTITY = 7
        const val OTP = 8
    }

    // Password Scheme  <!-- Caution: This was mapped in default_attrs.xml-->
    private object PASSWORD_SCHEMA {
        const val ANY = 1
        const val ALPHA = 2
        const val ALPHA_MIXED_CASE = 3
        const val NUMERIC = 4
        const val ALPHA_NUMERIC = 5
        const val ALPHA_NUMERIC_MIXED_CASE = 6
        const val ALPHA_NUMERIC_SYMBOLS = 7
        const val ALPHA_NUMERIC_MIXED_CASE_SYMBOLS = 8
    }

    // Password Regx patterns 
    object REGX_PATTERNS {
        val EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"//"^\\s*?(.+)@(.+?)\\s*$"
        val QUANTITY = "^[1-9]\\d{2}\$"
        val OTP = "^[0-9]\\d{5}\$"
        val MOBILE_INDIAN = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6-9]\\d{9}\$"//"^[6-9]\d{9}$"
        val LANDLINE_INDIAN = "^[0-9]\\d{7,10}\$"
        val ANY = ".+"
        val ALPHA = "\\w+"
        val ALPHA_MIXED_CASE = "(?=.*[a-z])(?=.*[A-Z]).+"
        val NUMERIC = "\\d+"
        val ALPHA_NUMERIC = "(?=.*[a-zA-Z])(?=.*[\\d]).+"
        val ALPHA_NUMERIC_MIXED_CASE = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+"
        val ALPHA_NUMERIC_SYMBOLS = "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+"
        val ALPHA_NUMERIC_MIXED_CASE_SYMBOLS = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+"
    }

    private val DEFAULT_PASSWORD_MIN = 6
    private val DEFAULT_PASSWORD_MAX = 100
    private val DEFAULT_INDIAN_VOTER_ID_LENGTH = 10

    private var jsonKey: String = ""
    var fieldType: Int = 0
    private var mandatoryField: Boolean = false
    private var passwordMinLength: Int = DEFAULT_PASSWORD_MIN
    private var passwordMaxLength: Int = DEFAULT_PASSWORD_MAX
    private var passwordSchema: Int = PASSWORD_SCHEMA.ANY

    private var mObservers: ArrayList<WidgetObserver>? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    override fun getJsonKey(): String {
        return jsonKey
    }

    override fun getJsonObject(): Any {
        return text.toString()
    }

    override fun registerObserver(observer: WidgetObserver) {
        if (!mObservers!!.contains(observer)) {
            mObservers!!.add(observer)
        }
    }

    override fun removeObserver(observer: WidgetObserver) {
        if (mObservers!!.contains(observer)) {
            mObservers!!.remove(observer)
        }
    }

    override fun notifyObservers() {
        for (observer in mObservers!!) {
            observer.onDataChanged(this, jsonObject)
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (isKeyboardShown(rootView)) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                clearFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
                Handler().postDelayed({
                    //                    validate()
                    requestFocus()
                }, 500)

                return true
            }
        }

        return super.dispatchKeyEvent(event)
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        val SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128

        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        val heightDiff = rootView.bottom - r.bottom
        /* Threshold size: dp to pixels, multiply with display density */
        val isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density

        Log.d(ContentValues.TAG, "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.height + ", rect:" + r)

        return isKeyboardShown
    }

    private fun init(attrs: AttributeSet?) {
        attrs.let {
            mObservers = ArrayList()
            this.addTextChangedListener(textWatcher)
            if (attrs != null) {
                val a = context.theme.obtainStyledAttributes(attrs, R.styleable.GMWidget, 0, 0)
                try {
                    jsonKey = a.getString(R.styleable.GMWidget_jsonkey)!!
                    fieldType = a.getInt(R.styleable.GMWidget_fieldType, 0)
                    mandatoryField = a.getBoolean(R.styleable.GMWidget_mandatoryField, false)
                    passwordMinLength = a.getInt(R.styleable.GMWidget_passwordMinLength, DEFAULT_PASSWORD_MIN)
                    passwordMaxLength = a.getInt(R.styleable.GMWidget_passwordMaxLength, DEFAULT_PASSWORD_MAX)
                    passwordSchema = a.getInt(R.styleable.GMWidget_passwordSchema, PASSWORD_SCHEMA.ANY)
                    if (fieldType == NAME) {
                        inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    }
                } finally {
                    a.recycle()
                }
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val parentView = parent as View
            if (parentView is FrameLayout) {
                val actualParentView = parentView.getParent() as View
                if (actualParentView is TextInputLayout) {
                    actualParentView.error = null
                    actualParentView.isErrorEnabled = false
                }
            }
        }

        override fun afterTextChanged(s: Editable) {

        }
    }

    fun isEmpty(): Boolean {
        return text.isNullOrEmpty()
    }

    fun setErrorIfEmpty(resId: Int): Boolean {
        if (isEmpty()) {
            error = context.resources.getString(resId)
            return false
        }
        return true
    }

    /**
     * Validation part.
     */
    fun validate(): Boolean {
        val value = text.toString().trim()
        if (!basicValidation()) {
            return false
        }
        when (fieldType) {
            NAME -> return validateName(value)
            EMAIL -> return validateEmail(value)
            PASSWORD -> return validatePassword(value)
            MOBILE -> return validateMobileNumber(value)
            VOTER_ID -> return validateVoterId(value)
            PIN_CODE -> return validatePinCode(value)
            QUANTITY -> return validateQuantity(value)
            OTP -> return validateOTP(value)

        }
        return true
    }

    private fun validateOTP(value: String): Boolean {
        val regx = Regex(REGX_PATTERNS.OTP)
        if (!value.trim().matches(regx)) {
            error = "Invalid OTP."
            requestFocus()
            return false
        }
        return true
    }

    private fun validateQuantity(value: String): Boolean {
        val regx = Regex(REGX_PATTERNS.QUANTITY)
        if (!value.trim().matches(regx)) {
            error = "Invalid Quantity."
            requestFocus()
            return false
        }
        return true
    }

    private fun validatePinCode(value: String): Boolean {
        // TODO
        return true
    }

    private fun validateVoterId(value: String): Boolean {
        val voterNoMaxLength = DEFAULT_INDIAN_VOTER_ID_LENGTH
        if (voterNoMaxLength > value.trim().length || voterNoMaxLength < value.trim().length) {
            error = "Invalid voter id."
            requestFocus()
            return false
        }
        val firstSet = value.trim().substring(0, 3)
        val second = value.trim().substring(3)
        val voterIdRegx = Regex(".*\\d+.*", RegexOption.IGNORE_CASE)
        val voterIdRegx2 = Regex("\\d{7}", RegexOption.IGNORE_CASE)
        if (firstSet.matches(voterIdRegx) || !(second.matches(voterIdRegx2))) {
            error = "Invalid voter id."
            requestFocus()
            return false
        }
        return true
    }

    private fun validateMobileNumber(value: String): Boolean {
        val regx = Regex(REGX_PATTERNS.MOBILE_INDIAN)
        if (!value.trim().matches(regx)) {
            error = "Invalid mobile number."
            requestFocus()
            return false
        }
        return true
    }

    private fun validateName(value: String): Boolean {
        // TODO 
        return true
    }

    private fun validatePassword(value: String): Boolean {
        if (passwordMinLength > value.trim().length) {
            error =SingleTon.getResourceStringValue("error_password_length_min") .format(passwordMinLength)
            requestFocus()
            return false
        }
        if (passwordMaxLength < value.trim().length) {
            error =SingleTon.getResourceStringValue("error_password_length_max").format(passwordMaxLength)
            requestFocus()
            return false
        }
        val schemaError = getPasswordSchemaError(passwordSchema)
        if (!TextUtils.isEmpty(schemaError)) {
            error = schemaError
            requestFocus()
            return false
        }
        return true
    }

    private fun validateEmail(value: String): Boolean {
        val emailRegX = Regex(REGX_PATTERNS.EMAIL, RegexOption.IGNORE_CASE)
        if (!value.trim().matches(emailRegX)) {
            error = "Invalid email."
            requestFocus()
            return false
        }
        return true
    }

    private fun getPasswordSchemaError(passwordSchema: Int): CharSequence? {
        val value = text.toString().trim()
        when (passwordSchema) {
            PASSWORD_SCHEMA.ALPHA -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA))) {
                return "Should contain only alphabets."
            }
            PASSWORD_SCHEMA.ALPHA_MIXED_CASE -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA_MIXED_CASE))) {
                return "Should contain both upper and lowercase."
            }
            PASSWORD_SCHEMA.NUMERIC -> if (!value.matches(Regex(REGX_PATTERNS.NUMERIC))) {
                return "Should contain only numbers."
            }
            PASSWORD_SCHEMA.ALPHA_NUMERIC -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA_NUMERIC))) {
                return "Should contain alphabets and numbers."
            }
            PASSWORD_SCHEMA.ALPHA_NUMERIC_MIXED_CASE -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA_NUMERIC_MIXED_CASE))) {
                return "Should contain at-least a uppercase, a lowercase letter " +
                        "and number."
            }
            PASSWORD_SCHEMA.ALPHA_NUMERIC_SYMBOLS -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA_NUMERIC_SYMBOLS))) {
                return "Should contain at-least a letter, a number and a symbol."
            }
            PASSWORD_SCHEMA.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS -> if (!value.matches(Regex(REGX_PATTERNS.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS))) {
                return "Should contain at-least a uppercase, a lowercase letter " +
                        "a number and a symbol."
            }

        }
        return ""
    }

    private fun basicValidation(): Boolean {
        val value = text.toString().trim()
        if (mandatoryField && isEmpty()) {
            error = "This field is required."
            requestFocus()
            return false
        }
        if (value.contains(">") || value.contains("<")) {
            //        if (value.matches(angleBracketRegX)) {
            error = "Invalid chars('<' or '>') are not allowed"
            requestFocus()
            return false
        }
        return true
    }
}
