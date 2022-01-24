package com.gm.WebServices

import android.content.Context
import com.core.utils.AppPreferences
import com.gm.GMApplication
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gmcoreui.utils.ActivityUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import gm.service.network.APICompletionListener
import gm.service.network.GMAPIService
import java.io.File

object ServiceWrapper {
    fun getContext(): Context {
        return GMApplication.appContext!!
    }

    fun getAuthToken(): String? {
        return AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.KEY_TOKEN_2)
    }

    fun postService(input: String, url: String, listener: APICompletionListener, requestCode: Int = 100) {
        val token = getAuthToken()
        GMAPIService(URLUtils.baseUrl).processPostURL(requestCode, getContext(), url, input, object : APICompletionListener {
            override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                listener.onRequestCompleted(requestCode, responseObject)

            }

            override fun onRequestFailed(requestCode: Int, responseObject: String) {
                if (GMKeys.HTTP_401_unauthorized.equals(responseObject, true)) {
                    IntentUtils.intent(getContext())
                } else {
                    listener.onRequestFailed(requestCode, responseObject)
                }
            }
        }, token)
    }

    fun getService(url: String, listener: APICompletionListener, requestCode: Int = 100) {
        GMAPIService(URLUtils.baseUrl).processGetURL(requestCode, getContext(), url, object : APICompletionListener {
            override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                listener.onRequestCompleted(requestCode, responseObject)
            }

            override fun onRequestFailed(requestCode: Int, responseObject: String) {
                if (GMKeys.HTTP_401_unauthorized.equals(responseObject, true)) {
                    IntentUtils.intent(getContext())
                } else {
                    listener.onRequestFailed(requestCode, responseObject)
                }
            }
        }, getAuthToken())
    }
/*
    public final fun processMediaUpload(requestCode: kotlin.Int, body: kotlin.String, context: android.content.Context, url: kotlin.String, file: java.io.File,
                                        completionListener: gm.service.network.APICompletionListener, authToken: kotlin.String?): kotlin.Unit { */
