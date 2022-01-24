package com.gmcoreui.controllers.fragments

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.core.utils.AppPreferences
import com.gm.GMApplication
import com.gm.R
import com.gm.controllers.activities.HomeActivity
import com.gm.db.SingleTon
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.ui.CustomDialog
import com.gmcoreui.controllers.ui.GMProgressDialog

abstract class GMBaseFragment : PhotoFragment(),
        Toolbar.OnMenuItemClickListener,
        SearchView.OnQueryTextListener {

    private var mProgressBar: GMProgressDialog? = null
    protected var syncFromOnline: Boolean = true

    open fun initToolbar(title: String, isMainFragment: Boolean = false) {
        initToolbar(-1, title, isMainFragment)
    }

    open fun initToolbar(menuId: Int, title: String, isMainFragment: Boolean = false) {
        dismissSnackBar()
        val toolbar = findViewById<Any>(R.id.toolbar) as? Toolbar
        if (activity is HomeActivity && isMainFragment) {
            toolbar?.setNavigationIcon(R.drawable.ic_menu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar?.navigationIcon?.setTint(resources.getColor(R.color.white))
            }
            toolbar?.title = title
            toolbar?.setNavigationOnClickListener {
                dismissSnackBar()
                (activity as HomeActivity).onToolbarClicked()
            }
        } else {
            toolbar?.setNavigationIcon(R.drawable.ic_back)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar?.navigationIcon?.setTint(resources.getColor(R.color.white))
            }
            toolbar?.title = title
            toolbar?.setNavigationOnClickListener {
                dismissSnackBar()
                activity?.onBackPressed()
            }
        }
        if (menuId != -1) {
            toolbar?.inflateMenu(menuId)
            toolbar?.setOnMenuItemClickListener(this)
        }
    }


    fun initSearchView(searchView: SearchView) {
        searchView.let {
            val searchText = it.findViewById(
                    androidx.appcompat.R.id.search_src_text) as TextView
            searchText.setTextColor(resources.getColor(R.color.black))
            val typeface = ResourcesCompat.getFont(context!!, R.font.worksans_regular)
            searchText.typeface = typeface
            // Search button
            val searchIcon = it.findViewById(
                    androidx.appcompat.R.id.search_mag_icon) as ImageView
            val searchDrawable = searchIcon.drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                searchDrawable.setTint(Color.GRAY)
            } //Whatever color you want it to be
            searchIcon.setImageDrawable(searchDrawable)
            // Close button
            val closeImgView = it.findViewById(
                    androidx.appcompat.R.id.search_close_btn) as ImageView
            val closeDrawable = closeImgView.drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                closeDrawable.setTint(Color.GRAY)
            } //Whatever color you want it to be
            closeImgView.setImageDrawable(closeDrawable)
            // Bottom line
            val bottomLine = it.findViewById(androidx.appcompat.R.id
                    .search_plate) as View
            bottomLine.setBackgroundColor(resources.getColor(R.color.transperent))

            searchView.setOnQueryTextListener(this)
        }
    }

    open fun showSuccessDialog(message: String, listener: DialogInterface.OnClickListener) {
        showDialog(true, message, listener)
    }

    open fun showErrorDialog(message: String, listener: DialogInterface.OnClickListener) {
        showDialog(false, message, listener)
    }

    open fun showDialog(isSuccess: Boolean, message: String, listener: DialogInterface.OnClickListener) {
        CustomDialog(context)
                .setType(if (isSuccess) CustomDialog.ALERT_TYPE_SUCCESS else CustomDialog.ALERT_TYPE_ERROR)
                .setMessage(message)
                .setPositiveButton(DialogInterface.OnClickListener { dialog, which ->
                    listener.onClick(dialog, which)
                    dialog.dismiss()
                }).show()
    }

    override fun onResume() {
        super.onResume()
        GMApplication.loginUserId = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, 0L)
                ?: 0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GMApplication.loginUserId = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, 0L)
                ?: 0L
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        GMApplication.loginUserId = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, 0L)
                ?: 0L
    }

    open fun showProgressBar() {
        try {
            dismissSnackBar()
            dismissProgressBar()
            activity?.let { mProgressBar = GMProgressDialog(activity!!) }
            mProgressBar?.disableMessageView()
            mProgressBar?.setCancelable(false)
            mProgressBar?.show()
        } catch (e: Exception) {
        }
    }

    open fun dismissProgressBar() {
        try {
            mProgressBar?.let {
                if (it.isShowing && isVisible) {
                    it.dismiss()
                }
            }
            mProgressBar?.dismiss()
        } catch (e: Exception) {
        }

    }

    override fun showCameraPreview() {
        // I'm offline
    }

    override fun setResultImage(outputFileUri: Uri) {
        // I'm offline
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        // I'm Off duty
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        // I'm Off duty
        return false
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        // I'm offline
        return true
    }

    /* override fun onPermissionDenied(requestCode: Int) {
         super.onPermissionDenied(requestCode)
     }

     override fun onPermissionGranted(requestCode: Int) {
         super.onPermissionGranted(requestCode)
     }
 */
    fun setErrorMessage(editText: EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }

    fun isValidEditText(editText: EditText): Boolean {
        if (editText.text.isNullOrBlank()) {
            setErrorMessage(editText,SingleTon.getResourceStringValue("error_field_required"))
            return false
        }
        return true
    }

    fun isValidEditText(editText: EditText, error: String): Boolean {
        if (editText.text.isNullOrBlank()) {
            setErrorMessage(editText, error)
            return false
        }
        return true
    }

    fun isValidSpinner(spinner: Spinner): Boolean {
        if (spinner.selectedItemPosition == 0) {
            val selectedView = spinner.selectedView
            if (selectedView != null && selectedView is TextView) {
                spinner.requestFocus()
                selectedView.error =SingleTon.getResourceStringValue("error_field_required")
                spinner.performClick()
            }
            return false
        }
        return true
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
}
