package com.gm.WebServices

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gm.GMApplication
import com.gm.R
import com.gm.db.DAO
import com.gm.db.SingleTon
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.GMKeys
import java.lang.Exception


object DataProvider {
    var application: GMApplication? = null
    private var materialId:Long?=null

    fun clearDatabase() {
        threadBlock {
            application?.database?.clearAllTables()
        }
    }

    fun getContext(): Context {
        return GMApplication.appContext!!
    }

    fun threadBlock(block: () -> Unit) {
        Thread() {
            kotlin.run {
                block.invoke()
            }
        }.start()
    }

    fun sendMessage(responseObject: kotlin.Any?, listener: ServiceRequestListener) {
        val mHandler = Handler(Looper.getMainLooper())
        if (responseObject != null) {
            mHandler.post({ listener.onRequestCompleted(responseObject) })
        } else {
            mHandler.post({ listener.onRequestFailed(SingleTon.getResourceStringValue("error_network_connection")) })
        }
    }


    fun getPendingActivityList(pageNo: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getPendingActivityList(pageNo, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.ActivityListResponse) {
                        responseObject.activityList?.let {
                            threadBlock {
                                val items = application?.database?.getActivityListDao()?.getListByStatus(DAO.TRUE)
                                items?.let {
                                    application?.database?.getActivityListDao()?.deleteItems(items)
                                }
                                val item: List<Model.ActivityList> = it
                                item.forEach {
                                    it.isPending = true
                                }
                                application?.database?.getActivityListDao()?.insert(item)
                            }
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val item = application?.database?.getActivityListDao()?.getListByStatus(DAO.TRUE)
                var responseObject: Model.ActivityListResponse? = null
                item?.let {
                    responseObject = Model.ActivityListResponse(ArrayList(it))
                }
                sendMessage(responseObject, listener)

            }
        }
    }


    fun getCompletedActivityList(date: String, age: Int, pageNo: Int, flage: Int,isShedReadyActivity:Boolean, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getCompletedActivityList(date, age, pageNo = pageNo, flage = flage, isShedReadyActivity = isShedReadyActivity,listener = object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.ActivityListResponse && flage == 0 && date == "" && age == -1) {
                        responseObject.activityList?.let {
                            threadBlock {
                                if (pageNo == 0) {
                                    val items = application?.database?.getActivityListDao()?.getListByStatus(DAO.FALSE)
                                    items?.let {
                                        application?.database?.getActivityListDao()?.deleteItems(items)
                                    }
                                }
                                val item: List<Model.ActivityList> = it
                                item.forEach {
                                    it.isPending = false
                                }
                                application?.database?.getActivityListDao()?.insert(item)

                            }
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                var responseObject: Model.ActivityListResponse? = null
                if (flage == 0 && date == "" && age == -1) {
                    val item = application?.database?.getActivityListDao()?.getListByStatus(DAO.FALSE)
                    item?.let {
                        responseObject = Model.ActivityListResponse(ArrayList(it.reversed()))
                    }
                }
                sendMessage(responseObject, listener)

            }
        }
    }


