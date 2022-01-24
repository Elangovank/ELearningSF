package com.gm.controllers.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gm.R
import com.gm.controllers.fragments.DispatchArrivedFragment
import com.gm.controllers.fragments.DispatchStatusFragment
import com.gmcoreui.controllers.BaseActivity
import java.util.*

class MaterialActivity : BaseActivity() {
    companion object {
        val fragments = Stack<Fragment>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material)
        replaceFragment(DispatchStatusFragment.newInstance(Bundle()))
    }

    fun replaceFragment(fragment: Fragment) {
        fragments.push(fragment)
        supportFragmentManager.beginTransaction().replace(R.id.containerMaterial, fragment)
                .commit()
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    override fun onBackPressed() = if (fragments.peek() is DispatchArrivedFragment) {
        fragments?.pop()
        var bundle=Bundle()
        bundle.putBoolean("fromVidio",true)

       replaceFragment(DispatchStatusFragment.newInstance(Bundle()))
    } else {
        super.onBackPressed()
    }

}


