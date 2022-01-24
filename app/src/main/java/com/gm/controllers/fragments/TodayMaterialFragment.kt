package com.gm.controllers.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.activities.MaterialActivity
import com.gm.controllers.adapter.ArrivalDateAdapter
import com.gm.listener.OnGetCallListener
import com.gm.listener.OnItemClickListener
import com.gm.listener.OnItemSelectedListenerFeedback
import com.gm.models.Model
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_today_arraival_material.*
import kotlinx.android.synthetic.main.fragment_today_arraival_material.dataLayout
import kotlinx.android.synthetic.main.layout_today_arraival_empty.*

class TodayMaterialFragment : GMBaseFragment(), OnItemClickListener , OnGetCallListener {


    private lateinit var dispatchFragment: DispatchStatusFragment

    override fun onPermissionGranted(requestCode: Int) {


    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    var materialResponse: Model.MaterialArrivalResponse? = null

    companion object {
        fun newInstance(args: Bundle = Bundle()): TodayMaterialFragment {
            val fragment = TodayMaterialFragment()
            fragment.arguments = args
            return fragment

        }
    }

    fun setDispatchInstance(dispatchFragment: DispatchStatusFragment): Fragment {
        this.dispatchFragment = dispatchFragment
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_today_arraival_material, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeContainer1?.isRefreshing = false
        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            dispatchFragment.getTodayMaterialArrivalList()
        }
        swipeContainer1?.setOnRefreshListener {
            swipeContainer1?.isRefreshing = true
            dispatchFragment.getTodayMaterialArrivalList()
        }
        (activity as BaseActivity).checkPhoneCallPermission()
    }


    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.MaterialArrivalAdapter) {
            if (item.arrivalQuantity?.value != 0.toLong()) {
                val list = Model.DispatchArrivalDetailsList()
                list.materialArrivalId = item.materialArrivalId
                list.arrivalQuantity = item.arrivalQuantity?.value
                list.comments = item.comments?.value
                dispatchFragment.submitTodayMaterialUpdate(list)
            } else {
                showSnackBar(getResourceString("message_arrival"))
            }

        }
    }

    fun swipeProgress() {
        swipeContainer1?.isRefreshing = false
        swipeContainer?.isRefreshing=false
    }

    fun onDataFetched(materialResponse: Model.MaterialArrivalResponse) {

        this.materialResponse = materialResponse
        displayData()
    }


    private fun displayData() {
        checkVisibility()
        if (materialResponse?.response?.isActiveBatch ?: true) {
            nextArrivalDateTextView?.text = getResourceString("next_material").plus(" ").plus(DateUtils.toDisplayDate(materialResponse?.response?.nextExpectedFeedArrival))
        } else {
            nextArrivalDateTextView?.text = getResourceString("next_check_arrival").plus(" ").plus(DateUtils.toDisplayDate(materialResponse?.response?.nextExpectedChickArrivalFrom).plus(" to ").plus(DateUtils.toDisplayDate(materialResponse?.response?.nextExpectedChickArrivalTo)))
        }
        initDateAdapater()

    }


    private fun checkVisibility() {
        //  if(materialResponse?.response?.todaysArrivals!=null)
        if (materialResponse?.response?.todaysArrivals?.size != 0) {
            dataLayout?.visibility = View.VISIBLE
            emptyLayout?.visibility = View.GONE
        } else {
            emptyLayout?.visibility = View.VISIBLE
        }

    }

    fun initDateAdapater() {
        dataLayout?.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
        val adapter = materialResponse?.response?.todaysArrivals?.let { context?.let { it1 -> ArrivalDateAdapter(it, it1, this,this,this) } }
        dataLayout?.adapter = adapter

    }

    override fun onItemSelected(phoneNumber: String?) {
        phoneNumber?.let { startCallIndent(it) }
    }

    fun startCallIndent(number: String) {
        val uri = "tel:" + number.trim()
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }


}