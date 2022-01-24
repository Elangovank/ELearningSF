package com.gm.utilities

import com.gm.GMApplication
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gmcoreui.utils.DateUtils

object DataRepository {
    var startDate: String = ""
    var endDate: String = ""
    var repositoryId: Long? = null

    fun getServerTime(isStartDate: Boolean = true) {
        if (NetworkAvailability.isNetworkAvailable(DataProvider.getContext())){
            ServiceWrapper.getServerTime(object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.ServerTime) {
                        if (isStartDate)
                            startDate = responseObject.time!!
                        else {
                            endDate = responseObject.time!!
                            getStatus()
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    if (isStartDate)
                        startDate = DateUtils.getTodayDate()
                    else
                        endDate = DateUtils.getTodayDate()
                }
            })
        }else{
            if (isStartDate)
                startDate = DateUtils.getTodayDate()
            else
                endDate = DateUtils.getTodayDate()
        }
    }

    private fun getStatus() {
        repositoryId?.let {
            ServiceWrapper.getRepositoryStatus(startDate, endDate, repositoryId!!, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.RepositoryStatus) {

                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    repositoryId = null
                }
            })
        }
    }
}