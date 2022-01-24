package com.gm.controllers.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.LoadMoreListener
import com.gm.controllers.adapter.SelectedSupportAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.default_fragment_bottom_sheet_item.view.*
import kotlinx.android.synthetic.main.fragment_recycler_horizontal.*


class SelectedSupportFragment : GMBaseFragment(), OnItemClickListener {
    var originalList = ArrayList<Model.Support>()
    var filteredList = ArrayList<Model.Support>()
    var title: String? = null


    companion object {
        const val ARG_SELECTED_ITEM = "ARG_SELECTED_ITEM"
        const val ARG_SELECTED_ITEM_ID = "ARG_SELECTED_ITEM_ID"

        fun newInstance(args: Bundle): SelectedSupportFragment {
            val fragment = SelectedSupportFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_horizontal, container, false)
    }

    fun setResourceString()
    {
        searchView?.queryHint=getResourceString("search_hint")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        title = arguments?.getString(ARG_SELECTED_ITEM) ?: ""
        initToolbar(arguments?.getString(ARG_SELECTED_ITEM) ?: "")
        initSearchView(searchView)
        searchView.isFocusable = false
        searchView.setOnClickListener {
            searchView.isFocusableInTouchMode = true
        }
        originalList.clear()
        getSupportList()
    }

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    private fun getSupportList() {
        if (originalList.size == 0) showProgressBar()
        val idValues: Int? = arguments?.getInt(ARG_SELECTED_ITEM_ID)
        DataProvider.getSupportTypeList(idValues, originalList.size, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (originalList.size == 0) dismissProgressBar()
                if (responseObject is Model.SupportListResponse) {
                    responseObject.supportList?.let {
                        originalList.addAll(it)
                        listItems()
                        if (it.size == 0 || (originalList.size % GMKeys.PAGE_SIZE) != 0) {
                            if (recyclerView?.adapter is SelectedSupportAdapter) {
                                (recyclerView?.adapter as SelectedSupportAdapter).setReachedEnd()
                            }
                        }
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                if (originalList.size == 0) dismissProgressBar()
            }
        })
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
                (!it.name.isNullOrEmpty()) && (it.name?.contains(newText,true) == true)
            }
            if (recyclerView?.adapter is SelectedSupportAdapter)
                (recyclerView?.adapter as SelectedSupportAdapter).setReachedEnd()
            notifyAdapter()
        }
    }


    fun setPaging() {
        if (recyclerView?.adapter is SelectedSupportAdapter) {
            if ((originalList.size % GMKeys.PAGE_SIZE != 0))
                (recyclerView?.adapter as SelectedSupportAdapter).setReachedEnd()
            else
                (recyclerView?.adapter as SelectedSupportAdapter).setPagingEnabled(true)
        }
    }


    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.Support) {
            val bundle = Bundle()
            bundle.putString(SelectedSupportDetailFragment.ITEM_TITLE, title)
            bundle.putLong(SelectedSupportDetailFragment.ARG_SELECTED_SUB_CATAGORY, item.supportCategoryId
                    ?: 0)
            bundle.putString(SelectedSupportDetailFragment.ARG_SELECTED_ITEM, item.name)
            bundle.putString(SelectedSupportDetailFragment.ARG_SELECTED_ITEM_IMAGE_URL, item.supportImageUrl)
            (activity as HomeActivity).replaceFragment(SelectedSupportDetailFragment.newInstance(bundle), "")
        }
    }

    fun listItems() {
        filteredList.clear()
        filteredList.addAll(originalList)
        notifyAdapter()
    }

    private fun notifyAdapter() {
        if (recyclerView?.adapter == null) {
            context?.let {
                SelectedSupportAdapter(filteredList, this)
                        .setPagingEnabled(true)
                        .setLoadingListener(object : LoadMoreListener {
                            override fun loadMoreItems() {
                                getSupportList()
                            }
                        }).into(recyclerView)
            }
        } else {
            recyclerView?.adapter?.notifyDataSetChanged()
        }
    }
}
