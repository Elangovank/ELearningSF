package com.gmcoreui.controllers.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.gm.R
import com.gm.db.SingleTon
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.controllers.fragments.BaseFragment.OnFragmentInteractionListener
import com.gmcoreui.listener.ToolbarUpdateListener
import com.gmcoreui.utils.ActivityUtils
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
abstract class BaseFragment : androidx.fragment.app.Fragment() {

    protected var mListener: OnFragmentInteractionListener? = null

    private val progress: Dialog? = null

    var snackbar: Snackbar? = null

    /**
     * Sets the base context with the locale selected in our app.
     *
     * @param context base context.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.overridePendingTransition(R.anim.activity_open_translate, android.R.anim.fade_out)
        retainInstance = true
    }

    protected fun <T> findViewById(id: Int): View? {
        return if (view != null) {
            view!!.findViewById(id)
        } else null
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onPause() {
        super.onPause()
        activity?.overridePendingTransition(android.R.anim.fade_in, R.anim.activity_close_translate)
    }

    /**
     * Sets the toolbar title via {@link: ToolbarUpdateListener}
     */
    fun setToolbarTitle(title: String?) {
        title?.let {
            if (activity is ToolbarUpdateListener) {
                (activity as ToolbarUpdateListener).updateTitle(title)
            }
        }
    }

    fun showProgressDialog(message: String) {
        activity?.let {
            val baseActivity = it as BaseActivity
            baseActivity.showProgressDialog(message)
        }
    }

    /**
     * Dismiss the progress dialog if shown.
     */
    open fun dismissProgressDialog() {
        try {
            activity?.let {
                if (isAdded) {
                    val baseActivity = it as BaseActivity
                    baseActivity.dismissProgressDialog()
                }
            }
        } catch (e: Exception) {
        }

    }

    fun showSnackBar(message: String) {
        try {
            activity?.let {
                if (message !=SingleTon.getResourceStringValue("no_record_found") && !message?.isNullOrEmpty() && message != SingleTon.getResourceStringValue("error_server")) {
                    val view = it.window?.decorView?.findViewById<View>(android.R.id.content) as ViewGroup
                    snackbar = Snackbar.make(view, Html.fromHtml(message.plus("<font color=#000000> </font>")), Snackbar.LENGTH_LONG)
                    /*var color=snackbar?.view
                    color?.setBackgroundColor(context?.resources?.getColor(R.color.colorPrimary)!!)*/
                    snackbar?.setAction(SingleTon.getResourceStringValue("ok"), object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            snackbar?.dismiss()
                        }
                    })
                    snackbar?.setDuration(10000)
                    snackbar?.show()
                }
            }
        } catch (e: Exception) {
        }
    }

    fun showErrorSnackBar(responseObject: String) {
        activity?.let {
            if (isAdded) {
                if (activity is BaseActivity) {
                    (activity as BaseActivity).showErrorSnackBar(responseObject)
                }
            }
        }

    }


    fun dismissSnackBar() {
        try {
            snackbar?.dismiss()
        } catch (e: Exception) {
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(fragment: BaseFragment, any: Any)
    }

    fun isActivityAvailable(context: Context, intent: Intent): Boolean {
        return context.packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    fun hideKeyboard(activity: Activity) {
        ActivityUtils.hideKeyboard(activity)
    }

    fun sendScroll(scrollView: ScrollView) {
        val handler = Handler()
        Thread(Runnable {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
            }

            handler.post(Runnable { scrollView.fullScroll(View.FOCUS_DOWN) })
        }).start()
    }

    abstract fun onPermissionGranted(requestCode: Int)

    abstract fun onPermissionDenied(requestCode: Int)
}