/* compiled code *//*
 }
    fun postMediaUpload(input: String, url: String, mediaPath: String?, listener: APICompletionListener, requestCode: Int = 105) {
        GMAPIService(URLUtils.baseUrl).processMediaUpload(requestCode,
                input,
                getContext(),
                url,
                File(mediaPath ?: ""),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(requestCode, responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        if (GMKeys.HTTP_401_unauthorized.equals(responseObject, true)) {
                            IntentUtils.intent(getContext())
                        } else {
                            listener.onRequestFailed(requestCode, responseObject)
                        }
                    }
                }, getAuthToken())
    }
*/


    fun postMediaActivityUpload(input: String, url: String, mediaPath: String?, listener: APICompletionListener, requestCode: Int = 105) {
        val fileList = ArrayList<File>()
        fileList.add(File(mediaPath ?: ""))
        GMAPIService(URLUtils.baseUrl).processImageUploadWithBody(requestCode,
                input,
                getContext(),
                url,
                fileList,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(requestCode, responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        if (GMKeys.HTTP_401_unauthorized.equals(responseObject, true)) {
                            IntentUtils.intent(getContext())
                        } else {
                            listener.onRequestFailed(requestCode, responseObject)
                        }
                    }
                }, getAuthToken())
    }


    fun postMediaUpload(input: String, url: String, mediaPath: String?, listener: APICompletionListener, requestCode: Int = 105) {
        //  val fileList=ArrayList<File>()
        //  fileList.add( File(mediaPath ?: ""))
        GMAPIService(URLUtils.baseUrl).processImageUpload(requestCode,
                //   input,
                getContext(),
                url,
                File(mediaPath ?: ""),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(requestCode, responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        if (GMKeys.HTTP_401_unauthorized.equals(responseObject, true)) {
                            IntentUtils.intent(getContext())
                        } else {
                            listener.onRequestFailed(requestCode, responseObject)
                        }
                    }
                }, getAuthToken())
    }


    fun getLevel1Token(clientKey: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("ClientKey", clientKey)
        json.addProperty("ClientType", GMKeys.clientType)
        GMAPIService(URLUtils.baseUrl).getLevel1Token(100,
                getContext(),
                URLUtils.getValidateUrl,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun login(token: String, farmCode: String, password: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.FARMCODE, farmCode)
        json.addProperty(GMKeys.PASSWORD, password)
        json.addProperty(GMKeys.PushTokenKey, ActivityUtils.getFCMToken())
        // json.addProperty(GMKeys.LanguageId, GMApplication.languageID)
        GMAPIService(URLUtils.baseUrl).getLevel2Token(101,
                getContext(),
                URLUtils.getLoginAuthorizeUrl,
                json.toString(),
                token,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {

                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun logout(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        // json.addProperty(GMKeys.LanguageId, GMApplication.languageID)
        postService(json.toString(), URLUtils.getLogoutUrl, object : APICompletionListener {
            override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                listener.onRequestCompleted(responseObject)
            }

            override fun onRequestFailed(requestCode: Int, responseObject: String) {
                listener.onRequestFailed(responseObject)
            }
        })
    }

    fun validateFarm(leveltoken_1: String, farmCode: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("FarmCode", farmCode)
        GMAPIService(URLUtils.baseUrl).processPostURL(101,
                getContext(),
                URLUtils.validateFarmUrl,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(
                                Gson().fromJson(responseObject as String, Model.ValidateUserResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {

                        listener.onRequestFailed(responseObject)
                    }
                }, leveltoken_1)
    }

    fun savePassowrd(leveltoken_1: String, password: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("LanguageId", GMApplication.languageID)
        json.addProperty("Password", password)
        GMAPIService(URLUtils.baseUrl).processPostURL(101,
                getContext(),
                URLUtils.savePasswordUrl,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {

                        listener.onRequestFailed(responseObject)
                    }
                }, leveltoken_1)
    }

    fun getOtp(level1Token: String, farmCode: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("FarmCode", farmCode)
        GMAPIService(URLUtils.baseUrl).processPostURL(101,
                getContext(),
                URLUtils.getOtpUrl,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(responseObject)
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {

                        listener.onRequestFailed(responseObject)
                    }
                }, level1Token)
    }

    fun getVerifyOtp(level1Token: String, farmCode: String, otpNumber: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("FarmCode", farmCode)
        json.addProperty("OtpNumber", otpNumber)
        GMAPIService(URLUtils.baseUrl).processPostURL(101,
                getContext(),
                URLUtils.getVerifyOtpUrl,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(
                                Gson().fromJson(responseObject as String, Model.LoginResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {

                        listener.onRequestFailed(responseObject)
                    }
                }, level1Token)
    }

    fun getLanguageList(loginUserId: Long, level1Token: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.LOGIN_USER_ID, loginUserId)
        GMAPIService(URLUtils.baseUrl).processPostURL(101,
                getContext(),
                URLUtils.getLanguageList,
                json.toString(),
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(
                                Gson().fromJson(responseObject as String, Model.LanguageList::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                }, level1Token)
    }


    fun getPendingActivityList(pageNo: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("PageNo", pageNo)
        postService(json.toString(),
                URLUtils.getPendingActivityListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ActivityListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getCompletedActivityList(date: String = "", age: Int = -1, pageNo: Int, flage: Int = 0, isShedReadyActivity: Boolean, listener: ServiceRequestListener) {
        val json = JsonObject()
        var url: String? = null
        if (flage == 0) {
            url = URLUtils.getCompletedActivityListUrl
        } else if (flage == 1) {
            url = URLUtils.getHistoryCompleted
        } else {
            url = URLUtils.getHistoryPending
        }
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("PageNo", pageNo)
        json.addProperty("IsShedReadyActivity", isShedReadyActivity)
        if (!date.equals("")) {
            json.addProperty("FilterDate", date)
        } else if (!age.equals(-1)) {
            json.addProperty("FilterAge", age)
        } else {
            json.addProperty("FilterAge", age)
        }
        val str = json.toString()
        postService(str,
                url,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ActivityListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun uploadMedia(offlineActivity: Model.OfflineMediaActivity, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("ActivityId", offlineActivity.activityId)
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("MediaTypeId", offlineActivity.mediaTypeId)
        json.addProperty("Latitude", offlineActivity.latitude)
        json.addProperty("Longitude", offlineActivity.longitude)
        if (File(offlineActivity.mediaPath ?: "").exists()) {
            postMediaActivityUpload(
                    json.toString(),
                    URLUtils.getMediaUploadUrl,
                    offlineActivity.mediaPath,
                    object : APICompletionListener {
                        override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                            listener.onRequestCompleted(responseObject)
                        }

                        override fun onRequestFailed(requestCode: Int, responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
        } else {
            listener.onRequestFailed(GMKeys.FILE_NOT_FOUND)
        }
    }

    fun getRepositoryList(repositoryCategoryId: Int, pageNo: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty(GMKeys.RepositoryCategoryId, repositoryCategoryId)
        json.addProperty(GMKeys.PageNo, pageNo)
        postService(json.toString(),
                URLUtils.getRepositoryListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.RepositoryListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getProgressResult(date: String, age: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)

        if (!date.equals("")) {
            json.addProperty("FilterDate", date)
        }
        json.addProperty("FilterAge", age)
        postService(json.toString(),
                URLUtils.getActivityProgress,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.PrograssBarResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getRepositoryStatus(startDate: String, endDate: String, repositoryId: Long, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("Id", repositoryId.toInt())
        json.addProperty("ReadStartTime", startDate)
        json.addProperty("ReadEndTime", endDate)
        postService(
                json.toString(),
                URLUtils.getRepositoryStatusUrl,

                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.RepositoryStatus::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getFeedBackDetails(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getFeedbackeUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.DailyReportDetailsResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getServerTime(listener: ServiceRequestListener) {
        getService(
                URLUtils.getServerTimeUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ServerTime::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun saveActivityDetails(saveDetails: Model.SaveActivityDetails, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty("ActivityId", saveDetails.activityId)
        if (saveDetails.comment != "") {
            json.addProperty("Comment", saveDetails.comment)
        }
        if (saveDetails.textAnswer != "") {
            json.addProperty("Answer", saveDetails.textAnswer)
        }
        postService(json.toString(),
                URLUtils.getActivityResponseSave,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ActivityResponsesSave::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun postDailyReportFeedBack(feedBackSubmitRequest: Model.FeedBackSubmitRequest, listener: ServiceRequestListener) {
        val json = Model.toJson(feedBackSubmitRequest)
        postService(json.toString(),
                URLUtils.getFeedbackSaveUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.FeedBackResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getSupportTypeList(SupportCategoryId: Int?, pageNo: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty(GMKeys.SupportCategoryId, SupportCategoryId)
        json.addProperty(GMKeys.PageNo, pageNo)
        postService(json.toString(),
                URLUtils.getSupportListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getSupportQuestionList(SupportId: Int?, pageNo: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty(GMKeys.SupportId, SupportId)
        postService(json.toString(),
                URLUtils.getSupportQuestionListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportQuestionListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun saveLanguage(languageId: Long, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("LanguageId", languageId)
        json.addProperty("UserId", GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getSaveLanguage,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.LanguageResponse1::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun postFCMKey(fcmKey: String, listener: ServiceRequestListener) {
        if (GMApplication.loginUserId != 0L) {
            val json = JsonObject()
            json.addProperty(GMKeys.PushTokenKey, fcmKey)
            json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
            postService(
                    json.toString(),
                    URLUtils.getFCMKeySave,
                    object : APICompletionListener {
                        override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                            listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.PushTokenMessageResponse::class.java))
                        }

                        override fun onRequestFailed(requestCode: Int, responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
        }
    }


    fun getRepositoryList(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getGetReoitoryCategoriesList,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.RepositoryCategoriesList::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getNotificationList(pageNo: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty(GMKeys.PageNo, pageNo)
        postService(json.toString(),
                URLUtils.getNotificationList,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.NotificaitonListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getSuportDetails(listener: ServiceRequestListener) {
        getService(
                URLUtils.getSupportDetailUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getRepositoryDetails(repositoryId: Int, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        json.addProperty("RepositoryId", repositoryId)
        postService(json.toString(),
                URLUtils.getReoitoryDetails,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.RepositoryDetailResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getNotificationStatus(notificationId: Long, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("Id", notificationId)
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getNotificationStatus,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.RepositoryStatus::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun createSupportTicket(item: Model.SupportCreateTicket, listener: ServiceRequestListener) {
        val postJson = Model.toJson(item)
        postService(postJson.toString(),
                URLUtils.getCreateSupportTicket,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportCreateTicketResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun updateSupportTicket(item: Model.SupportUpdate, listener: ServiceRequestListener) {
        val postJson = Model.toJson(item)
        postService(postJson,
                URLUtils.addCommentUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportCreateTicketResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getFarmsListDetails(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getFarmsListSupportTicket,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportDetailListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getIssueDetails(ticket: Long, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("TicketId", ticket)
        postService(json.toString(),
                URLUtils.geSupportTicketIssueDetailsUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportIssueListDetailsResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun uploadSupportMedia(offlineFeedBack: Model.OfflineSupportMediaList, listener: ServiceRequestListener) {
        if (File(offlineFeedBack.mediaPath ?: "").exists()) {
            postMediaUpload(
                    "",
                    URLUtils.saveSupportTicket,
                    offlineFeedBack.mediaPath,
                    object : APICompletionListener {
                        override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                            listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportTicketResultResponse::class.java))
                        }

                        override fun onRequestFailed(requestCode: Int, responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
        } else {
            listener.onRequestFailed(GMKeys.FILE_NOT_FOUND)
        }
    }


    fun getFeedbackQuestion(listener: ServiceRequestListener) {
        getService(
                URLUtils.getFeedBackQuestionListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.FeedBackQuestionListResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getFeedbackSaveRating(feedBackSaveDetails: Model.FeedBackSaveDetails, listener: ServiceRequestListener) {
        val postJson = Model.toJson(feedBackSaveDetails)
        postService(postJson.toString(),
                URLUtils.getFeedBackSaveMediaUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.FeedBackResponse1::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getDailyReportHistory(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getDailyReportHistoryUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.DailyReportHistoryResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getFeedBackHistory(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.UserId, GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getFeedBackHistoryUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.FeedbackHistoryResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun uploadFeedBackMedia(offlineFeedBack: Model.OfflineFeedBackMediaList, listener: ServiceRequestListener) {
        if (File(offlineFeedBack.mediaPath ?: "").exists()) {
            postMediaUpload(
                    "",
                    URLUtils.getFeedBackMediaUploadUrl,
                    offlineFeedBack.mediaPath,
                    object : APICompletionListener {
                        override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                            listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.FeedbackResultResponse::class.java))
                        }

                        override fun onRequestFailed(requestCode: Int, responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
        } else {
            listener.onRequestFailed(GMKeys.FILE_NOT_FOUND)
        }
    }


    fun uploadCommentMedia(offlineFeedBack: Model.OfflineUpdateSupportMediaList, listener: ServiceRequestListener) {
        if (File(offlineFeedBack.mediaPath ?: "").exists()) {
            postMediaUpload(
                    "",
                    URLUtils.saveSupportTicket,
                    offlineFeedBack.mediaPath,
                    object : APICompletionListener {
                        override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                            listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.SupportTicketResultResponse::class.java))
                        }

                        override fun onRequestFailed(requestCode: Int, responseObject: String) {
                            listener.onRequestFailed(responseObject)
                        }
                    })
        } else {
            listener.onRequestFailed(GMKeys.FILE_NOT_FOUND)
        }
    }


    fun getTodayMaterialArrivalList(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.FARMCODE, GMApplication.farmCode)
        postService(json.toString(),
                URLUtils.getTodayMaterialArrivalListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.MaterialArrivalResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getMaterialHistoryList(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.FARMCODE, GMApplication.farmCode)
        postService(json.toString(),
                URLUtils.getMaterialHistoryListUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.HistoryMaterialArrivalResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getMaterialHistoryDetails(date: String, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("ExpectedDate", date)
        json.addProperty(GMKeys.FARMCODE, GMApplication.farmCode)
        postService(json.toString(),
                URLUtils.getMaterialArrivalHistoryUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.HistoryMaterialArrivalDetailResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getTodayMaterialUpdate(arrivalDetail: Model.DispatchArrivalDetailsList, listener: ServiceRequestListener) {
        val postJson = Model.toJson(arrivalDetail)
        postService(postJson.toString(),
                URLUtils.getTodayMaterialArrivalUpdateUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.DispatchHistory::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getPerformanceReport(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty(GMKeys.FARMCODE, GMApplication.farmCode)
        postService(json.toString(),
                URLUtils.getPerformanceUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.PerformanceReportResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getActivityDetailById(activityId: Long, listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        json.addProperty("ActivityId", activityId)
        postService(json.toString(),
                URLUtils.getActivityDetailByIdUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ActivityResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }


    fun getDailyReportReason(listener: ServiceRequestListener) {
        val json = JsonObject()
        json.addProperty("UserId", GMApplication.loginUserId)
        postService(json.toString(),
                URLUtils.getDailyReportReasonUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.DailyReportReasonsResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getPerformanceChart(batchIdList: Model.ChartInput, listener: ServiceRequestListener) {
        val postJson = Model.toJson(batchIdList)
        postService(postJson.toString(),
                URLUtils.getPerformanceChartUrl,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.PerformanceChartResponse
                        ::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

    fun getResourceString(languageCode:String,listener: ServiceRequestListener) {
        getService(
                URLUtils.getResourceStringUrl+languageCode,
                object : APICompletionListener {
                    override fun onRequestCompleted(requestCode: Int, responseObject: Any) {
                        listener.onRequestCompleted(Gson().fromJson(responseObject as String, Model.ResourceStringResponse::class.java))
                    }

                    override fun onRequestFailed(requestCode: Int, responseObject: String) {
                        listener.onRequestFailed(responseObject)
                    }
                })
    }

}