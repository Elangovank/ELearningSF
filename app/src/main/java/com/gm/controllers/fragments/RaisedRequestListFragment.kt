package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.RaisedRequestAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.fragment_raised_request_list.*


class RaisedRequestListFragment : GMBaseFragment(), OnItemClickListener {
    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    var list = ArrayList<Model.SuppourtDetailList>()

    companion object {
        fun newInstance(args: Bundle): RaisedRequestListFragment {
            val fragment = RaisedRequestListFragment()
            fragment.arguments = args
            return fragment
        }

        var SUPPORT_TICKET_ID = "supportTicketId"
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.SuppourtDetailList) {
            val bundle = Bundle()
            bundle.putLong(SUPPORT_TICKET_ID, item.supportTicketId ?: 0)
            (activity as HomeActivity).replaceFragment(SelectedSupportIssueDetailFragment.newInstance(bundle), "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_raised_request_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            getFarmsListDetails()
        }
        initToolbar(getResourceString("title_raised_request"), false)
        getFarmsListDetails()
    }


    fun getFarmsListDetails() {
        showProgressBar()
        list.clear()
        DataProvider.getFarmsListDetails(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.SupportDetailListResponse) {
                    dismissProgressBar()
                    swipeContainer?.isRefreshing = false
                    responseObject.response?.forEach {
                        list?.add(it)
                        initAdapter(list)
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                swipeContainer?.isRefreshing = false
                dismissProgressBar()
            }
        })
    }

    private fun initAdapter(list: ArrayList<Model.SuppourtDetailList>) {
        raisedRequestRecyclerView?.visibility = View.VISIBLE
        if (raisedRequestRecyclerView?.adapter == null) {
            context?.let {
                raisedRequestRecyclerView?.layoutManager = LinearLayoutManager(context)
                raisedRequestRecyclerView?.adapter = RaisedRequestAdapter(list, context!!, this, resources,this)
            }
        } else {
            raisedRequestRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

}
