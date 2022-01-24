package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.activities.HomeActivity
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.IntentUtils
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_chick_details.*


class SupportFragment : GMBaseFragment() {
    var supportDetail = Model.SupportResponse()
    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    companion object {
        fun newInstance(args: Bundle): SupportFragment {
            val fragment = SupportFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_support, container, false)
    }
    fun setResourceSting()
    {
        raisedRequestTextView?.text=getResourceString("title_raised_request")
        chickTextView?.text=getResourceString("chick_type")
        feedTextView?.text=getResourceString("feed")
        medicineTextView?.text=getResourceString("medicine")
        markettingTextView?.text=getResourceString("marketting")
        managementTextView?.text=getResourceString("management")
        othersTextView?.text=getResourceString("others")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceSting()
        initToolbar(R.menu.menu_fliter,getResourceString("title_farmer"), true)
        val navMenu = toolbar.menu
        navMenu.findItem(R.id.menu_notification).isVisible = resources.getBoolean(R.bool.visible)
        getSuportDetails()
    }

    fun initView() {
        val bundle = Bundle()
        supportDetail.response?.supportDetails?.forEach {
            when (it.supportId?.toInt()) {
                1 -> chickCat?.text = it.count.toString().plus(" ").plus(getResourceString("sub_category"))
                2 -> feedCat?.text = it.count.toString().plus(" ").plus(getResourceString("sub_category"))
                3 -> medicineCat?.text = it.count.toString().plus(" ").plus(getResourceString("sub_category"))
                4 -> markettingCat?.text = it.count.toString().plus(" ").plus(getResourceString("sub_category"))
                5 -> managementCat?.text = it.count.toString().plus(" ").plus(getResourceString("sub_category"))
            }
        }
        chickTypeCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM, getResourceString("chick_type"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 1)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }
        feedCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM,  getResourceString("feed"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 2)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }
        medicineCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM,  getResourceString("medicine"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 3)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }
        markettingCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM,  getResourceString("marketting"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 4)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }
        managementCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM, getResourceString("management"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 5)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }
        othersCardView?.setOnClickListener {
            bundle.putString(SelectedSupportFragment.ARG_SELECTED_ITEM, getResourceString("others"))
            bundle.putInt(SelectedSupportFragment.ARG_SELECTED_ITEM_ID, 6)
            (activity as HomeActivity).replaceFragment(SelectedSupportFragment.newInstance(bundle))
        }


        raisedRequestLayout?.setOnClickListener {
           (activity as HomeActivity).replaceFragment(RaisedRequestListFragment.newInstance(bundle), "")
        }

        issueCount?.text = supportDetail.response?.tickets.toString()
    }


    fun getSuportDetails() {
        showProgressBar()
        supportLayout.visibility = View.VISIBLE
        DataProvider.getSuportDetails(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.SupportResponse) {
                    dismissProgressBar()
                    supportDetail = responseObject
                    initView()
                }

            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()
                showErrorSnackBar(responseObject)
            }

        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                val bundle = Bundle()
                (activity as HomeActivity).replaceFragment(NotificationFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

}


/*
<string-array name="supportList">
<item>@string/chick_bird</item>
<item>@string/feed_stock</item>
<item>@string/medicine</item>
<item>@string/marketting</item>
<item>@string/management</item>
<item>@string/others</item>
</string-array>*/
