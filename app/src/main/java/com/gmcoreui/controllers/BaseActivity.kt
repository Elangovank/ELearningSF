package com.gmcoreui.controllers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.core.utils.AppPreferences
import com.core.utils.Logger
import com.gm.GMApplication
import com.gm.R
import com.gm.db.SingleTon
import com.gm.utilities.Downloader
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gm.utilities.LocaleHelper
import com.gmcoreui.controllers.fragments.BaseFragment
import com.gmcoreui.controllers.ui.GMProgressDialog
import com.gmcoreui.listener.ToolbarUpdateListener
import com.gmcoreui.utils.ActivityUtils
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_CAMERA
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_LOCATION
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_MICRO_PHONE
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_PHONE_CALL
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_PHONE_STATE
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_READ_SMS
import com.gmcoreui.utils.Keys.PERMISSION_REQUEST_STORAGE
import com.gmcoreui.utils.PermissionUtils
import com.google.android.material.snackbar.Snackbar

/**
 * Base class for all activity present in compass application
 */
abstract class BaseActivity : AppCompatActivity(), BaseFragment.OnFragmentInteractionListener, ToolbarUpdateListener {


    private val STORAGE_PERMISSIONS = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

    private var toolBar: Toolbar? = null

    override fun updateTitle(title: String) {
        setupToolbar(title)
    }

    private var progress: GMProgressDialog? = null
    private var mProgressBar: GMProgressDialog? = null

    companion object {
        const val KEY_REQUEST_TYPE = "Request Type"
        val TAG = BaseActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        overridePendingTransition(R.anim.activity_open_translate, android.R.anim.fade_out)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(android.R.anim.fade_in, R.anim.activity_close_translate)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentInteraction(fragment: BaseFragment, any: Any) {
        // I'm offline
    }

    /**
     * Loads the fragment in to the container.
     *
     * @param container container to which fragment is loaded.
     * @param fragment  fragment which is to be load.
     */
    fun loadFragment(container: Int, fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager?.beginTransaction()?.replace(container, fragment)?.commit()
    }

    /**
     * Loads the fragment in to the container
     *
     * @param container container to which fragment is loaded.
     * @param fragment fragment which is to be load.
     * @param tag tag used for the back-stack
     */
    fun loadFragment(container: Int, fragment: androidx.fragment.app.Fragment, tag: String) {
        supportFragmentManager.beginTransaction().add(container, fragment)
                .addToBackStack(tag).commit()
    }

    /**
     * Initializes the toolbar as action bar and sets the title.
     *
     * @param toolbar
     * @param title
     */
    fun setupToolbar(title: String?) {
        toolBar?.let {
            title?.let { toolBar!!.title = title }
            setSupportActionBar(it)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    fun showProgressDialog(message: String) {
        try {
        this.let {
            if (progress == null) {
                progress = GMProgressDialog(this)
            }
            progress?.setCancelable(false)
            progress?.setMessage(message)
            progress?.show()
        }
        }catch (e:Exception){}
    }



    fun getResourceString(str: String): String {
 /*       val resourceSting = SingleTon.readThefile()
        var keyString:String?=null
        if (resourceSting.size != 0) {
            if (resourceSting.containsKey(str))
            {
                keyString=resourceSting.get(str)
            }else{
                keyString="-"
            }
        }
        return keyString?:"-"*/
        return SingleTon.getResourceStringValue(str)
    }


    fun showErrorSnackBar(responseObject:String){
        if (IntentUtils.checkResponseObject(responseObject)) {
            showSnackBar(IntentUtils.assignErrorLanguageForId(resources, responseObject.toInt())?:"")
        } else {
            showSnackBar(responseObject)
        }
    }

    /**
     * Dismiss the progress dialog if shown.
     */
    fun dismissProgressDialog() {
        try {
        if (isFinishing || isDestroyed) {
            return
        }
        progress?.dismiss()
        }catch (e:Exception){}
    }

    fun showSnackBar(message: String) {
        val view = window?.decorView?.findViewById(android.R.id.content) as ViewGroup
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    fun checkStoragePermission(): Boolean {
        if (!PermissionUtils.isPermissionsGranted(this, STORAGE_PERMISSIONS)) {
            requestStoragePermission()
            return false
        } else {
            return true

        }
    }




    fun checkAudioRecodPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestMicroPhonePermission()
            return false
        } else {
            return true

        }
    }

    fun checkPhoneStatePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPhoneStatePermission()
            return false
        } else {
            return true

        }
    }


    fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkPhoneCallPermission() {
        try {
            // Check phone call permission
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPhoneCallPermission()
            } else {
                onPermissionGranted(PERMISSION_REQUEST_PHONE_CALL)
            }
        } catch (e: Exception) {
            Logger.e("", e.localizedMessage)
        }
    }


    fun checkPhoneCallsPermission(): Boolean {
        if (!PermissionUtils.isPermissionsGranted(this, STORAGE_PERMISSIONS)) {
            requestStoragePermission()
            return false
        } else {
            return true

        }
    }

    private fun checkSMSPermission() {
        try {
            // Check read/receive sms permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_READ_SMS)
            }
        } catch (e: Exception) {
            Logger.e("", e.localizedMessage)
        }
    }

    fun checkLocationPermission() {
        try {
            // Check location permission
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission()
            } else {
                onPermissionGranted(PERMISSION_REQUEST_LOCATION)
            }
        } catch (e: Exception) {

        }
    }


    fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, PERMISSION_REQUEST_STORAGE)
    }

    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA)
    }


    fun requestMicroPhonePermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_MICRO_PHONE)
    }

    fun requestPhoneStatePermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_PHONE_STATE),
                PERMISSION_REQUEST_PHONE_STATE)
    }

    private fun requestPhoneCallPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),
                PERMISSION_REQUEST_PHONE_CALL)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_STORAGE)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_STORAGE)

            }
            PERMISSION_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted(PERMISSION_REQUEST_CAMERA)
                } else {
                    onPermissionDenied(PERMISSION_REQUEST_CAMERA)
                }
            }
            PERMISSION_REQUEST_READ_SMS -> if (!grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_STORAGE)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_STORAGE)
            }
            PERMISSION_REQUEST_LOCATION -> if (!grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_LOCATION)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_LOCATION)
            }
            PERMISSION_REQUEST_PHONE_CALL -> if (!grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_PHONE_CALL)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_PHONE_CALL)
            }
            PERMISSION_REQUEST_MICRO_PHONE -> if (!grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_MICRO_PHONE)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_MICRO_PHONE)
            }
            PERMISSION_REQUEST_PHONE_STATE -> if (!grantResults.isEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(PERMISSION_REQUEST_PHONE_STATE)
            } else {
                onPermissionDenied(PERMISSION_REQUEST_PHONE_STATE)
            }

        }
    }

    fun hideKeyboard(activity: Activity) {
        ActivityUtils.hideKeyboard(activity)
    }

    open fun showProgressBar() {
        try {
            mProgressBar = GMProgressDialog(this)
            if (mProgressBar != null) {
                mProgressBar?.disableMessageView()
                mProgressBar!!.setCancelable(false)
                mProgressBar!!.show()
            }
        } catch (e: Exception) { }
    }

    open fun showCancelableProgressBar() {
        try {
        mProgressBar = GMProgressDialog(this)
        mProgressBar?.disableMessageView()
        mProgressBar!!.setCancelable(true)
        mProgressBar!!.show()
        } catch (e: Exception) { }
    }

    open fun dismissProgressBar() {
        try {
        mProgressBar?.dismiss()
        } catch (e: Exception) { }
    }

    abstract fun onPermissionGranted(requestCode: Int)

    abstract fun onPermissionDenied(requestCode: Int)
    

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase!!, IntentUtils.assignLanguage((AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId, 0L)
                ?: 0L).toInt())!!))
    }
}
