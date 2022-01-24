package com.gmcoreui.controllers.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.gm.R
import com.gm.db.SingleTon
import com.gm.utilities.ImageUtils
import kotlinx.android.synthetic.main.default_fragment_progress_dialog.*

/**
 * Created by ramanan.vijayakumar on 3/21/2018.
 */
class GMProgressDialog(context: Context) : Dialog(context) {
    private var disableMessageView=false
    private var messageText:String=""
    private var showProgressBar=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun disableMessageView(){
        showProgressBar=false
        disableMessageView=true
    }
    fun setMessage(message: String){
        showProgressBar=false
        messageText=message
    }

    override fun show() {
        super.show()
        setContentView(R.layout.default_fragment_progress_dialog)
        if (showProgressBar){
            progressBar?.visibility=View.VISIBLE
            contentLayout?.visibility=View.GONE
            loadingImageView1?.visibility=View.GONE
        }else{
            progressBar.visibility=View.GONE
            if (disableMessageView || messageText.equals("")){
                loadingTextView?.text=SingleTon.getResourceStringValue("loading")
                contentLayout?.visibility=View.GONE
                loadingImageView1?.visibility=View.VISIBLE
                ImageUtils.loadProgressGIF(loadingImageView1,R.raw.final_gif)
            }else{
                contentLayout?.visibility=View.VISIBLE
                loadingImageView1?.visibility=View.GONE
                loadingTextView?.text=messageText
                ImageUtils.loadProgressGIF(loadingImageView,R.raw.final_gif)
            }
        }
    }
}