    fun getRepositoryList(repositoryCategoryId: Int, pageNo: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getRepositoryList(repositoryCategoryId, pageNo, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.RepositoryListResponse) {
                        threadBlock {
                            if (pageNo == 0) {
                                application?.database?.getRepositoryListDao()?.deleteById(repositoryCategoryId.toLong())
                            }
                            val item = application?.database?.getRepositoryListDao()?.getById(repositoryCategoryId.toLong())

                            if (item != null) {
                                if (item.repositoryList.isNullOrEmpty()) {
                                    item.repositoryList = responseObject.repositoryList
                                } else {
                                    item.repositoryList?.addAll(responseObject.repositoryList!!)
                                }
                                application?.database?.getRepositoryListDao()?.update(item)
                            } else {
                                responseObject.pId = repositoryCategoryId.toLong()
                                application?.database?.getRepositoryListDao()?.insert(responseObject)
                            }
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })

        } else {
            threadBlock {
                val responseObject: Model.RepositoryListResponse? = application?.database?.getRepositoryListDao()?.getById(repositoryCategoryId.toLong())
                sendMessage(responseObject, listener)

            }
        }
    }


    fun getRepositoryDetails(repositoryId: Int, listener: ServiceRequestListener) {
        threadBlock {
            val totalList = application?.database?.getRepositoryListDao()?.getall()
            val list = totalList?.map { it.repositoryList?.singleOrNull { it.repositoryId == repositoryId.toLong() } }?.singleOrNull()
            if (list == null) {
                if (NetworkAvailability.isNetworkAvailable(getContext())) {
                    ServiceWrapper.getRepositoryDetails(repositoryId, object : ServiceRequestListener {
                        override fun onRequestCompleted(responseObject: Any?) {
                            sendMessage(responseObject, listener)
                            if (responseObject is Model.RepositoryDetailResponse) {
                                threadBlock {
                                    val repositoryCategoryItem = application?.database?.getRepositoryListDao()?.getById(responseObject.repositoryList?.repositoryCategoryId
                                            ?: -1)
                                    if (repositoryCategoryItem != null) {
                                        if (repositoryCategoryItem.repositoryList?.count { it.repositoryId == repositoryId.toLong() } ?: 0 == 0) {
                                            if (repositoryCategoryItem.repositoryList == null) {
                                                repositoryCategoryItem.repositoryList = arrayListOf(responseObject.repositoryList!!)
                                            } else {
                                                repositoryCategoryItem.repositoryList?.add(responseObject.repositoryList!!)
                                            }
                                            application?.database?.getRepositoryListDao()?.update(repositoryCategoryItem)
                                        }
                                    } else {
                                        val model = Model.RepositoryListResponse()
                                        model.pId = responseObject.repositoryList?.repositoryCategoryId
                                                ?: -1
                                        model.repositoryList = arrayListOf(responseObject.repositoryList!!)
                                        application?.database?.getRepositoryListDao()?.insert(model)
                                    }
                                }
                            }
                        }

                        override fun onRequestFailed(responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
                } else {
                    sendMessage(null, listener)
                }
            } else {
                val model = Model.RepositoryDetailResponse()
                model.id = repositoryId.toLong()
                model.repositoryList = list
                sendMessage(model, listener)
            }
        }

    }


    fun getDownloadVideo(activityId: Long, videoId: Long, path: String) {

        var data = Model.DownLoadInput()
        data.activityId = activityId
        data.videoId = videoId
        data.path = path
        threadBlock {
            //  application?.database?.getDownLoadedVideoDao()?.deleteAll()
            application?.database?.getDownLoadedVideoDao()?.insert(data)
        }


    }

    fun getDownloadFilePath(activityId: Long, videoId: Long, listener: ServiceRequestListener) {
        var responseObject: Model.DownLoadInput? = null
        threadBlock {

            var list = application?.database?.getDownLoadedVideoDao()?.getVideoPath(activityId, videoId)
            if (list != null) {
                responseObject = list as Model.DownLoadInput
            } else {
                responseObject = Model.DownLoadInput()
            }
            sendMessage(responseObject, listener)
        }
    }

    fun deletDownloadFilePath(activityId: Long, videoId: Long) {
        threadBlock {
            application?.database?.getDownLoadedVideoDao()?.deleteVideoPath(activityId, videoId)
        }
    }

    fun deleteAllDownloadFilePath() {
        threadBlock {
            application?.database?.getDownLoadedVideoDao()?.deleteAll()
        }
    }

    fun getAllDownloadPath() {
        threadBlock {
            var responseObject = application?.database?.getDownLoadedVideoDao()?.getAll()
            Log.e("All Download path", responseObject.toString())
        }
    }


    fun getRepositoryList(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getRepositoryList(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.RepositoryCategoriesList) {
                        threadBlock {
                            application?.database?.getRepositoryCategoriesDao()?.deleteAll()
                            application?.database?.getRepositoryCategoriesDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getRepositoryCategoriesDao()?.getall()
                var responseObject: Model.RepositoryCategoriesList? = null
                list?.let {
                    responseObject = list
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun getProgressResult(date: String, age: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getProgressResult(date, age, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.PrograssBarResponse) {
                        threadBlock {
                            if (date == "" && age == -1) {
                                application?.database?.getProgressDao()?.deleteAll()
                                application?.database?.getProgressDao()?.insert(responseObject)
                            }

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                var responseObject: Model.PrograssBarResponse? = null
                if (date == "" && age == -1) {
                    responseObject = application?.database?.getProgressDao()?.getall()
                } else {
                    responseObject = Model.PrograssBarResponse()
                }
                sendMessage(responseObject, listener)
            }
        }
    }



    fun getDailyReportReason(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getDailyReportReason(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.DailyReportReasonsResponse) {
                        threadBlock {

                                application?.database?.getReasonListDao()?.deleteAll()
                                application?.database?.getReasonListDao()?.insert(responseObject)


                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                var responseObject: Model.DailyReportReasonsResponse? = null

                    responseObject = application?.database?.getReasonListDao()?.getall()

                sendMessage(responseObject, listener)
            }
        }
    }



    fun checkNetworkConnectivity(): Boolean {

        var connected: Boolean? = false
        val connectivityManager = getContext()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.state == NetworkInfo.State.CONNECTED
        return connected
    }

    fun getSupportTypeList(SupportCategoryId: Int?, pageNo: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getSupportTypeList(SupportCategoryId, pageNo, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.SupportListResponse) {
                        threadBlock {
                            if (pageNo == 0) {
                                application?.database?.getSupportDao()?.deleteById(supportCategoryId = SupportCategoryId?.toLong()
                                        ?: -1)
                            }
                            val item = application?.database?.getSupportDao()?.getById(SupportCategoryId?.toLong()
                                    ?: -1)
                            if (item != null) {
                                if (item.supportList.isNullOrEmpty()) {
                                    item.supportList = responseObject.supportList
                                } else {
                                    item.supportList?.addAll(responseObject.supportList!!)
                                }
                                application?.database?.getSupportDao()?.update(item)
                            } else {
                                responseObject.id = SupportCategoryId?.toLong()
                                application?.database?.getSupportDao()?.insert(responseObject)
                            }
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val responseObject: Model.SupportListResponse? = application?.database?.getSupportDao()?.getById(SupportCategoryId?.toLong()
                        ?: -1)
                sendMessage(responseObject, listener)
            }
        }

    }


    fun getNotificationList(pageNo: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getNotificationList(pageNo, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.NotificaitonListResponse) {
                        threadBlock {
                            application?.database?.getNotificationDao()?.deleteAll()
                            application?.database?.getNotificationDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getNotificationDao()?.getall()
                var responseObject: Model.NotificaitonListResponse? = null
                list?.let {
                    responseObject = list as Model.NotificaitonListResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }




    fun getActivityDetailById(activityId: Long, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getActivityDetailById(activityId, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.ActivityResponse) {
                        threadBlock {
                            application?.database?.getActivityDetailsByIdDao()?.deleteAll()
                            application?.database?.getActivityDetailsByIdDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    GMKeys
                    listener.onRequestFailed(responseObject)
                }
            })


        } else {
            threadBlock {
                val list = application?.database?.getActivityDetailsByIdDao()?.getall()
                var responseObject: Model.ActivityResponse? = null
                list?.let {
                    responseObject = list as Model.ActivityResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun getSuportDetails(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getSuportDetails(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.SupportResponse) {
                        threadBlock {
                            application?.database?.getSupportDetailsDao()?.deleteAll()
                            application?.database?.getSupportDetailsDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getSupportDetailsDao()?.getall()
                var responseObject: Model.SupportResponse? = null
                list?.let {
                    responseObject = list as Model.SupportResponse
                }

                sendMessage(responseObject, listener)
            }
        }
    }


    fun getFarmsListDetails(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getFarmsListDetails(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.SupportDetailListResponse) {
                        threadBlock {
                            application?.database?.getRaisedRequestDao()?.deleteAll()
                            application?.database?.getRaisedRequestDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getRaisedRequestDao()?.getall()
                var responseObject: Model.SupportDetailListResponse? = null
                list?.let {
                    responseObject = list as Model.SupportDetailListResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }

    fun getIssueListDetails(ticket: Long, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getIssueDetails(ticket, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.SupportIssueListDetailsResponse) {
                        threadBlock {
                            application?.database?.getSupportListDetailDao()?.deleteDetailBySupportId(ticket)
                            val supportData = responseObject.response
                            supportData?.supportTicketId = ticket
                            supportData?.let { application?.database?.getSupportListDetailDao()?.insert(supportData) }

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getSupportListDetailDao()?.getDetailBySupportId(ticket)
                var responseObject: Model.SupportIssueListDetailsResponse? = null
                list?.let {
                    responseObject = Model.SupportIssueListDetailsResponse(list)
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun postDailyReportFeedBack(feedBackSubmitRequest: Model.FeedBackSubmitRequest, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.postDailyReportFeedBack(feedBackSubmitRequest, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                application?.database?.addDailyReportDao()?.insert(feedBackSubmitRequest)
                sendMessage("Record saved Successfully", listener)
            }
        }
    }

    fun getFeedbackQuestion(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getFeedbackQuestion(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.FeedBackQuestionListResponse) {
                        threadBlock {
                            application?.database?.getFeedBackRatingDetailsDao()?.deleteAll()
                            application?.database?.getFeedBackRatingDetailsDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getFeedBackRatingDetailsDao()?.getall()
                var responseObject: Model.FeedBackQuestionListResponse? = null
                list?.let {
                    responseObject = list as Model.FeedBackQuestionListResponse
                }
                sendMessage(responseObject, listener)

            }
        }
    }

    fun getFeedBackDetails(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getFeedBackDetails(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.DailyReportDetailsResponse) {
                        threadBlock {
                            application?.database?.getFeedBackDetailsDao()?.deleteAll()
                            application?.database?.getFeedBackDetailsDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getFeedBackDetailsDao()?.getall()
                var responseObject: Model.DailyReportDetailsResponse? = null
                list?.let {
                    responseObject = list as Model.DailyReportDetailsResponse
                }
                sendMessage(responseObject, listener)

            }
        }
    }

    fun uploadSupportMedia(offlineFeedBack: Model.OfflineSupportMediaList, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.uploadSupportMedia(offlineFeedBack, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.addSupportTicketDao()?.getall()
                offlineFeedBack.supportId = (list?.size ?: 0) + 1
                application?.database?.addSupportMediaDao()?.insert(offlineFeedBack)

                sendMessage("Record saved Successfully", listener)
            }

        }
    }

    fun uploadCommentMedia(offlineSupportUpdateMedia: Model.OfflineUpdateSupportMediaList, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.uploadCommentMedia(offlineSupportUpdateMedia, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.updateSupportTicketDao()?.getall()
                offlineSupportUpdateMedia.supportId = (list?.size ?: 0) + 1
                application?.database?.addCommentMediaDao()?.insert(offlineSupportUpdateMedia)

                sendMessage("Record saved Successfully", listener)
            }

        }
    }


    fun uploadFeedBackMedia(offlineFeedBack: Model.OfflineFeedBackMediaList, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {

            ServiceWrapper.uploadFeedBackMedia(offlineFeedBack, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {

            threadBlock {
                val list = application?.database?.addFeedBackSaveDetailsDao()?.getall()
                offlineFeedBack.feedBackId = (list?.size ?: 0) + 1
                application?.database?.addFeedBackMediaDao()?.insert(offlineFeedBack)
                sendMessage("Record saved Successfully", listener)
            }

        }
    }


    fun uploadMedia(offlineActivity: Model.OfflineMediaActivity, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {

            ServiceWrapper.uploadMedia(offlineActivity, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                application?.database?.uploadActivityMediaDao()?.insert(offlineActivity)
                var mediaList = application?.database?.uploadActivityMediaDao()?.getall()
                sendMessage("Record saved Successfully", listener)

            }
        }
    }


    fun getFeedbackSaveRating(feedBackSubmitRequest: Model.FeedBackSaveDetails, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getFeedbackSaveRating(feedBackSubmitRequest, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.addFeedBackSaveDetailsDao()?.getall()
                feedBackSubmitRequest.feedBackId = list?.size?.plus(1)
                application?.database?.addFeedBackSaveDetailsDao()?.insert(feedBackSubmitRequest)
                sendMessage("You are in offline.Record saved Successfully", listener)
            }
        }
    }


    fun saveActivityDetails(saveActivityDetails: Model.SaveActivityDetails, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.saveActivityDetails(saveActivityDetails, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                application?.database?.addActivityDetailsDao()?.insert(saveActivityDetails)
                sendMessage("You are in offline.Record saved Successfully", listener)
            }
        }
    }


    fun createSupportTicket(createSupport: Model.SupportCreateTicket, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.createSupportTicket(createSupport, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.addSupportTicketDao()?.getall()
                createSupport.supportId = (list?.size ?: 0) + 1

                application?.database?.addSupportTicketDao()?.insert(createSupport)

                sendMessage("You are in offline.Record saved Successfully", listener)
            }
        }
    }


    fun updateSupportTicket(createSupport: Model.SupportUpdate, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.updateSupportTicket(createSupport, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.updateSupportTicketDao()?.getall()
                createSupport.supportOfflineId = (list?.size ?: 0) + 1

                application?.database?.updateSupportTicketDao()?.insert(createSupport)

                sendMessage("You are in offline.Record saved Successfully", listener)
            }
        }
    }


    fun getDailyReportHistory(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getDailyReportHistory(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.DailyReportHistoryResponse) {
                        threadBlock {
                            application?.database?.getDailyReportHistoryDao()?.deleteAll()
                            application?.database?.getDailyReportHistoryDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getDailyReportHistoryDao()?.getall()
                var responseObject: Model.DailyReportHistoryResponse? = null
                list?.let {
                    responseObject = list as Model.DailyReportHistoryResponse
                }
                sendMessage(responseObject, listener)

            }
        }
    }


    fun getFeedBackHistory(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getFeedBackHistory(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.FeedbackHistoryResponse) {
                        threadBlock {
                            application?.database?.getFeedbackHistoryDao()?.deleteAll()
                            application?.database?.getFeedbackHistoryDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getFeedbackHistoryDao()?.getall()
                var responseObject: Model.FeedbackHistoryResponse? = null
                list?.let {
                    responseObject = list as Model.FeedbackHistoryResponse
                }
                sendMessage(responseObject, listener)

            }
        }
    }


    fun getSupportQuestionList(SupportCategoryId: Int?, pageNo: Int, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getSupportQuestionList(SupportCategoryId, pageNo, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.SupportQuestionListResponse) {
                        threadBlock {
                            application?.database?.getSupportQuestionDao()?.deleteAll();
                            application?.database?.getSupportQuestionDao()?.insert(responseObject)
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    sendMessage(responseObject, listener)
                }
            })
        } else {
            threadBlock {
                val list = application?.database?.getSupportQuestionDao()?.getall()
                var responseObject: Model.SupportQuestionListResponse? = null
                list?.let {
                    responseObject = list as Model.SupportQuestionListResponse
                }
                sendMessage(responseObject, listener)
            }
        }

    }

    fun getTodayMaterialArrivalList(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getTodayMaterialArrivalList(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.MaterialArrivalResponse) {
                        threadBlock {
                            application?.database?.getMaterialArrivalDao()?.deleteAll()
                            application?.database?.getMaterialArrivalDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {

            threadBlock {
                materialId?.let { offlineSubmit(it) }
                val list = application?.database?.getMaterialArrivalDao()?.getall()
                var responseObject: Model.MaterialArrivalResponse? = null
                list?.let {
                    responseObject = list as Model.MaterialArrivalResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }

    fun getTodayMaterialUpdate(arrivalDispatch: Model.DispatchArrivalDetailsList, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getTodayMaterialUpdate(arrivalDispatch, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }
            })
        } else {
            materialId=arrivalDispatch.materialArrivalId
            threadBlock {
                application?.database?.submitMaterialArrivalDao()?.insert(arrivalDispatch)
                sendMessage("Record saved Successfully", listener)
            }
        }
    }


    fun getMaterialHistoryList(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getMaterialHistoryList(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.HistoryMaterialArrivalResponse) {
                        threadBlock {
                            application?.database?.getHistoryArrivalDao()?.deleteAll()
                            application?.database?.getHistoryArrivalDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getHistoryArrivalDao()?.getall()
                var responseObject: Model.HistoryMaterialArrivalResponse? = null
                list?.let {
                    responseObject = list as Model.HistoryMaterialArrivalResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun getMaterialHistoryDetails(date: String, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getMaterialHistoryDetails(date, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.HistoryMaterialArrivalDetailResponse) {
                        threadBlock {
                            application?.database?.getHistoryDetailsDao()?.deleteAll()
                            application?.database?.getHistoryDetailsDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getHistoryDetailsDao()?.getall()
                var responseObject: Model.HistoryMaterialArrivalDetailResponse? = null
                list?.let {
                    responseObject = list as Model.HistoryMaterialArrivalDetailResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }

    fun getPerformanceReport(listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getPerformanceReport(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.PerformanceReportResponse) {
                        threadBlock {
                            application?.database?.getPerformanceReportDao()?.deleteAll()
                            application?.database?.getPerformanceReportDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getPerformanceReportDao()?.getall()
                var responseObject: Model.PerformanceReportResponse? = null
                list?.let {
                    responseObject = list as Model.PerformanceReportResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun getPerformanceChart(batchIdList: Model.ChartInput, listener: ServiceRequestListener) {
        if (NetworkAvailability.isNetworkAvailable(getContext())) {
            ServiceWrapper.getPerformanceChart(batchIdList, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    sendMessage(responseObject, listener)
                    if (responseObject is Model.PerformanceChartResponse) {
                        threadBlock {
                            application?.database?.getPerformanceChartDao()?.deleteAll()
                            application?.database?.getPerformanceChartDao()?.insert(responseObject)

                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    listener.onRequestFailed(responseObject)
                }

            })


        } else {
            threadBlock {
                val list = application?.database?.getPerformanceChartDao()?.getall()
                var responseObject: Model.PerformanceChartResponse? = null
                list?.let {
                    responseObject = list as Model.PerformanceChartResponse
                }
                sendMessage(responseObject, listener)
            }
        }
    }


    fun offlineSubmit(materialArrivalId:Long) {
        val overallList=application?.database?.getMaterialArrivalDao()?.getall()!!
        //Get all values from table....and delete particular item....
        try {
          //  overallList = application?.database?.getMaterialArrivalDao()?.getall()!!
            overallList?.response?.todaysArrivals?.forEach {

                var item=it
                val it= item.value?.iterator()
                while (it?.hasNext()!!) {
                    val s = it.next()
                    if (s.materialArrivalId == materialArrivalId) {
                        it.remove()
                    }
                }


            }


        } catch (e: Exception) {
            e
        }

//Delete the overall table
        application?.database?.getMaterialArrivalDao()?.deleteAll()
//Insert the modified values
        overallList.let {
         application?.database?.getMaterialArrivalDao()?.insert(it)
        }
        overallList
        }
    }
