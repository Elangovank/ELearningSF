package com.gm.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.core.utils.AppPreferences
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.adapter.NavigationMenuAdapter
import com.gm.controllers.fragments.*
import com.gm.firebase.RegistrationIntentService
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import kotlinx.android.synthetic.main.home_bottom.*
import kotlinx.android.synthetic.main.nav_drawer.*
import java.util.*
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.widget.ImageView
import com.gm.db.SingleTon
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.CustomDialogeEixt
import com.gmcoreui.controllers.fragments.BaseFragment
import com.gmcoreui.controllers.ui.CustomDialog
import com.gmcoreui.utils.ActivityUtils
import fr.maxcom.libmedia.Licensing
import java.io.File
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList


class HomeActivity : BaseActivity(), OnItemClickListener {

    private var slidingRootNav: SlidingRootNav? = null
    val fragments = Stack<Fragment>()
    var currentFragment = Fragment()

    override fun onPermissionDenied(requestCode: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is BaseFragment) {
            fragment.onPermissionDenied(requestCode)
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is BaseFragment) {
            fragment.onPermissionGranted(requestCode)
        }
    }


    companion object {
        var homeActivityWeekReference: WeakReference<Context>? = null
    }
fun setResourceString()
{
    close?.text=getResourceString("close")

}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityWeekReference = WeakReference(this)
        setContentView(R.layout.activity_home)
        Licensing.allow(this)
        selectedItem(R.id.activity)
        slidingRootNav = SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.nav_drawer)
                .inject()
        (slidingRootNav as SlidingRootNav).closeMenu(true)
        initNavDrawer()
        if (File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt").exists()) {
            registerForNotification()
            bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
            loadFragmentByCondition()
        }else{

            getResourceString()


        }
    }

    override fun onDestroy() {
        homeActivityWeekReference = null
        super.onDestroy()
    }


    private fun registerForNotification() {
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }


    fun loadActivityFragment() {
        bottomNavigation.selectedItemId = R.id.activity
    }

    fun loadSupportFragment() {
        bottomNavigation.selectedItemId = R.id.support
    }


    override fun onBackPressed() {
        if (fragments.size == 1) {
            if (fragments.peek() is ActivityFragment) {
                showConfirmDialog()
            } else {
                loadActivityFragment()
            }

        } else if (fragments.peek() is SelectedRepositoryDetailsFragment) {
            fragments.pop()
            loadFragment(R.id.container, fragments.peek())
        } else {
            fragments.pop()
            loadFragment(R.id.container, fragments.peek())
        }
    }



    private fun showConfirmDialog() {
        val customDialog = CustomDialogeEixt(this)
        customDialog.setType(CustomDialogeEixt.ALERT_TYPE_WARNING)
                .setMessage(getResourceString("message_app_exit_alert"))
                .setPositiveButtonMessage(getResourceString("message_yes_text")).setNegativeButtonMessage(getResourceString("message_no_text"))
                .setPositiveButton(DialogInterface.OnClickListener { dialog, _ ->

                  if (!fragments?.empty())
                  {
                      fragments.pop()
                      loadFragment(R.id.container, fragments.peek())
                  }

                    dialog.dismiss()
                })
                .setNegativeButton(DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                .show()
    }


    private val mOnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
            selectedItem(item.itemId)

            when (item.getItemId()) {
                R.id.activity -> {
                    fragments.clear()
                    if (!(currentFragment is ActivityFragment))
                        replaceFragment(ActivityFragment.newInstance(Bundle()), "")
                    return true
                }
                R.id.repository -> {
                    fragments.clear()
                    if (!(currentFragment is RepositoryHomeFragment))
                        replaceFragment(RepositoryHomeFragment.newInstance(Bundle()), "")
                    return true
                }
                R.id.feedback -> {
                    fragments.clear()
                    if (!(currentFragment is FeedbackRatingFragment))
                        replaceFragment(FeedbackRatingFragment.newInstance(Bundle()), "")
                    return true
                }
                R.id.support -> {
                    fragments.clear()
                    if (!(currentFragment is SupportFragment))
                        replaceFragment(SupportFragment.newInstance(Bundle()), "")
                    return true
                }
                R.id.report -> {
                    item.setIcon(R.drawable.ic_report_selected)
                    fragments.clear()
                    if (!(currentFragment is DailyReportFragment))
                        replaceFragment(DailyReportFragment.newInstance(Bundle()), "")
                    return true
                }
            }
            return false
        }
    }


    fun getRepositoryDetails(repositoryId: Int?) {
        repositoryId?.let {
            DataProvider.getRepositoryDetails(it, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    dismissProgressBar()
                    if (responseObject is Model.RepositoryDetailResponse) {
                        val bundle = Bundle()
                        bundle.putSerializable("key", responseObject.repositoryList)
                        replaceFragment(SelectedRepositoryDetailsFragment.newInstance(bundle))
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)
                    dismissProgressBar()
                }
            })
        }
    }


    fun getNotificationStatus(notificationid: Long) {
        ServiceWrapper.getNotificationStatus(notificationid, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
            }

            override fun onRequestFailed(responseObject: String) {
            }
        })

    }

    fun loadFragmentByCondition() {
        if (intent.getSerializableExtra("notification") != null) {
            GMApplication.loginUserId = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID)
                    ?: 0
            val notification = intent.getSerializableExtra("notification") as Model.Notificaiton
            notification.notificationId?.let { getNotificationStatus(it) }
            notification.notificationTypeId.let {
                val notificationTypeId = it
                if (it == 5) {
                    val bundle = Bundle()
                    bundle.putLong(RaisedRequestListFragment.SUPPORT_TICKET_ID, notification.id
                            ?: 0)
                    replaceFragment(SelectedSupportIssueDetailFragment.newInstance(bundle), "")
                } else if (notificationTypeId == 4) {
                    getRepositoryDetails(notification.id?.toInt())
                } else if (notificationTypeId == 1) {
                    notification.id?.let { it1 -> getActivityDetailsById(it1) }

                }
            }
        } else {
            loadActivityFragment()
        }
    }






    fun getActivityDetailsById(activityId: Long) {
        ServiceWrapper.getActivityDetailById(activityId, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.ActivityResponse) {
                    val bundle = Bundle()
                    bundle.putSerializable(GMKeys.selectedActivity, responseObject.activity)
                    replaceFragment(ActivityViewFragment.newInstance(bundle), "")
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showSnackBar(responseObject)
            }
        })
    }


    fun replaceFragment(fragment: Fragment) {
        replaceFragment(fragment, "")
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        (slidingRootNav as SlidingRootNav).closeMenu(true)
        if (selectedIndex == 0) {
            if (NetworkAvailability.isNetworkAvailable(applicationContext)) {
                val intent = Intent(this, LanguageActivity::class.java)
                intent.putExtra(LanguageActivity.NAVIGATION, INDENT_NAVIGATION.Home)
                startActivity(intent)
            } else {
                showSnackBar(getResourceString("error_network_connection"))
            }
        } else if (selectedIndex == 1) {
            fragments.clear()
            loadSupportFragment()
            replaceFragment(SupportFragment.newInstance(Bundle()), "")
        } else if (selectedIndex == 2) {
            replaceFragment(DownloadFragment.newInstance(Bundle()), "")
        } else if (selectedIndex == 3) {
            loadMaterialArrivedActivity()
            // replaceFragment(DispatchStatusFragement.newInstance(Bundle()), "")
        } else if (selectedIndex == 4) {
            replaceFragment(PerformanceReportFragment.newInstance(Bundle()), "")
        } else if (selectedIndex == 5) {
            replaceFragment(WeatherViewPagerFragment.newInstance(Bundle()), "")
        } else if (selectedIndex == 6) {
            gotoLogout()
        }
    }

    fun loadMaterialArrivedActivity() {

        val myIntent = Intent(this, MaterialActivity::class.java)
        this.startActivity(myIntent)

    }

    fun gotoLogout() {
        showProgressBar()
        ServiceWrapper.logout(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                dismissProgressBar()
                startLoginActivity()
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                dismissProgressBar()
            }
        })

    }

    fun startLoginActivity() {
        val bundle = Bundle()
        val user = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.KEY_LOGIN_DATA)
        user?.let {
            val userDetails = Gson().fromJson<Model.LoginData>(it, Model.LoginData::class.java)
            userDetails?.let {
                bundle.putString(ValidateUserActivity.ARG_FARMER_CODE, it.farmCode ?: "")
            }
        }

        GMApplication.loginUserId = 0
        GMApplication.level2Token = ""
        GMApplication.languageCode=""
        AppPreferences.getInstance()?.setStringSharedPreference(GMKeys.KEY_LOGIN_DATA, "")
        AppPreferences.getInstance()?.setLongSharedPreference(GMKeys.SESS_LOGIN_USER_ID, 0)

        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }



    private fun initNavDrawer() {
        val menuList = ArrayList<String>()
        menuList.add(getResourceString("label_language"))
        menuList.add(getResourceString("title_farmer"))
        menuList.add(getResourceString("label_download"))
        menuList.add(getResourceString("title_material_arrival"))
        menuList.add(getResourceString("performance_report"))
        menuList.add(getResourceString("weather"))
        menuList.add(getResourceString("label_sign_out"))

        val iconList = resources.obtainTypedArray(R.array.navigationMenuIcon)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val navigationMenuAdapter = NavigationMenuAdapter(menuList, iconList, this)
        recyclerView.adapter = navigationMenuAdapter
                                                                                                                                               GMApplication.userName?.let {
            userName.text = "".plus(it)
        }
        version?.setText("${getResourceString("version")} ${ActivityUtils.getVersionName()}")
        GMApplication.farmCode?.let {
            mobileNumber.text = "" + it
        }
        userLayout.setOnClickListener {
            (slidingRootNav as SlidingRootNav).closeMenu(true)
        }
        close.setOnClickListener {
            (slidingRootNav as SlidingRootNav).closeMenu(true)
        }
    }

    fun onToolbarClicked() {
        hideKeyboard(this)
        if (slidingRootNav?.isMenuOpened!!) {
            slidingRootNav?.closeMenu()
        } else {
            slidingRootNav?.openMenu()
        }
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        fragments.push(fragment)
        currentFragment = fragment
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
                .commit()
    }

    fun selectedItem(itemId: Int) {
        resetDotVisibility()
        resetBottomIcon()
        when (itemId) {
            R.id.activity -> {
              //  bottomTextView1?.text=getResourceString("activity")
                bottomDot1.visibility = View.VISIBLE
                animateImageView(bottomImageView1)
                bottomImageView1.setImageResource(R.drawable.ic_activity_selected)
            }
            R.id.repository -> {
               // bottomTextView2?.text=getResourceString("repository")
                bottomDot2.visibility = View.VISIBLE
                animateImageView(bottomImageView2)
                bottomImageView2.setImageResource(R.drawable.ic_repo_selected)
            }
            R.id.feedback -> {
               // bottomTextView3?.text=getResourceString("feedback")

                bottomDot4.visibility = View.VISIBLE
                animateImageView(bottomImageView4)
                bottomImageView4.setImageResource(R.drawable.ic_feedback_selected)
            }
            R.id.support -> {
             //   bottomTextView5?.text=getResourceString("title_farmer")

                bottomDot5.visibility = View.VISIBLE
                animateImageView(bottomImageView5)
                bottomImageView5.setImageResource(R.drawable.ic_support_selected)
            }
            R.id.report -> {
                //bottomTextView3?.text=getResourceString("report")
                bottomDot3.visibility = View.VISIBLE
                animateImageView(bottomImageView3)
                bottomImageView3.setImageResource(R.drawable.ic_report_selected)
            }
        }
    }

    fun animateImageView(imageView: ImageView) {
        val objectAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)
        objectAnimator.setDuration(500)
        objectAnimator.start()
    }

    private fun resetDotVisibility() {
        bottomDot1.visibility = View.GONE
        bottomDot2.visibility = View.GONE
        bottomDot3.visibility = View.GONE
        bottomDot4.visibility = View.GONE
        bottomDot5.visibility = View.GONE
    }

    fun resetBottomIcon() {
        bottomImageView1.setImageResource(R.drawable.ic_activity_unselected)
        bottomImageView2.setImageResource(R.drawable.ic_repository_unselected)
        bottomImageView3.setImageResource(R.drawable.ic_report_unselected)
        bottomImageView4.setImageResource(R.drawable.ic_feedback_unselected)
        bottomImageView5.setImageResource(R.drawable.ic_support_unselected)
        bottomTextView1?.text=getResourceString("activity")
        bottomTextView2?.text=getResourceString("repository")
        bottomTextView4?.text=getResourceString("feedback")
        bottomTextView5?.text=getResourceString("title_farmer")
        bottomTextView3?.text=getResourceString("report")
    }
    private fun getResourceString() {
      //  languageCode?.let {
         //   GMApplication.languageCode=it
            ServiceWrapper.getResourceString("en",object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.ResourceStringResponse) {
                        GMApplication.resourceStringMap= HashMap()
                        val file=File(GMApplication.appContext?.externalCacheDir?.getPath().toString().toString() + "/fileName.txt")
                        val jsonString:String = Gson().toJson(responseObject)
                        file.deleteOnExit()
                        file.writeText(jsonString)
                        SingleTon.clearMapValue()
                        registerForNotification()
                        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                        loadFragmentByCondition()
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    showErrorSnackBar(responseObject)
                    //  dismissProgressDialog()
                }
            })
       }
    }

