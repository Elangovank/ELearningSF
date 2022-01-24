package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.gm.R
import com.gmcoreui.controllers.ui.GMProgressDialog

class PdfViewerFragment : DialogFragment() {
    private var mProgressBar: GMProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("PATH").let {
            startWebView("https://docs.google.com/viewer?embedded=true&url=${it}")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressBar()
    }

    protected fun findViewById(id: Int): View? {
        return if (view != null) {
            view!!.findViewById(id)
        } else null
    }

    private fun startWebView(url: String) {
        showProgressBar()
        val webView = findViewById(R.id.pdfView) as WebView?
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
            /*override fun onPageFinished(view: WebView, url: String) {
                dismissProgressBar()
                setVisiblity(true)
            }*/

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webView?.loadUrl("javascript:(function() { " + "document.querySelector('[role=\"toolbar\"]').remove();})()")
                dismissProgressBar()

            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                dismissProgressBar()
            }
        }
        webView?.loadUrl(url)

    }

    fun showProgressBar() {
        dismissProgressBar()
        mProgressBar = GMProgressDialog(activity!!)
        mProgressBar?.disableMessageView()
        mProgressBar?.setCancelable(false)
        mProgressBar?.show()
    }

    fun dismissProgressBar() {
        mProgressBar?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        mProgressBar?.dismiss()
    }


}
