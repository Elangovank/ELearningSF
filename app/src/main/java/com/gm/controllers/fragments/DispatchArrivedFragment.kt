package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.adapter.DispatchArrivedAdapter
import com.gm.controllers.adapter.LoadMoreListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_arrived_material.*


class DispatchArrivedFragment : GMBaseFragment() {

    companion object {
        fun newInstance(args: Bundle): DispatchArrivedFragment {
            val fragment = DispatchArrivedFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_arrived_material, container, false)
    }
    fun setResourceSting()
    {
        reasonGMTextInputLayout?.hint=getResourceString("reason")
        completedTextView?.text=getResourceString("label_completed")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(getResourceString("title_arrived_materials"), false)
        val data = arguments?.get("History") as Model.HistoryMaterialArrival
        data.arrivalDate?.let { getDispatchArrivalHistoryDetails(it) }
        completedDate?.text = data.arrivalDate.let {DateUtils.toDisplayDate(it)  }
    }


    fun notifyAdapter(detailList: ArrayList<Model.HistoryMaterialArrivalDetail>) {
        context?.let {
            DispatchArrivedAdapter(detailList, it,this)
                .setPagingEnabled(false)
                .setLoadingListener(object : LoadMoreListener {
                    override fun loadMoreItems() {

                    }
                }).into(dispatchStatusRecyclerView)
        }



    }

    fun getDispatchArrivalHistoryDetails(date: String) {
        showProgressBar()
        DataProvider.getMaterialHistoryDetails(date, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.HistoryMaterialArrivalDetailResponse) {
                    arrivedLayout?.visibility = View.VISIBLE
                    dismissProgressBar()
                    responseObject.response?.let { notifyAdapter(it) }
                }
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