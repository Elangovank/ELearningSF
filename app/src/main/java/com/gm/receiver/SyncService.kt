package com.gm.receiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys

class SyncService : Service() {
    var dailyReportList = ArrayList<Model.FeedBackSubmitRequest>()
    var feedbBackMediaListSyn = ArrayList<Model.OfflineFeedBackMediaList>()
    var feedbackDetailsList = ArrayList<Model.FeedBackSaveDetails>()

    var supportMediaListSyn = ArrayList<Model.OfflineSupportMediaList>()
    var supportDetailsList = ArrayList<Model.SupportCreateTicket>()

    var supportUpdateMediaListSyn = ArrayList<Model.OfflineUpdateSupportMediaList>()
    var supportUpdateDetailsList = ArrayList<Model.SupportUpdate>()


    var activityMediaListSyn = ArrayList<Model.OfflineMediaActivity>()
    var activityDetailsList = ArrayList<Model.SaveActivityDetails>()

    var arrivalDetailList=ArrayList<Model.DispatchArrivalDetailsList>()


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        object : Thread() {
            override fun run() {
                doSync()
            }
        }.start()
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun doSync() {
        saveFeedBackMediaList()
        saveDailyReportDetails()
        saveActivityMediaList()
        saveSupportMediaList()
        saveSupportUpdateMediaList()
        saveMaterialArrivalList()

    }

    private fun saveDailyReportDetails() {
        DataProvider.threadBlock {

            DataProvider.application?.database?.addDailyReportDao()?.getall().let {
                dailyReportList = it as ArrayList<Model.FeedBackSubmitRequest>
                if (dailyReportList.size > 0) {
                    saveDailyReportDetails(0)
                }

            }
            DataProvider.application?.database?.addDailyReportDao()?.deleteAll()
        }
    }

    private fun saveMaterialArrivalList() {
        DataProvider.threadBlock {
            DataProvider.application?.database?.submitMaterialArrivalDao()?.getall().let {
                arrivalDetailList = it as ArrayList<Model.DispatchArrivalDetailsList>
                if (arrivalDetailList.size > 0) {
                    saveMaterialArrivalDetails(0)
                }
            }
            DataProvider.application?.database?.submitMaterialArrivalDao()?.deleteAll()
        }
    }

