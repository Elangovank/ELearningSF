package com.gm.controllers.activities

import android.os.Bundle
import com.gm.R
import com.gm.controllers.fragments.SelectedRepositoryFragment
import com.gmcoreui.controllers.BaseActivity

class FragmentContainerActivity: BaseActivity() {

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        loadFragment(R.id.container, SelectedRepositoryFragment.newInstance(Bundle()))
    }

}