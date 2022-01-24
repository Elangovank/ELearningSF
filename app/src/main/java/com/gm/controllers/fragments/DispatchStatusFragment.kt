package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.gm.R
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.adapter.ListOfFragmentViewPagerAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_dispatch_status.*
import android.R.attr.name
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import com.gm.WebServices.DataProvider
import com.gm.receiver.NetworkAvailability


class DispatchStatusFragment() : GMBaseFragment() {


    enum class ViewPagerPosition(val value: Int) {
        TodayMaterialFrag(0), MaterialHistoryFrag(1)
    }

    val fragments = arrayListOf(TodayMaterialFragment.newInstance().setDispatchInstance(this), MaterialHistoryFragment.newInstance().setDispatchInstance(this))


    companion object {
        fun newInstance(args: Bundle): DispatchStatusFragment {
            val fragment = DispatchStatusFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dispatch_status, container, false)
    }

    private fun setResourceFile()
    {
        materialArrivalTextView?.text=getResourceString("title_material_arrival")
        historyTextView?.text=getResourceString("history")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(getResourceString("title_material_arrival"), false)
        setResourceFile()
        //arguments?.get("fromVidio")as Boolean
        fetchData()
        dispatchViewPager?.adapter = ListOfFragmentViewPagerAdapter(fragments, childFragmentManager)

        setListener()
    }


    private fun setListener() {

        materialArrivalTextView?.setOnClickListener {
            if (dispatchViewPager?.currentItem != ViewPagerPosition.TodayMaterialFrag.value)
                dispatchViewPager?.currentItem = ViewPagerPosition.TodayMaterialFrag.value
        }

        historyTextView?.setOnClickListener {
            if (dispatchViewPager?.currentItem != ViewPagerPosition.MaterialHistoryFrag.value)
                dispatchViewPager?.currentItem = ViewPagerPosition.MaterialHistoryFrag.value

        }

        dispatchViewPager.addOnPageChangeListener(pagerListener)

    }
  fun moveToHistory()
  {
      if (dispatchViewPager?.currentItem != ViewPagerPosition.MaterialHistoryFrag.value)
          dispatchViewPager?.currentItem = ViewPagerPosition.MaterialHistoryFrag.value
  }
    private val pagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            setTabIndicatorVisibility()
        }
    }


    fun setTabIndicatorVisibility() {
        if (dispatchViewPager?.currentItem == ViewPagerPosition.TodayMaterialFrag.value) {
            historyLineView?.visibility = View.INVISIBLE
            materialArrivalLineView?.visibility = View.VISIBLE
        } else if (dispatchViewPager?.currentItem == ViewPagerPosition.MaterialHistoryFrag.value) {
            historyLineView?.visibility = View.VISIBLE
            materialArrivalLineView?.visibility = View.INVISIBLE
        }
    }

    fun fetchData() {
        getTodayMaterialArrivalList()
        getMaterialHistoryList()
    }


    fun getTodayMaterialArrivalList() {
        showProgressBar()
        DataProvider.getTodayMaterialArrivalList(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.MaterialArrivalResponse) {
                    dismissProgressBar()
                    getTodayMaterialArrivalFragment().swipeProgress()
                    getTodayMaterialArrivalFragment().onDataFetched(responseObject)
                }
            }

            override fun onRequestFailed(responseObject: String) {
                getTodayMaterialArrivalFragment().swipeProgress()
                dismissProgressBar()

            }
        })
    }


    fun getTodayMaterialArrivalFragment(): TodayMaterialFragment {
        return fragments[0] as TodayMaterialFragment
    }


    fun getHistoryMaterialArrivalFragment(): MaterialHistoryFragment {
        return fragments[1] as MaterialHistoryFragment
    }

    fun getMaterialHistoryList() {
        showProgressBar()
        DataProvider.getMaterialHistoryList(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.HistoryMaterialArrivalResponse) {
                    dismissProgressBar()
                    getHistoryMaterialArrivalFragment().onDataFetched(responseObject)
                    getHistoryMaterialArrivalFragment().dismissSwipeProgress()
                }
            }

            override fun onRequestFailed(responseObject: String) {
                getHistoryMaterialArrivalFragment().dismissSwipeProgress()
                dismissProgressBar()

            }
        })
    }

    fun submitTodayMaterialUpdate(arrivalList: Model.DispatchArrivalDetailsList) {
        showProgressBar()
        DataProvider.getTodayMaterialUpdate(arrivalList, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                dismissProgressBar()
                showSnackBar(getResourceString("submittedSucessfully"))
                Handler().postDelayed({
                    fetchData()
                }, 1000)
            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()
               showErrorSnackBar(responseObject)
            }
        })
    }


    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }


}

