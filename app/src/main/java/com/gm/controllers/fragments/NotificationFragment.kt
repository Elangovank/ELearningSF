package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.NotificationAdapter
import com.gm.listener.LastItemReachedListener
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.layout_no_record.*
import java.text.SimpleDateFormat
import java.util.*


class NotificationFragment : GMBaseFragment(), OnItemClickListener, LastItemReachedListener {

    var notificationList1 = ArrayList<Model.Notificaiton>()
    private var notification: ArrayList<Model.Notificaiton>? = null
    private var adapter: NotificationAdapter? = null

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    companion object {
        fun newInstance(args: Bundle): NotificationFragment {
            val fragment = NotificationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(getResourceString("Notifications"))
        notificationList1.clear()
        getNotificationList()


    }


    override fun onItemSelected() {
        /* if (lastitemCount != -1 && lastitemCount == 10) {
             getNotificationList()
             loadMoreProgressBar.visibility = View.VISIBLE
         }*/
    }


    fun getNotificationList() {
        loadMoreProgressBar.visibility = View.VISIBLE
        if (notificationList1.size % GMKeys.PAGE_SIZE == 0) {
            showProgressBar()
            DataProvider.getNotificationList(0, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    dismissProgressBar()
                    loadMoreProgressBar?.visibility = View.GONE
                    if (responseObject is Model.NotificaitonListResponse) {
                        var readList = ArrayList<Model.Notificaiton>()
                        var unreadList = ArrayList<Model.Notificaiton>()
                        responseObject.notificationList?.notificationRead?.let{
                            readList = responseObject.notificationList?.notificationRead!!
                        }
                        responseObject.notificationList?.notificationUnRead?.let{
                            unreadList = responseObject.notificationList?.notificationUnRead!!
                        }
                        notificationList1.addAll(unreadList)
                        notificationList1.addAll(readList)
                        initViewAdapter(notificationList1, unreadList.size)
                        checkVisibility()
                    }

                }

                override fun onRequestFailed(responseObject: String) {
                    dismissProgressBar()
                    loadMoreProgressBar?.visibility = View.GONE
                    showErrorSnackBar(responseObject)
                }
            })
        }
    }


    fun initViewAdapter(notificationList: ArrayList<Model.Notificaiton>, size: Int) {
        var notificationReaded = ArrayList<Model.Notificaiton>()
        var notificationUnReaded = ArrayList<Model.Notificaiton>()
        notification = ArrayList<Model.Notificaiton>()
        notification!!.addAll(notificationUnReaded)
        notification!!.addAll(notificationReaded)
        context?.let {
            recyclerViewRepository?.layoutManager = LinearLayoutManager(it) as RecyclerView.LayoutManager?
            val dateFormat = SimpleDateFormat(DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)

            notification!!.sortWith(Comparator { o1, o2 -> dateFormat.parse(o2.notificationDate).compareTo(dateFormat.parse(o1.notificationDate)) })
            adapter = NotificationAdapter(notificationList!!, size, this, this,this)
            recyclerViewRepository?.adapter = adapter
        }
    }

    fun getRepositoryDetails(repositoryId: Int?) {
        showProgressBar()
        repositoryId?.let {
            DataProvider.getRepositoryDetails(it, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    dismissProgressBar()
                    if (responseObject is Model.RepositoryDetailResponse) {
                        val bundle = Bundle()
                        bundle.putSerializable("key", responseObject.repositoryList)
                        (activity as HomeActivity).replaceFragment(SelectedRepositoryDetailsFragment.newInstance(bundle))
                    }

                }

                override fun onRequestFailed(responseObject: String) {

                    dismissProgressBar()
                    showErrorSnackBar(responseObject)


                }
            })
        }
    }

    fun getNotificationStatus(notificationid: Long) {
        ServiceWrapper.getNotificationStatus(notificationid, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
            }
        })

    }

    fun getActivityDetailsById(activityId: Long) {
        showProgressBar()
        DataProvider.getActivityDetailById(activityId, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                dismissProgressBar()
                if (responseObject is Model.ActivityResponse) {
                        val bundle = Bundle()
                        bundle.putSerializable(GMKeys.selectedActivity, responseObject.activity)
                        bundle.putBoolean(GMKeys.isFromNotification, true)
                        (activity as HomeActivity).replaceFragment(ActivityViewFragment.newInstance(bundle), "")
                    }


            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()
               showErrorSnackBar(responseObject)
            }


        })
    }


    fun checkVisibility() {
        if (notificationList1.size != 0) {
            recyclerViewRepository?.visibility = View.VISIBLE
            noDataLayout?.visibility = View.GONE
        } else {
            recyclerViewRepository?.visibility = View.GONE
            noDataLayout?.visibility = View.VISIBLE
        }
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if(item is Model.Notificaiton)
        {
            when(item.notificationTypeId)
            {

                GMKeys.NotificatioRepository->{
                    item.notificationId?.let { getNotificationStatus(it) }
                        getRepositoryDetails(item.id?.toInt())
                }
                GMKeys.NotificationActivity->{
                    item.id?.let { getActivityDetailsById(it) }

                }
                GMKeys.NotificationSupport->{
                    val bundle = Bundle()
                    bundle.putSerializable(RaisedRequestListFragment.SUPPORT_TICKET_ID,item.id)
                    (activity as HomeActivity).replaceFragment(SelectedSupportIssueDetailFragment.newInstance(bundle))

                }


            }

        }



    }
}