package com.gm.controllers.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.LoadMoreListener
import com.gm.controllers.adapter.SelectedRepositoryAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import java.util.*


class SelectedRepositoryFragment : GMBaseFragment(), OnItemClickListener {


    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.Repository) {
            val bundle = Bundle()
            bundle.putSerializable(GMKeys.repositoryActivity, item)
            (activity as HomeActivity).replaceFragment(SelectedRepositoryDetailsFragment.newInstance(bundle), "")
        }
    }

    companion object {
        const val ARG_SELECTED_ITEM = "ARG_SELECTED_ITEM"
        const val ARG_SELECTED_ITEM_ID = "ARG_SELECTED_ITEM_ID"
        fun newInstance(args: Bundle): SelectedRepositoryFragment {
            val fragment = SelectedRepositoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var originalList = ArrayList<Model.Repository>()
    var filteredList = ArrayList<Model.Repository>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    fun setResourceSting()
    {
        searchView?.queryHint=getResourceString("search_hint")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceSting()
        initToolbar(arguments?.getString(ARG_SELECTED_ITEM) ?: "")
        initSearchView(searchView)
        searchView.isFocusable = false
        searchView.setOnClickListener {
            searchView.isFocusableInTouchMode = true
        }
        swipeContainer?.isRefreshing = false

        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            originalList.clear()
            setPaging()
            getRepositoryList()
        }

        originalList.clear()
        getRepositoryList()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            hideKeyboard(activity!!)
            filterByString(query)
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterByString(newText)
        return true
    }

    fun filterByString(newText: String?) {
        if (newText.isNullOrEmpty()) {
            setPaging()
            listItems()
        } else {
            filteredList.clear()
            originalList.filterTo(filteredList) {
                (!it.title.isNullOrEmpty()) && (it.title?.contains(newText, true) == true)
            }
            if (recyclerViewRepository?.adapter is SelectedRepositoryAdapter)
                (recyclerViewRepository?.adapter as SelectedRepositoryAdapter).setReachedEnd()
            notifyAdapter()
        }
    }


    fun setPaging() {
        if (recyclerViewRepository?.adapter is SelectedRepositoryAdapter) {
            if ((originalList.size % GMKeys.PAGE_SIZE != 0))
                (recyclerViewRepository?.adapter as SelectedRepositoryAdapter).setReachedEnd()
            else
                (recyclerViewRepository?.adapter as SelectedRepositoryAdapter).setPagingEnabled(true)
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    fun notifyAdapter() {
        if (recyclerViewRepository?.adapter == null) {
            SelectedRepositoryAdapter(filteredList, this)
                    .setPagingEnabled(true)
                    .setLoadingListener(object : LoadMoreListener {
                        override fun loadMoreItems() {
                            getRepositoryList()
                        }
                    }).into(recyclerViewRepository)
        } else {
            recyclerViewRepository?.adapter?.notifyDataSetChanged()
        }

    }

    fun listItems() {
        filteredList.clear()
        filteredList.addAll(originalList)
        notifyAdapter()
    }

    private fun getRepositoryList() {
        if (originalList.size == 0) showProgressBar()
        val idValues: Int = arguments?.getString(ARG_SELECTED_ITEM_ID)!!.toInt()
        DataProvider.getRepositoryList(idValues, originalList.size, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                swipeContainer?.isRefreshing = false
                if (originalList.size == 0) dismissProgressBar()
                if (responseObject is Model.RepositoryListResponse) {
                    responseObject.repositoryList?.let {
                        originalList.addAll(it)
                        listItems()
                        if (it.size == 0 || (originalList.size % GMKeys.PAGE_SIZE) != 0) {
                            recyclerViewRepository?.adapter?.let {
                                (recyclerViewRepository?.adapter as SelectedRepositoryAdapter).setReachedEnd()
                            }
                        }
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                if (originalList.size == 0) dismissProgressBar()
                swipeContainer?.isRefreshing = false
                showErrorSnackBar(responseObject)
            }
        })
    }

}