    fun saveMaterialArrivalDetails(index: Int) {
        ServiceWrapper.getTodayMaterialUpdate(arrivalDetailList.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(response: Any?) {
                if (index != arrivalDetailList.size - 1) {
                    saveMaterialArrivalDetails(index + 1)
                }
            }

            override fun onRequestFailed(response: String) {
                if (index != arrivalDetailList.size - 1) {
                    saveMaterialArrivalDetails(index + 1)
                }
            }
        })
    }


    fun saveDailyReportDetails(index: Int) {
        ServiceWrapper.postDailyReportFeedBack(dailyReportList.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(response: Any?) {
                if (index != dailyReportList.size - 1) {
                    saveDailyReportDetails(index + 1)
                }
            }

            override fun onRequestFailed(response: String) {
                if (index != dailyReportList.size - 1) {
                    saveDailyReportDetails(index + 1)
                }
            }
        })
    }



    fun saveFeedBackMediaList(index: Int) {
        ServiceWrapper.uploadFeedBackMedia(feedbBackMediaListSyn.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.FeedbackResultResponse) {
                    responseObject.response?.let { response ->
                        if (feedbackDetailsList.size > 0) {
                            val list = ArrayList<Model.SupportTicketsResponse>()
                            feedbackDetailsList.get(0).medias?.let { mediaList -> list.addAll(mediaList) }
                            response.mediaType = feedbBackMediaListSyn.get(index).mediaType
                            list.add(response)
                            feedbackDetailsList.get(0).medias = list
                            DataProvider.threadBlock {
                                DataProvider.application?.database?.addFeedBackSaveDetailsDao()?.update(feedbackDetailsList.get(0))
                                DataProvider.application?.database?.addFeedBackMediaDao()?.deleteItem(feedbBackMediaListSyn.get(index))
                            }
                            if (index != feedbBackMediaListSyn.size - 1) {
                                saveFeedBackMediaList(index + 1)
                            } else {
                                saveFeedBackDetail()
                            }
                        }
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                if (responseObject.equals(GMKeys.FILE_NOT_FOUND)) {
                    DataProvider.threadBlock {
                        DataProvider.application?.database?.addFeedBackMediaDao()?.deleteItem(feedbBackMediaListSyn.get(index))
                    }
                    saveFeedBackMediaList(index + 1)
                } else {
                    saveFeedBackMediaList(index)
                }

            }
        })
    }

    private fun saveFeedBackMediaList() {
        DataProvider.threadBlock {
            DataProvider.application?.database?.addFeedBackSaveDetailsDao()?.getall().let { list ->
                list?.let {
                    feedbackDetailsList = ArrayList(list)
                    if (feedbackDetailsList.size > 0) {
                        feedbackDetailsList.get(0).feedBackId?.let {
                            DataProvider.application?.database?.addFeedBackMediaDao()?.getItemById(it)?.let {
                                feedbBackMediaListSyn = ArrayList(it)
                            }
                        }

                        if (feedbBackMediaListSyn.size > 0) {
                            saveFeedBackMediaList(0)
                        }
                    }
                }
            }
        }

    }


    fun saveFeedBackDetail() {
        if (feedbackDetailsList.size > 0)
            ServiceWrapper.getFeedbackSaveRating(feedbackDetailsList.get(0), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    DataProvider.threadBlock {
                        feedbackDetailsList.get(0).feedBackId?.let { DataProvider.application?.database?.addFeedBackSaveDetailsDao()?.deleteSynFeedback(it) }
                        saveFeedBackMediaList()
                    }
                }

                override fun onRequestFailed(responseObject: String) {

                }
            })
    }


    fun saveSupportMediaList(index: Int) {
        ServiceWrapper.uploadSupportMedia(supportMediaListSyn.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.SupportTicketResultResponse) {
                    responseObject.response?.let { response ->
                        if (supportDetailsList.size > 0) {
                            val list = ArrayList<Model.SupportTicketsMediaResponse>()
                            supportDetailsList.get(0).medias?.let { mediaList -> list.addAll(mediaList) }
                            response.mediaType = supportMediaListSyn.get(index).mediaType
                            list.add(response)
                            supportDetailsList.get(0).medias = list
                            DataProvider.threadBlock {
                                DataProvider.application?.database?.addSupportTicketDao()?.update(supportDetailsList.get(0))
                                DataProvider.application?.database?.addSupportMediaDao()?.deleteItem(supportMediaListSyn.get(index))
                            }
                            if (index != supportMediaListSyn.size - 1) {
                                saveSupportMediaList(index + 1)
                            } else {
                                saveSupportDetail()
                            }
                        }
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                if (responseObject.equals(GMKeys.FILE_NOT_FOUND)) {
                    DataProvider.threadBlock {
                        DataProvider.application?.database?.addSupportMediaDao()?.deleteItem(supportMediaListSyn.get(index))
                    }
                    if (index != supportMediaListSyn.size - 1) {
                        saveSupportMediaList(index + 1)
                    } else {
                        saveSupportDetail()
                    }
                } else {
                    saveSupportMediaList(index)
                }

            }
        })
    }

    private fun saveSupportMediaList() {
        DataProvider.threadBlock {
            DataProvider.application?.database?.addSupportTicketDao()?.getall().let { list ->
                list?.let {
                    supportDetailsList = ArrayList(list)
                    if (supportDetailsList.size > 0) {
                        supportDetailsList.get(0).supportId?.let {
                            DataProvider.application?.database?.addSupportMediaDao()?.getItemById(it)?.let {

                                supportMediaListSyn = ArrayList(it)
                            }
                        }

                        if (supportMediaListSyn.size > 0) {
                            saveSupportMediaList(0)
                        }
                    }
                }
            }
        }

    }


    fun saveSupportDetail() {
        if (supportDetailsList.size > 0)
            ServiceWrapper.createSupportTicket(supportDetailsList.get(0), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    DataProvider.threadBlock {
                        supportDetailsList.get(0).supportId?.let { DataProvider.application?.database?.addSupportTicketDao()?.deleteSynSupport(it) }
                        saveSupportMediaList()
                    }
                }

                override fun onRequestFailed(responseObject: String) {

                }
            })
    }





    /*Support comment Update Sync */

    private fun saveSupportUpdateMediaList() {
        DataProvider.threadBlock {
            DataProvider.application?.database?.updateSupportTicketDao()?.getall().let { list ->
                list?.let {
                    supportUpdateDetailsList = ArrayList(list)
                    if (supportUpdateDetailsList.size > 0) {
                        supportUpdateDetailsList.get(0).supportOfflineId?.let {
                            DataProvider.application?.database?.addCommentMediaDao()?.getItemById(it)?.let {

                                supportUpdateMediaListSyn = ArrayList(it)
                            }
                        }

                        if (supportUpdateMediaListSyn.size > 0) {
                            saveSupportUpdateMediaList(0)
                        }else{
                            saveSupportUpdateDetail()
                        }
                    }
                }
            }
        }

    }


    fun saveSupportUpdateMediaList(index: Int) {
        ServiceWrapper.uploadCommentMedia(supportUpdateMediaListSyn.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.SupportTicketResultResponse) {
                    responseObject.response?.let { response ->
                        if (supportUpdateDetailsList.size > 0) {
                            val list = ArrayList<Model.SupportTicketsMediaResponse>()
                            supportUpdateDetailsList.get(0).medias?.let { mediaList -> list.addAll(mediaList) }
                            response.mediaType = supportUpdateMediaListSyn.get(index).mediaType
                            list.add(response)
                            supportUpdateDetailsList.get(0).medias = list
                            DataProvider.threadBlock {
                                DataProvider.application?.database?.updateSupportTicketDao()?.update(supportUpdateDetailsList.get(0))
                                DataProvider.application?.database?.addCommentMediaDao()?.deleteItem(supportUpdateMediaListSyn.get(index))
                            }
                            if (index != supportUpdateMediaListSyn.size - 1) {
                                saveSupportUpdateMediaList(index + 1)
                            } else {
                                saveSupportUpdateDetail()
                            }
                        }
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                if (responseObject.equals(GMKeys.FILE_NOT_FOUND)) {
                    DataProvider.threadBlock {
                        DataProvider.application?.database?.addCommentMediaDao()?.deleteItem(supportUpdateMediaListSyn.get(index))
                    }
                    if (index != supportUpdateMediaListSyn.size - 1) {
                        saveSupportUpdateMediaList(index + 1)
                    } else {
                        saveSupportUpdateDetail()
                    }
                } else if(!responseObject.contains("check your network",true)){
                        saveSupportUpdateMediaList(index)
                }
            }
        })
    }


    fun saveSupportUpdateDetail() {
        if (supportUpdateDetailsList.size > 0)
            ServiceWrapper.updateSupportTicket(supportUpdateDetailsList.get(0), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    DataProvider.threadBlock {
                        supportUpdateDetailsList.get(0).supportOfflineId?.let { DataProvider.application?.database?.updateSupportTicketDao()?.deleteSynSupport(it) }
                        saveSupportUpdateMediaList()
                    }
                }

                override fun onRequestFailed(responseObject: String) {

                }
            })
    }


    /*Activity sync*/


    fun saveActivityMediaList(index: Int) {
        ServiceWrapper.uploadMedia(activityMediaListSyn.get(index), object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (index != activityMediaListSyn.size - 1) {
                    saveActivityMediaList(index + 1)
                } else {
                    saveActivityDetail()
                }
            }

            override fun onRequestFailed(responseObject: String) {
                if (responseObject.equals(GMKeys.FILE_NOT_FOUND)) {
                    DataProvider.threadBlock {
                        DataProvider.application?.database?.uploadActivityMediaDao()?.deleteItem(activityMediaListSyn.get(index))
                    }
                    saveActivityMediaList(index + 1)
                } else {
                    saveActivityMediaList(index)
                }

            }
        })
    }

    private fun saveActivityMediaList() {
        DataProvider.threadBlock {
            DataProvider.application?.database?.addActivityDetailsDao()?.getall().let { list ->
                list?.let {
                    activityDetailsList = ArrayList(list)
                    if (activityDetailsList.size > 0) {
                        activityDetailsList.get(0).activityId?.let {
                            DataProvider.application?.database?.uploadActivityMediaDao()?.getItemById(it.toInt())?.let {

                                activityMediaListSyn = ArrayList(it)
                            }
                        }

                        if (activityMediaListSyn.size > 0) {
                            saveActivityMediaList(0)
                        }
                    }
                }
            }
        }

    }

    fun saveActivityDetail() {
        if (activityDetailsList.size > 0)
            ServiceWrapper.saveActivityDetails(activityDetailsList.get(0), object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    DataProvider.threadBlock {
                        activityDetailsList.get(0).activityId?.let { DataProvider.application?.database?.addActivityDetailsDao()?.deleteSynActivity(it.toInt()) }
                        saveActivityMediaList()
                    }
                }

                override fun onRequestFailed(responseObject: String) {

                }
            })
    }

}