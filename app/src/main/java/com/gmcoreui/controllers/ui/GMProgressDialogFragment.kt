package com.gmcoreui.controllers.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gm.R

/**
 * Created by ramanan.vijayakumar on 3/21/2018.
 */
class GMProgressDialogFragment : androidx.fragment.app.DialogFragment() {
    companion object {
        fun newInstance(args: Bundle): GMProgressDialogFragment {
            val fragment = GMProgressDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.default_fragment_progress_dialog, container, false)
    }

}
