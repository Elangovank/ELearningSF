package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gm.R
import com.gm.controllers.activities.MaterialActivity
import com.gm.controllers.adapter.DispatchHistoryAdapter
import com.gm.controllers.adapter.LoadMoreListener
import com.gm.listener.OnItemClickListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.layout_recyclerview.*

class MaterialHistoryFragment : GMBaseFragment() {

    lateinit var dispatchFragment: DispatchStatusFragment

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    companion object {
        fun newInstance(args: Bundle = Bundle()): MaterialHistoryFragment {
            val fragment = MaterialHistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun setDispatchInstance(dispatchFragment: DispatchStatusFragment): Fragment {
        this.dispatchFragment = dispatchFragment
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainerSwipeRefreshLayout?.isRefreshing = false
        swipeContainerSwipeRefreshLayout?.setOnRefreshListener {
            swipeContainerSwipeRefreshLayout?.isRefreshing = true
            dispatchFragment.getMaterialHistoryList()
        }

    }

    fun dismissSwipeProgress() {
        swipeContainerSwipeRefreshLayout?.isRefreshing = false
    }

    fun onDataFetched(materialResponse: Model.HistoryMaterialArrivalResponse) {
        initAdapter(materialResponse?.response ?: arrayListOf())
    }


    private fun displayData() {
        initAdapter(arrayListOf())
    }

    fun initAdapter(materialList: ArrayList<Model.HistoryMaterialArrival>) {

        DispatchHistoryAdapter(materialList, object : OnItemClickListener {
            override fun onItemSelected(item: Any?, selectedIndex: Int) {
                if (item is Model.HistoryMaterialArrival) {
                    val bundle = Bundle()
                    bundle.putSerializable("History", item)
                    (activity as MaterialActivity).replaceFragment(DispatchArrivedFragment.newInstance(bundle))


                }

            }
        },this).setPagingEnabled(false)
                .setLoadingListener(object : LoadMoreListener {
                    override fun loadMoreItems() {

                    }
                }).into(recyclerView)
    }


}