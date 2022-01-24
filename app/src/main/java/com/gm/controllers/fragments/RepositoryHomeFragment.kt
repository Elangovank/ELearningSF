package com.gm.controllers.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.RepositoryCategoriesAdapter
import com.gm.db.SingleTon
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.layout_no_record.*
import kotlinx.android.synthetic.main.repository_home_fragment_recyclerview.*


class RepositoryHomeFragment : GMBaseFragment(), OnItemClickListener {

    var categoriesList = ArrayList<Model.RepositoryCategories>()
    private var adapter: RepositoryCategoriesAdapter? = null
    var filteredList = ArrayList<Model.RepositoryCategories>()
    private var searchResult = -1

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Model.RepositoryCategories) {
            val bundle = Bundle()
            bundle.putString(SelectedRepositoryFragment.ARG_SELECTED_ITEM_ID, item.repositoryCategoryId!!.toString())
            bundle.putString(SelectedRepositoryFragment.ARG_SELECTED_ITEM, item.name)
            (activity as HomeActivity).replaceFragment(SelectedRepositoryFragment.newInstance(bundle))
        }
    }

    companion object {
        fun newInstance(args: Bundle): RepositoryHomeFragment {
            val fragment = RepositoryHomeFragment()
            fragment.arguments = args
            return fragment
        }

    //    var resourceString = SingleTon.readThefile()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.repository_home_fragment_recyclerview, container, false)
    }


    private fun setResourceString() {
        searchView?.queryHint = getResourceString("search_hint")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        initToolbar(R.menu.menu_fliter,getResourceString("title_repository"), true)
        val navMenu = toolbar.menu
        navMenu.findItem(R.id.menu_notification).isVisible = resources.getBoolean(R.bool.visible)
        initSearchView(searchView)
        searchView.isFocusable = false


        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            toGetReposList()
        }
        toGetReposList()
        initAdapter()
        searchView.setOnClickListener {
            searchView.isFocusableInTouchMode = true
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            hideKeyboard(activity!!)
            filterListBy(it)
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrBlank()) {
            loadOriginalHistory()
            return true
        } else {
            newText?.let { filterListBy(it) }
            return true
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    private fun filterListBy(searchString: String) {
        var title: ArrayList<Model.RepositoryCategories> = ArrayList()
        filteredList.clear()
        categoriesList.let {
            categoriesList?.filterTo(title) {
                (!TextUtils.isEmpty(it.name) && it.name!!.contains(searchString, true))
            }
        }
        if (title.size == 0) {
            searchResult = 0
            filteredList?.clear()
            display(true)
        } else {
            searchResult = -1
            filteredList?.clear()
            filteredList?.addAll(title)
            adapter?.notifyDataSetChanged()
            display(false)
        }
    }

    private fun loadOriginalHistory() {
        searchResult = -1
        filteredList?.clear()
        filteredList?.addAll(categoriesList)
        adapter?.notifyDataSetChanged()
        display(false)
    }

    private fun display(status: Boolean) {
        if (status) {
            recyclerViewCatogories.visibility = View.GONE
            noDataLayout?.visibility = View.VISIBLE
        } else {
            recyclerViewCatogories.visibility = View.VISIBLE
            noDataLayout?.visibility = View.GONE
        }
    }

    private fun initAdapter() {
        recyclerViewCatogories?.layoutManager = LinearLayoutManager(context)
        adapter = RepositoryCategoriesAdapter(filteredList, this@RepositoryHomeFragment)
        recyclerViewCatogories?.adapter = adapter
    }


    private fun toGetReposList() {
        showProgressBar()
        DataProvider.getRepositoryList(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.RepositoryCategoriesList) {
                    if (responseObject.repositoryCategoriesList?.size != 0) {
                        categoriesList.clear()
                        filteredList.clear()
                        recyclerViewCatogories?.visibility = View.VISIBLE
                        noDataLayout?.visibility = View.GONE
                        recyclerViewCatogories?.adapter?.let {
                            categoriesList.addAll(responseObject.repositoryCategoriesList!!)
                            filteredList.addAll(categoriesList)
                            initAdapter()
                            recyclerViewCatogories?.adapter?.notifyDataSetChanged()
                        }
                    } else {
                        recyclerViewCatogories?.visibility = View.GONE
                        noDataLayout?.visibility = View.VISIBLE
                    }
                }
                dismissProgressBar()
                swipeContainer?.isRefreshing = false
            }

            override fun onRequestFailed(responseObject: String) {
                swipeContainer?.isRefreshing = false
                dismissProgressBar()
                showErrorSnackBar(responseObject)
            }
        })

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                var bundle = Bundle()
                (activity as HomeActivity).replaceFragment(NotificationFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
}
