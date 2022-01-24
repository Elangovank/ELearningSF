package com.gm.WebServices

import com.gm.GMApplication
import com.gm.R

object URLUtils {
    val baseUrl: String
        get() {
            return GMApplication.appContext!!.getString(R.string.base_url)
        }

    val apiBaseUrl: String
        get() {
            return GMApplication.appContext!!.getString(R.string.base_url) + GMApplication.appContext!!.getString(R.string.api)
        }

    val getValidateUrl: String
        get() {
            val res = GMApplication.appContext!!.resources
            return apiBaseUrl + res.getString(R.string.validate_url)
        }

    val getLoginAuthorizeUrl: String
        get() {
            val res = GMApplication.appContext!!.resources
            return apiBaseUrl + res.getString(R.string.authorize_url)
        }
    val getLogoutUrl: String
        get() {
            val res = GMApplication.appContext!!.resources
            return apiBaseUrl + res.getString(R.string.authorize_logout_url)
        }

    val validateFarmUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.validate_farm)
        }

    val savePasswordUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.save_password_url)
        }
    val getOtpUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.otp_url)
        }
    val getVerifyOtpUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.verify_otp)
        }
    val getLanguageList: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.language_list)
        }
    val getPendingActivityListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.list_of_pending_activities_url)
        }

    val getServerTimeUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.server_time)
        }
    val getCompletedActivityListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.list_of_completed_activities_url)
        }

    val getMediaUploadUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.media_upload)
        }


    val getRepositoryListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.repository_list)
        }
    val getFeedbackeUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_details)
        }
    val getFeedbackSaveUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_save)
        }


    val getFeedbackRatingUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_rating_save)
        }
    val getRepositoryStatusUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.repository_status_update)
        }

    val getSupportListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_list)
        }

    val getSaveLanguage: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.user_language_save)
        }

    val getSupportQuestionListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_question_list)
        }

    val getSupportQuestionSubmitListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_question_submit_list)
        }

    val getSupportCommentUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_comment)
        }


    val getFCMKeySave: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.user_pushtokenkey_add)
        }


    val getGetReoitoryCategoriesList: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.repository_categories)
        }

    val getNotificationList: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.notification_list)
        }

    val getSupportDetailUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_detail)
        }
    val getReoitoryDetails: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.repository_detail)
        }

    val getNotificationStatus: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.notification_status_update)
        }
    val getActivityText: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.activity_response_text)
        }

    val getActivityResponseSave: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.activity_response_save)
        }
    val getActivityProgress: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.activity_progress)
        }

    val getHistoryPending: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.history_pending)
        }
    val getHistoryCompleted: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.history_completed)
        }

    val saveSupportTicket: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_ticket)
        }
    val getCreateSupportTicket: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.create_support)
        }

    val getFarmsListSupportTicket: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.list_questions)
        }

    val geSupportTicketIssueDetailsUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_raise_request_details)
        }

    val geSupportTicketIssueListDetailsUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.support_raise_request_details)
        }

    val getFeedBackQuestionListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_questions)
        }


    val getFeedBackSaveMediaUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_save_media)
        }
    val getFeedBackMediaUploadUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_upload_media)
        }
    val getDailyReportHistoryUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.report_history)
        }
    val getFeedBackHistoryUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.feedback_history)
        }


    val addCommentUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.add_comment_url)
        }

    val getTodayMaterialArrivalListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.today_material_arrival_list)
        }

    val getTodayMaterialArrivalUpdateUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.today_material_arrival_update)
        }

    val getMaterialHistoryListUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.material_history_list)
        }

    val getMaterialArrivalHistoryUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.history_details)
        }
    val getPerformanceUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.performance_chart_details)
        }
    val getPerformanceChartUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.performance_chart_detais_url)
        }

    val getActivityDetailByIdUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.get_activity_by_id)
        }


    val getResourceStringUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.language_key)
        }



    val getDailyReportReasonUrl: String
        get() {
            return apiBaseUrl + GMApplication.appContext!!.getString(R.string.daily_report_reason)
        }

}