package com.gm.models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gm.db.CustomTypeConvertors.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

object Model {

    fun toJson(param: Any): String {
        return param.let { Gson().toJson(param).toString() }
    }

    open class BaseModel : Serializable

    data class LanguageList(
            @SerializedName("response")
            var response: List<Language>? = null) : BaseModel()

    data class MessageResponse(
            @SerializedName("message")
            var message: List<Language>? = null) : BaseModel()

    data class PushTokenMessageResponse(
            @SerializedName("message")
            var message: String? = null) : BaseModel()

    data class Language(
            @SerializedName("languageId")
            var languageId: Int? = null,
            @SerializedName("language")
            var language: String? = null,
            @SerializedName("isActive")
            var isActive: Boolean? = false,
            @SerializedName("postStatus")
            var postStatus: Boolean? = false,
            @SerializedName("postMessage")
            var postMessage: String? = null,
            @SerializedName("languageCode")
            var languageCode: String? = null


    ) : BaseModel()


    data class LanguageResponse1(
            @SerializedName("response")
            var response: Model.Language? = null
    ) : BaseModel()

    data class ValidateUserResponse(
            @SerializedName("response")
            var response: ValidateUser? = null) : BaseModel()

    data class ValidateUser(
            @SerializedName("isRegistered")
            var isRegistered: Boolean? = null) : BaseModel()

    data class LoginResponse(
            @SerializedName("response")
            var loginData: LoginData? = null) : BaseModel()

    data class LoginData(
            @SerializedName("loginToken")
            var loginToken: String? = null,
            @SerializedName("userId")
            var userId: Long? = null,
            @SerializedName("roleId")
            var roleId: Int? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("farmCode")
            var farmCode: String? = null,
            @SerializedName("languageId")
            var languageId: Long? = null,
            @SerializedName("mobileNumber")
            var mobileNumber: String? = null,
            @SerializedName("latitude")
            var latitude: String? = null,
            @SerializedName("longitude")
            var longitude: String? = null,
            @SerializedName("village")
            var village: String? = null,
            @SerializedName("postalCode")
            var postalCode: String? = null

    ) : BaseModel()

    data class ActivityListResponse(
            @SerializedName("response")
            var activityList: ArrayList<ActivityList>? = null
    ) : BaseModel()

    @Entity
    data class ActivityResponse(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("response")
            var activity: ActivityList? = null
    ) : BaseModel()


    data class WeatherDetails(
            var list: ArrayList<WeatherDetailsList>? = null
    )

    data class WeatherDetailsList(
            var weatherlist: ArrayList<DailyWeatherData>? = null
    ) : Serializable

    @Entity
    data class ActivityList(
            @PrimaryKey
            @SerializedName("activityId")
            var activityId: Long? = null,
            @SerializedName("title")
            var title: String? = null,
            @SerializedName("activityCategoryId")
            var activityCategoryId: Int? = null,
            @SerializedName("message")
            var message: String? = null,
            @SerializedName("age")
            var age: Int? = null,
            @SerializedName("defaultActivity")
            var defaulAtctivity: String? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("videos")
            var videos: ArrayList<Media> = ArrayList(),
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("audios")
            var audios: ArrayList<Media> = ArrayList(),
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("images")
            var images: ArrayList<Media> = ArrayList(),
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("pdfs")
            var pdfs: ArrayList<Media> = ArrayList(),
            @SerializedName("status")
            var status: Boolean? = null,
            @SerializedName("hatchDate")
            var hatchDate: String? = null,
            @SerializedName("isUpload")
            var isUpload: Boolean? = null,
            var isPending: Boolean = true,
            @SerializedName("isAudio")
            var allowAudio: Boolean? = null,
            @SerializedName("isVideo")
            var allowVideo: Boolean? = null,
            @SerializedName("isImage")
            var allowImage: Boolean? = null,
            @SerializedName("recommended")
            var recommended: String? = null,
            @SerializedName("question")
            var question: String? = null,
            @SerializedName("alternateQuestion")
            var alternateQuestion: String? = null,
            @SerializedName("uom")
            var uOM: String? = null,
            @SerializedName("response")
            var response: String? = null,
            @SerializedName("textAnswer")
            var textAnswer: String? = null,
            @SerializedName("comment")
            var comment: String? = null,
            @SerializedName("colourCode")
            var colourCode: String? = null,
//            below properties for only completed activities
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("responseVideos")
            var responseVideos: ArrayList<Media>? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("responseAudios")
            var responseAudios: ArrayList<Media>? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("responseImages")
            var responseImages: ArrayList<Media>? = null,
            @SerializedName("progress")
            var progress: Double? = null,
            @SerializedName("activityTypeId")
            var activityTypeId: Int? = null,
            @SerializedName("endRange")
            var endRange: Int? = null,
            @SerializedName("startRange")
            var startRange: Int? = null,
            @SerializedName("options")
            var options: String? = null,
            @SerializedName("responseTypeId")
            var responseTypeId: Long? = null,
            @SerializedName("lastModifiedDate")
            var lastModifiedDate: String? = null,
            @SerializedName("isCompleted")
            var isCompleted: Boolean? = null,
            @SerializedName("activityAge")
            var activityAge: String? = null,
            @SerializedName("hint")
            var hint: String? = null,
            @SerializedName("fromDate")
            var fromDate: String? = null,
            @SerializedName("toDate")
            var toDate: String? = null
    ) : BaseModel()

    data class SelectedItemList(
            var itemList: ArrayList<SelectedItem>? = null
    ) : BaseModel()

    data class SelectedItem(
            var item: String? = null,
            var isSelected: Boolean? = false

    ) : BaseModel()

    @Entity
    data class DownLoadInput(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("videoId")
            var videoId: Long? = null,
            @SerializedName("activityId")
            var activityId: Long? = null,
            @SerializedName("path")
            var path: String? = null

    ) : BaseModel()

    @Entity
    data class DailyReportDetailsResponse(
            @PrimaryKey
            var id: Long?=0,
            @TypeConverters(DailyReportDetailsConventer::class)
            @SerializedName("response")
            var response: DailyReportDetails? = null
    ) : BaseModel()

    @Entity
    data class DailyReportDetails(
            @PrimaryKey
            var id: Long?=0,
            @SerializedName("age")
            var age: Int? = null,
            @SerializedName("birdStock")
            var birdStock: String? = null,
            @SerializedName("uomConversion")
            var uomConversion: Long? = null,
            @SerializedName("feedConsumptionUnit")
            var feedConsumptionUnit: Int? = null,
            @SerializedName("feedStandard")
            var feedStandard: Int? = null,
            @SerializedName("feedConsumptionMin")
            var feedConsumptionMin: Double? = null,
            @SerializedName("feedConsumptionMax")
            var feedConsumptionMax: Double? = null,
            @SerializedName("isFeedStandardAvailable")
            var isFeedStandardAvailable: Boolean? = null,
            @TypeConverters(DailyReportSupportConventer::class)
            @SerializedName("dailySupport")
            var dailySupport: DailyReportSupport? = null,
            @TypeConverters(ReasonConventer::class)
            @SerializedName("reasons")
            var reasons: ArrayList<Reasons>? = null,
            var feedKg: Double? = null,
            var feedBags: Double? = null
    ) : BaseModel()

    data class DailyReportSupport(
            @SerializedName("isDailyFeedback")
            var isDailyFeedback: Boolean? = null,
            @SerializedName("feedStock")
            var feedStock: Double? = null,
            @SerializedName("receipt")
            var receipt: Double? = null,
            @SerializedName("enableFeedConsumption")
            var enableFeedConsumption: Boolean? = null,
            @SerializedName("feedConsumption")
            var feedConsumption: Double? = null,
            @SerializedName("feedConsumptionUOM")
            var feedConsumptionUOM: String? = null,
            @SerializedName("feedTransfer")
            var feedTransfer: Double? = null,
            @SerializedName("mortality")
            var mortality: Int? = null,
            @SerializedName("bodyWeight")
            var bodyWeight: Double? = null,
            @SerializedName("reason")
            var reason: String? = null,
            @SerializedName("feedConsumptionReason")
            var feedConsumptionReason: Int? = null,
            @SerializedName("toleranceIncrementDecrementValue")
            var toleranceIncrementDecrementValue: Double? = null


    ) : BaseModel()
    @Entity
    data class Reasons(

            @SerializedName("text")
            var text: String? = null,
            @PrimaryKey
            @SerializedName("value")
            var value: Long? = null

    ) : BaseModel() {
        override fun toString(): String {
            return text ?: ""
        }
    }

    @Entity
    data class SupportDetails(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("supportId")
            var supportId: Long? = null,
            @SerializedName("count")
            var count: Long? = null
    ) : BaseModel()

    @Entity
    data class SupportResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(SupportTypeConvertor::class)
            @SerializedName("response")
            var response: SupportDetailsList? = null

    ) : BaseModel()

    @Entity
    data class SupportDetailsList(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("tickets")
            var tickets: Int? = null,
            @TypeConverters(SupportDetailsListConventer::class)
            @SerializedName("supportDetails")
            var supportDetails: ArrayList<SupportDetails>? = null

    ) : BaseModel()

    data class FeedBackResponse1(
            @SerializedName("response")
            var response: Long? = null,
            var feedBackId: Long? = null

    ) : BaseModel()

    data class UploadMedia(
            @SerializedName("id")
            var id: Long? = null,
            @SerializedName("url")
            var url: String? = null,
            var isDownloaded: Boolean = false,
            var mediaType: MediaType = MediaType.Image,
            @SerializedName("ActivityId")
            var activityId: Long? = null,
            @SerializedName("UserId")
            var userId: Long? = null,
            @SerializedName("MediaTypeId")
            var MediaTypeId: Int? = null,
            @SerializedName("Message")
            var message: String? = null,
            @SerializedName("File")
            var file: File? = null,
            var mediaUri: Uri? = null,
            var mediaTypeId: String? = null,
            var isSelected: Boolean? = false,
            var status: Int? = null) : BaseModel()

    data class UploadMediaOffline(
            var id: Long? = null,
            var mediaType: Int? = null,
            var filePath: String? = null
    ) : BaseModel()


    @Entity
    data class Media(
            @PrimaryKey(autoGenerate = true)
            var pId: Long,
            @SerializedName("id")
            var id: Long? = null,
            @SerializedName("url")
            var url: String? = null,
            var isSelected: Boolean? = false,
            var isDownloaded: Boolean = false,
            @TypeConverters(EnumMediaTypeConvertor::class)
            var mediaType: MediaType = MediaType.Image
    ) : BaseModel() {
        val mediaFileName: String
            get() {
                url?.let {
                    try {
                        return it.substring(it.lastIndexOf("/") + 1).split(".")[0]
                    } catch (e: Exception) {
                        return ""
                    }
                }
                return ""
            }
    }

    @Entity
    data class Repository(
            @PrimaryKey
            @SerializedName("repositoryId")
            var repositoryId: Long? = null,
            @SerializedName("repositoryCategoryId")
            var repositoryCategoryId: Long? = null,
            @SerializedName("title")
            var title: String? = null,
            @SerializedName("message")
            var message: String? = null,
            @SerializedName("repositoryImageUrl")
            var repositoryImageUrl: String? = null,
            @SerializedName("uploadedDate")
            var uploadedDate: String? = null,
            @SerializedName("status")
            var status: Boolean? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("videos")
            var videos: ArrayList<Media>? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("audios")
            var audios: ArrayList<Media>? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("images")
            var images: ArrayList<Media>? = null) : BaseModel()

    @Entity
    data class FeedBackSubmitRequest(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("UserId")
            var UserId: Long? = null,
            @SerializedName("FeedStock")
            var FeedStock: Double? = null,
            @SerializedName("Receipt")
            var Receipt: Double? = null,
            @SerializedName("FeedConsumption")
            var FeedConsumption: Double? = null,
            @SerializedName("FeedTransferIn")
            var FeedTransferIn: Double? = null,
            @SerializedName("FeedTransferOut")
            var FeedTransferOut: Double? = null,
            @SerializedName("Mortality")
            var Mortality: Int? = null,
            @SerializedName("BodyWeight")
            var BodyWeight: Double? = null,
            @SerializedName("Chick_Bird")
            var Chick_Bird: Int? = null,
            @SerializedName("Feed")
            var Feed: Int? = null,
            @SerializedName("SupportCenter")
            var SupportCenter: Int? = null,
            @SerializedName("FeedConsumptionReason")
            var feedConsumptionReason: Long? = null,
            @SerializedName("Medicine")
            var Medicine: Int? = null,
            @SerializedName("Reason")
            var Reason: String? = null,
            @SerializedName("RatingReason")
            var RatingReason: String? = null,
            @SerializedName("FeedConsumptionUOM")
            var feedConsumptionUOM: String? = null) : BaseModel()

    @Entity
    data class FeedBackDetailsResponses(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(ReasonsConventer::class)
            @SerializedName("response")
            var response: DailyReportReasons? = null
    ) : BaseModel()

    data class ActivityResponsesSave(
            @SerializedName("response")
            var defaultSubActivityResponseId: Long? = null
    ) : BaseModel()


    data class ServerTime(
            @SerializedName("response")
            var time: String? = null
    ) : BaseModel()

    @Entity
    data class FeedBackDetails(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @SerializedName("isDailyFeedback")
            var IsDailyFeedback: Boolean? = false,
            @SerializedName("feedStock")
            var FeedStock: Double? = null,
            @SerializedName("receipt")
            var Receipt: Double? = null,
            @SerializedName("enableFeedConsumption")
            var enableFeedConsumption: Boolean? = null,
            @SerializedName("feedConsumptionUOM")
            var feedConsumptionUOM: String? = null,
            @SerializedName("feedConsumption")
            var FeedConsumption: Double? = null,
            @SerializedName("feedTransferIn")
            var FeedTransferIn: Double? = null,
            @SerializedName("feedTransfer")
            var FeedTransfer: Double? = null,
            @SerializedName("openBags")
            var OpenBags: Int? = null,
            @SerializedName("mortality")
            var Mortality: Double? = null,
            @SerializedName("bodyWeight")
            var BodyWeight: Double? = null,
            @SerializedName("reason")
            var Reason: String? = null,
            @SerializedName("isRating")
            var IsRating: Boolean? = false,
            @SerializedName("chick_Bird")
            var Chick_Bird: Int? = null,
            @SerializedName("feed")
            var Feed: Int? = null,
            @SerializedName("medicine")
            var Medicine: Int? = null,
            @SerializedName("supportCenter")
            var SupportCenter: Int? = null,
            @SerializedName("ratingReason")
            var RatingReason: String? = null,
            @SerializedName("feedConsumptionReason")
            var feedConsumptionReason: Int? = null,
            @SerializedName("toleranceIncrementDecrementValue")
            var toleranceIncrementDecrementValue: Double? = null
    ) : BaseModel()


    data class FeedBackRating(
            var iconDrawableId: Int,
            var feedName: String? = null,
            var feedBackCount: String? = null,
            var count: Int? = null,
            var list: ArrayList<String>? = null,
            var videoList: ArrayList<Model.SupportTicketsResponse>? = null,
            var audioList: ArrayList<Model.SupportTicketsResponse>? = null,
            var imageList: ArrayList<Model.SupportTicketsResponse>? = null
    ) : BaseModel()


    data class FeedBack(
            var iconDrawableId: Int,
            @SerializedName("feedName")
            var feedName: String? = null,
            @SerializedName("feedBackCount")
            var feedBackCount: String? = null,
            @SerializedName("count")
            var count: Double? = null,
            var increamentValue: Double? = null,
            @SerializedName("isNeedToValidate")
            var isNeedToValidate: Boolean? = null,
            var isMorality: Boolean? = null,
            var disableThefield: Boolean? = null,
            var minValue: Double? = null,
            var maxValue: Double? = null,
            var feedString: String? = null,
            @SerializedName("decimalCount")
            var decimalCount: String? = null

    ) : BaseModel()

    @Entity
    data class FeedBackResponse(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("response")
            var response: Long? = null) : BaseModel()

    data class LanguageSaveResponse(
            @SerializedName("languageId")
            var languageId: Long? = null) : BaseModel()

    data class DispatchHistory(
            @SerializedName("response")
            var response: String? = null,
            @SerializedName("message")
            var message: String? = null
    ) : BaseModel()

    data class DispatchArrivalDetails(
            @SerializedName("response")
            var response: String? = null,
            @SerializedName("message")
            var message: String? = null
    ) : BaseModel()


    data class RepositoryStatusResponse(

            @SerializedName("response")
            var repositoryList: Long? = null

    ) : BaseModel()

    data class RepositoryStatus(
            @SerializedName("response")
            var repositoryStatusId: Boolean? = null

    ) : BaseModel()

    @Entity
    data class PrograssBarResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(ProgressConventer::class)
            @SerializedName("response")
            var progressStatus: PrograssBar? = null
    ) : BaseModel()

    @Entity
    data class PrograssBar(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("pendingProgress")
            var pendingProgress: Double? = null,
            @SerializedName("completedProgress")
            var completedProgress: Double? = null
    ) : BaseModel()


    data class ActivityTextResponse(
            @SerializedName("response")
            var repositoryStatusId: Long? = null
    )

    @Entity
    data class RepositoryListResponse(
            @PrimaryKey
            var pId: Long = 1,
            @TypeConverters(RepositoryTypeConvertor::class)
            @SerializedName("response")
            var repositoryList: ArrayList<Repository>? = null
    ) : BaseModel()

    @Entity
    data class RepositoryDetailResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @SerializedName("response")
            var repositoryList: Repository? = null
    ) : BaseModel()

    @Entity
    data class SupportListResponse(
            @PrimaryKey
            var id: Long? = 1,
            @TypeConverters(SupportTypeConvertor::class)
            @SerializedName("response")
            var supportList: ArrayList<Support>? = null
    ) : BaseModel()

    @Entity
    data class Support(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("supportId")
            var supportId: Long? = null,
            @SerializedName("supportCategoryId")
            var supportCategoryId: Long? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("supportImageUrl")
            var supportImageUrl: String? = null) : BaseModel()

    @Entity
    data class SupportQuestionListResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(SupportQuestionTypeConventer::class)
            @SerializedName("response")
            var supportQuestionList: ArrayList<SupportQuestion>? = null
    ) : BaseModel()

    @Entity
    data class SupportQuestion(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("supportQuestionId")
            var supportQuestionId: Long? = null,
            @SerializedName("question")
            var question: String? = null,
            @SerializedName("response")
            var response: Boolean? = false) : BaseModel()

    data class SupportQuestionSubmitListResponse(
            @SerializedName("response")
            var message: String? = null
    ) : BaseModel()

    data class SupportQuestionSubmit(
            @SerializedName("UserId")
            var UserId: Long? = null,
            @SerializedName("QuestionResponses")
            var QuestionResponses: ArrayList<SupportQuestionSubmitList>? = null) : BaseModel()

    data class SupportQuestionSubmitList(
            @SerializedName("QuestionId")
            var questionId: Long? = null,
            @SerializedName("Response")
            var response: Boolean? = false) : BaseModel()

    @Entity
    data class RepositoryCategories(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("repositoryCategoryId")
            var repositoryCategoryId: Long? = null,
            @SerializedName("mediaPath")
            var mediaPath: String? = null,
            @SerializedName("name")
            var name: String? = null) : BaseModel()

    @Entity
    data class RepositoryCategoriesList(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(RepositoryCategoriesTypeConvertor::class)
            @SerializedName("response")
            var repositoryCategoriesList: ArrayList<RepositoryCategories>? = null
    ) : BaseModel()

    @Entity
    data class NotificaitonListResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(StatusNotificationConventer::class)
            @SerializedName("response")
            var notificationList: StatusNotificaiton? = null
    ) : BaseModel()

    @Entity
    data class StatusNotificaiton(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(NotificationTypeConvertor::class)
            @SerializedName("read")
            var notificationRead: ArrayList<Notificaiton>? = null,
            @TypeConverters(NotificationTypeConvertor::class)
            @SerializedName("unRead")
            var notificationUnRead: ArrayList<Notificaiton>? = null

    ) : BaseModel()


    data class LanguageResponse(
            @SerializedName("languageId")
            var languageId: Long? = null,
            @SerializedName("language")
            var language: String? = null


    ) : BaseModel()

    @Entity
    data class Notificaiton(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("notificationId")
            var notificationId: Long? = null,
            @SerializedName("notificationTypeId")
            var notificationTypeId: Int? = null,
            @SerializedName("message")
            var message: String? = null,
            @SerializedName("title")
            var title: String? = null,
            @SerializedName("referenceId")
            var id: Long? = null,
            @SerializedName("notificationDate")
            var notificationDate: String? = null,
            @SerializedName("status")
            var status: Boolean? = null,
            @SerializedName("icon")
            var icon: String? = null,
            @SerializedName("pushTokenKeys")
            var pushTokenKeys: String? = null
    ) : BaseModel()

    data class DownloadQueue(
            var title: String? = null,
            var activityId: Long? = null,
            var downloadId: Long? = null,
            var downloadProgress: Double? = 0.0,
            var isDownloaded: Boolean? = false,
            var downloadDate: String? = null,
            var isEncryptedMedia: Boolean? = null,
            var url: String? = null,
            var filePath: String? = null,
            var mediaType: MediaType? = null,
            var moduleType: ModuleType? = null,
            var downloadStatus: Boolean? = false
    ) : BaseModel()


    @Entity
    data class Download(
            @PrimaryKey
            var downloadId: Long? = null,
            var title: String? = null,
            var url: String? = null,
            var fileName: String? = null,
            var downloadDate: String? = null,
            var filePath: String? = null,
            @TypeConverters(EnumMediaTypeConvertor::class)
            var mediaType: MediaType? = null,
            @TypeConverters(EnumModuleTypeConvertor::class)
            var moduleType: ModuleType? = null,
            var isDownloaded: Boolean? = null,
            var isEncryptedMedia: Boolean? = null
    ) : BaseModel()

    data class SupportTicketResultResponse(
            @SerializedName("response")
            var response: Model.SupportTicketsMediaResponse? = null
    ) : Model.BaseModel()


    data class FeedbackResultResponse(
            @SerializedName("response")
            var response: Model.SupportTicketsResponse? = null
    ) : Model.BaseModel()

    @Entity
    data class OfflineFeedBackMediaList(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            var mediaType: Int? = null,
            var mediaPath: String? = null,
            var feedBackId: Int? = null
    )

    @Entity
    data class OfflineSupportMediaList(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            var mediaType: Int? = null,
            var mediaPath: String? = null,
            var supportId: Int? = null
    )

    @Entity
    data class OfflineUpdateSupportMediaList(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            var mediaType: Int? = null,
            var mediaPath: String? = null,
            var supportId: Int? = null
    )


    @Entity
    data class SupportTicketsMediaResponse(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("mediaType")
            var mediaType: Int? = null,
            @SerializedName("mediaLocation")
            var mediaLocation: String? = null,
            var supportId: Long? = 1
    ) : Model.BaseModel()

    @Entity
    data class SupportTicketsResponse(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("mediaType")
            var mediaType: Int? = null,
            @SerializedName("mediaLocation")
            var mediaLocation: String? = null,
            var feedBackId: Long? = 1
    ) : Model.BaseModel()

    @Entity
    data class SupportCreateTicket(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("UserId")
            var userId: Long? = null,
            @SerializedName("BatchId")
            var batchId: Long? = null,
            @SerializedName("CustomerSupportSubCategoryId")
            var customerSupportSubCategoryId: Long? = null,
            @SerializedName("Questions")
            var questions: ArrayList<Long>? = null,
            @SerializedName("Comment")
            var comment: String? = null,
            @TypeConverters(SupportMediaListTypeConventer::class)
            @SerializedName("Medias")
            var medias: ArrayList<SupportTicketsMediaResponse>? = null,
            var supportId: Int? = null
    ) : Model.BaseModel()

    @Entity
    data class SupportUpdate(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("UserId")
            var userId: Long? = null,
            @SerializedName("SupportTicketId")
            var supportTicketId: Long? = null,
            @SerializedName("Comments")
            var comment: String? = null,
            @TypeConverters(SupportMediaListTypeConventer::class)
            @SerializedName("ProofMedias")
            var medias: ArrayList<SupportTicketsMediaResponse>? = null,
            var supportOfflineId: Int? = null
    ) : Model.BaseModel()

    @Entity
    data class SupportDetailListResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(RaisedRequestListConventer::class)
            @SerializedName("response")
            var response: ArrayList<SuppourtDetailList>? = null

    ) : BaseModel()

    @Entity
    data class SuppourtDetailList(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("supportTicketId")
            var supportTicketId: Long? = null,
            @SerializedName("supportCategoryId")
            var supportCategoryId: Int? = null,
            @SerializedName("supportCategory")
            var supportCategory: String? = null,
            @SerializedName("icon")
            var icon: String? = null,
            @SerializedName("supportSubCategory")
            var supportSubCategory: String? = null,
            @SerializedName("farmerName")
            var farmerName: String? = null,
            @SerializedName("assignedUserId")
            var assignedUserId: Long? = null,
            @SerializedName("assignedUser")
            var assignedUser: String? = null,
            @SerializedName("createdDate")
            var createdDate: String? = null,
            @SerializedName("ticketStatus")
            var ticketStatus: String? = null,
            @SerializedName("status")
            var status: String? = null,
            @SerializedName("ticketStatusId")
            var ticketStatusId: Int? = null,
            @SerializedName("isNewResponse")
            var isNewResponse: Boolean? = null


    ) : BaseModel()

    data class SupportCreateTicketResponse(
            @SerializedName("response")
            var ticketId: Long? = null

    ) : BaseModel()

    @Entity
    data class SupportTicketDetails(
            @PrimaryKey
            @SerializedName("supportTicketId")
            var supportTicketId: Long? = null,
            @SerializedName("supportCategory")
            var supportCategory: String? = null,
            @SerializedName("ticketDate")
            var ticketDate: String? = null,
            @SerializedName("ticketCloseDate")
            var ticketCloseDate: String? = null,
            @SerializedName("supportSubCategory")
            var supportSubCategory: String? = null,
            @SerializedName("solution")
            var solution: String? = null,
            @SerializedName("icon")
            var icon: String? = null,
            @SerializedName("farmerId")
            var farmerId: Long? = null,
            @SerializedName("farmerName")
            var farmerName: String? = null,
            @TypeConverters(QuestionListTypeConvertor::class)
            @SerializedName("farmerQuestions")
            var farmerQuestions: ArrayList<String>? = null,
            @SerializedName("farmerComment")
            var farmerComment: String? = null,
            @TypeConverters(MediaListTypeConventer::class)
            @SerializedName("proofMedias")
            var proofMedias: ArrayList<SupportTicketsResponse>? = null,
            @SerializedName("commentsList")

            var commentsList: ArrayList<SupportQueries>? = null,
            @SerializedName("supportCategoryId")
            var supportCategoryId: Long? = null,
            @SerializedName("ticketStatus")
            var ticketStatus: String? = null,
            @SerializedName("ticketStatusId")
            var ticketStatusId: Int? = null
    ) : BaseModel()

    @Entity
    data class SupportIssueDetailsResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @TypeConverters(SupportTicketDetailsConventer::class)
            @SerializedName("response")
            var response: SupportTicketDetails? = null
    )


    data class SupportIssueListDetailsResponse(
            @SerializedName("response")
            var response: SupportTicketDetails? = null
    )

    @Entity
    data class FeedBackSaveDetails(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @SerializedName("UserId")
            var userId: Long? = null,
            @SerializedName("Chick_Bird")
            var chick_Bird: Int? = null,
            @TypeConverters(FeedBackQuestionListConventer::class)
            @SerializedName("Chick_Bird_Questions")
            var chick_Bird_Questions: ArrayList<Long>? = null,
            @SerializedName("Feed")
            var feed: Int? = null,
            @TypeConverters(FeedBackQuestionListConventer::class)
            @SerializedName("Feed_Questions")
            var feed_Questions: ArrayList<Long>? = null,
            @SerializedName("Medicine")
            var medicine: Int? = null,
            @TypeConverters(FeedBackQuestionListConventer::class)
            @SerializedName("Medicine_Questions")
            var medicine_Questions: ArrayList<Long>? = null,
            @SerializedName("SupportCenter")
            var supportCenter: Int? = null,
            @TypeConverters(FeedBackQuestionListConventer::class)
            @SerializedName("SupportCenter_Questions")
            var supportCenter_Questions: ArrayList<Long>? = null,
            @SerializedName("Reason")
            var reason: String? = null,
            @TypeConverters(MediaListTypeConventer::class)
            @SerializedName("Medias")
            var medias: ArrayList<Model.SupportTicketsResponse>? = null,
            var isSelected: Boolean? = false,
            var feedBackId: Int? = null

    ) : BaseModel()

    @Entity
    data class FeedBackQuestionListResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(FeedbackRatingListTypeConventer::class)
            @SerializedName("response")
            var response: ArrayList<Model.FeedBackQuestionList>? = null
    )

    @Entity
    data class FeedBackQuestionList(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("rating")
            var rating: Int? = null,
            @SerializedName("feedBackCategoryId")
            var feedBackCategoryId: Long? = null,
            @SerializedName("categoryName")
            var categoryName: String? = null,
            @TypeConverters(StarDetailsListTypeConventer::class)
            @SerializedName("stars")
            var stars: ArrayList<Model.StarDetails>? = null
    ) : BaseModel()

    @Entity
    data class StarDetails(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("star")
            var star: Int? = null,
            @TypeConverters(QuestionListDetailsTypeConventer::class)
            @SerializedName("questions")
            var questions: ArrayList<Model.QuestionListDetails>? = null

    ) : BaseModel()

    data class QuestionListDetails(
            @SerializedName("questionId")
            var questionId: Long? = null,
            @SerializedName("question")
            var question: String? = null,
            var isSelected: Boolean? = false
    ) : BaseModel()

    @Entity
    data class DailyReportHistoryResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(DailyReportDateHistoryTypeConventer::class)
            @SerializedName("response")
            var response: ArrayList<Model.DailyReportDateHistory>? = null) : BaseModel()

    @Entity
    data class DailyReportDateHistory(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = 1,
            @SerializedName("reportDate")
            var reportDate: String? = null,
            @TypeConverters(DailyReportTypeConventer::class)
            @SerializedName("reports")
            var reports: Model.DailyReportHistory? = null,
            @SerializedName("count")
            var count: Int? = null,
            @SerializedName("lastUpdatedDate")
            var lastUpdatedDate: String? = null
    ) : BaseModel()

    @Entity
    data class DailyReportHistory(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @SerializedName("dailySupportId")
            var dailySupportId: Long? = null,
            @SerializedName("feedStock")
            var feedStock: String? = null,
            @SerializedName("receipt")
            var receipt: Double? = null,
            @SerializedName("feedConsumption")
            var feedConsumption: String? = null,
            @SerializedName("feedTransferIn")
            var feedTransferIn: Double? = null,
            @SerializedName("feedTransferOut")
            var feedTransferOut: Double? = null,
            @SerializedName("mortality")
            var mortality: Int? = null,
            @SerializedName("bodyWeight")
            var bodyWeight: Double? = null,
            @SerializedName("reason")
            var reason: String? = null,
            @SerializedName("farmCode")
            var farmCode: String? = null,
            @SerializedName("farmerName")
            var farmerName: String? = null,
            @SerializedName("batchNo")
            var batchNo: String? = null,
            @SerializedName("feedConsumptionReason")
            var feedConsumptionReason: String? = null,
            @SerializedName("createdDate")
            var createdDate: String? = null,
            @SerializedName("isActive")
            var isActive: Boolean? = null
    ) : BaseModel()

    @Entity
    data class FeedbackHistoryResponse(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            @TypeConverters(FeedBackHistoryTypeConventer::class)
            @SerializedName("response")
            var response: ArrayList<Model.FeedBackRatingHistory>? = null) : BaseModel()

    @Entity
    data class FeedBackRatingHistory(
            @PrimaryKey
            @SerializedName("feedbackRequestId")
            var feedbackRequestId: Long? = null,
            @SerializedName("chick_Bird")
            var chick_Bird: Int? = null,
            @TypeConverters(QuestionListTypeConvertor::class)
            @SerializedName("chick_Bird_Questions")
            var chick_Bird_Questions: ArrayList<String>? = null,
            @SerializedName("feed")
            var feed: Int? = null,
            @TypeConverters(QuestionListTypeConvertor::class)
            @SerializedName("feed_Questions")
            var feed_Questions: ArrayList<String>? = null,
            @SerializedName("medicine")
            var medicine: Int? = null,
            @TypeConverters(QuestionListTypeConvertor::class)
            @SerializedName("medicine_Questions")
            var medicine_Questions: ArrayList<String>? = null,
            @SerializedName("supportCenter")
            var supportCenter: Int? = null,
            @TypeConverters(QuestionListTypeConvertor::class)
            @SerializedName("supportCenter_Questions")
            var supportCenter_Questions: ArrayList<String>? = null,
            @SerializedName("reason")
            var reason: String? = null,
            @SerializedName("farmCode")
            var farmCode: String? = null,
            @SerializedName("farmerName")
            var farmerName: String? = null,
            @SerializedName("batchNo")
            var batchNo: String? = null,
            @SerializedName("reportedDate")
            var reportedDate: String? = null,
            @SerializedName("isActive")
            var isActive: Boolean? = null,
            @TypeConverters(MediaListTypeConventer::class)
            @SerializedName("proofs")
            var proofs: ArrayList<Model.SupportTicketsResponse>? = null
    ) : BaseModel()

    data class FeedBackMediaList(

            @SerializedName("mediaType")
            var mediaType: Int? = null,
            @SerializedName("mediaLocation")
            var mediaLocation: String? = null
    ) : Model.BaseModel()

    @Entity
    data class OfflineMediaActivity(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            var activityId: Long? = null,
            var userId: Long? = null,
            var mediaTypeId: Int? = null,
            var latitude: String? = null,
            var longitude: String? = null,
            var mediaPath: String? = null,
            var url: String? = null,
            var mediaType: MediaType? = null

    ) : BaseModel()


    @Entity
    data class SaveActivityDetails(
            @PrimaryKey(autoGenerate = true)
            var id: Long? = null,
            var activityId: Long? = null,
            var userId: Long? = null,
            var textAnswer: String? = null,
            var comment: String? = null
    ) : BaseModel()

    @Entity
    data class MaterialArrivalList(
            @PrimaryKey
            var id: Long? = 0,
            @TypeConverters(MaterialArrivalPendingTypeConventer::class)
            @SerializedName("todaysArrivals")
            var todaysArrivals: ArrayList<MaterialArrivalPending>? = null,
            @SerializedName("nextExpectedFeedArrival")
            var nextExpectedFeedArrival: String? = null,
            @SerializedName("isActiveBatch")
            var isActiveBatch: Boolean? = false,
            @SerializedName("nextExpectedChickArrivalFrom")
            var nextExpectedChickArrivalFrom: String? = null,
            @SerializedName("nextExpectedChickArrivalTo")
            var nextExpectedChickArrivalTo: String? = null

    ) : BaseModel()

    @Entity
    data class MaterialArrivalResponse(
            @PrimaryKey
            var id: Long? = 0,
            @TypeConverters(MaterialArrivalTypeConventer::class)
            @SerializedName("response")
            var response: MaterialArrivalList? = null

    ) : BaseModel()

    @Entity
    data class MaterialArrivalPending(
            @PrimaryKey
            var id: Long = 1,
            @SerializedName("key")
            var key: String? = null,
            @TypeConverters(MaterialArrivalTypeConventer::class)
            @SerializedName("value")
            var value: ArrayList<MaterialArrival>? = null
    )


    @Entity
    data class MaterialArrival(
            @PrimaryKey
            @SerializedName("materialArrivalId")
            var materialArrivalId: Long? = null,
            @SerializedName("vechicleNumber")
            var vehicleNumber: String? = null,
            @SerializedName("expectedDateAndTime")
            var arrivalDate: String? = null,
            @SerializedName("expectedTime")
            var expectedTime: String? = null,
            @SerializedName("uom")
            var UOM: String? = null,
            @SerializedName("arrivalStatus")
            var arrivalStatus: String? = null,
            @SerializedName("materialName")
            var materialName: String? = null,
            @SerializedName("arrivalQuantity")
            var arrivalQuantity: Long? = null,
            @SerializedName("dispatchedQuantity")
            var dispatchedQuantity: Long? = null,
            @SerializedName("feedItemCount")
            var feedItemCount: Int? = null,
            @SerializedName("feedQuantity")
            var feedQuantity: Long? = null,
            @SerializedName("chicksCount")
            var chicksCount: Long? = null,
            @SerializedName("item")
            var item: String? = null,
            @SerializedName("driverMobileNo")
            var phoneNumber: String? = null

    ) : BaseModel()


    data class MaterialArrivalAdapter(
            @SerializedName("materialArrivalId")
            var materialArrivalId: Long? = null,
            @SerializedName("vechicleNumber")
            var vehicleNumber: String? = null,
            @SerializedName("expectedDateAndTime")
            var arrivalDate: String? = null,
            @SerializedName("expectedTime")
            var expectedTime: String? = null,
            @SerializedName("UOM")
            var UOM: String? = null,
            @SerializedName("arrivalStatus")
            var arrivalStatus: String? = null,
            @SerializedName("materialName")
            var materialName: String? = null,
            @SerializedName("arrivalQuantity")
            var arrivalQuantity: MutableLiveData<Long>? = null,
            @SerializedName("dispatchedQuantity")
            var dispatchedQuantity: Long? = null,
            @SerializedName("driverMobileNo")
            var phoneNumber: String? = null,
            @SerializedName("item")
            var item: String? = null,
            var comments: MutableLiveData<String>? = null
    ) : BaseModel() {
        fun getArrivalQty(): LiveData<Long>? {
            return arrivalQuantity
        }

        fun getComments(): LiveData<String>? {
            return comments
        }
    }


    data class SingleChoice(
            var option: String? = null,
            var isSelected: Boolean? = null
    )

    @Entity
    data class SupportQueries(
            @PrimaryKey(autoGenerate = true)
            @SerializedName("id")
            var supportId: Long? = null,
            @SerializedName("commentPostBy")
            var commentPostBy: String? = null,
            @SerializedName("comments")
            var comment: String? = null,
            @SerializedName("commentedDate")
            var commentedDate: String? = null,
            @SerializedName("isFarmer")
            var isFarmer: Boolean? = null,
            @TypeConverters(MediaTypeConvertor::class)
            @SerializedName("proofMedias")
            var mediaContent: ArrayList<SupportTicketsResponse>? = null) : BaseModel()

    /* postJson.addProperty("MaterialArrivalId", materialArrivalId)
     postJson.addProperty("ArrivalQuantity", arrivalQuantity)
     postJson.addProperty("Comments", comments)*/
    @Entity
    data class DispatchArrivalDetailsList(
            @PrimaryKey
            @SerializedName("MaterialArrivalId")
            var materialArrivalId: Long? = null,
            @SerializedName("ArrivalQuantity")
            var arrivalQuantity: Long? = null,
            @SerializedName("Comments")
            var comments: String? = null
    ) : BaseModel()


    @Entity
    data class HistoryMaterialArrivalResponse(
            @PrimaryKey
            var id: Long? = null,
            @TypeConverters(HistoryMaterialArrivalListConventer::class)
            @SerializedName("response")
            var response: ArrayList<HistoryMaterialArrival>? = null

    ) : BaseModel()

    @Entity
    data class HistoryMaterialArrival(
            @PrimaryKey
            @SerializedName("materialArrivalId")
            var materialArrivalId: Long? = null,
            @SerializedName("vehicleNumber")
            var vehicleNumber: String? = null,
            @SerializedName("expectedDate")
            var arrivalDate: String? = null,
            @SerializedName("expectedTime")
            var expectedTime: String? = null,
            @SerializedName("uom")
            var UOM: String? = null,
            @SerializedName("arrivalStatus")
            var arrivalStatus: String? = null,
            @SerializedName("materialName")
            var materialName: String? = null,
            @SerializedName("arrivalQuantity")
            var arrivalQuantity: Long? = null,
            @SerializedName("dispatchedQuantity")
            var dispatchedQuantity: Long? = null,
            @SerializedName("feedItemCount")
            var feedItemCount: String? = null,
            @SerializedName("feedQuantity")
            var feedQuantity: String? = null,
            @SerializedName("chicksCount")
            var chicksCount: String? = null

    ) : BaseModel()


    @Entity
    data class HistoryMaterialArrivalDetailResponse(
            @PrimaryKey
            var id: Long? = null,
            @TypeConverters(HistoryDetailMaterialArrivalListConventer::class)
            @SerializedName("response")
            var response: ArrayList<HistoryMaterialArrivalDetail>? = null

    ) : BaseModel()

    @Entity
    data class HistoryMaterialArrivalDetail(
            @PrimaryKey
            @SerializedName("materialArrivalId")
            var materialArrivalId: Long? = null,
            @SerializedName("vechicleNumber")
            var vehicleNumber: String? = null,
            @SerializedName("expectedDateAndTime")
            var arrivalDate: String? = null,
            @SerializedName("expectedTime")
            var expectedTime: String? = null,
            @SerializedName("uom")
            var UOM: String? = null,
            @SerializedName("arrivalStatus")
            var arrivalStatus: String? = null,
            @SerializedName("materialName")
            var materialName: String? = null,
            @SerializedName("arrivalQuantity")
            var arrivalQuantity: Long? = null,
            @SerializedName("dispatchedQuantity")
            var dispatchedQuantity: Long? = null,
            @SerializedName("feedItemCount")
            var feedItemCount: String? = null,
            @SerializedName("feedQuantity")
            var feedQuantity: String? = null,
            @SerializedName("chicksCount")
            var chicksCount: String? = null,
            @SerializedName("comments")
            var comments: String? = null,
            @SerializedName("item")
            var itemName: String? = null,
            @SerializedName("driverMobileNo")
            var driverMobileNo: String? = null

    ) : BaseModel()

    @Entity
    data class PerformanceReportResponse(
            @PrimaryKey
            var id: Long? = null,
            @TypeConverters(PerformanceReportConventer::class)
            @SerializedName("response")
            var response: ArrayList<PerformanceReport>? = null

    ) : BaseModel()


    data class MortalityChartResponse(
            @SerializedName("response")
            var response: ArrayList<MortalityChart>? = null
    ) : BaseModel()

    data class MortalityChart(
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("mortality")
            var mortality: Long? = null
    ) : BaseModel()

    data class FeedIntakeChartResponse(
            @SerializedName("response")
            var response: ArrayList<FeedIntakeChart>? = null
    ) : BaseModel()

    data class FeedIntakeChart(
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("mortality")
            var mortality: Long? = null,
            @SerializedName("week")
            var week: Long? = null,
            @SerializedName("bodyWeight")
            var bodyWeight: Long? = null
    ) : BaseModel()

    @Entity
    data class ChartDetailList(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("data")
            var value: Double? = null,
            @SerializedName("rightAxies")
            var rightAxies: Long? = null
    ) : BaseModel()

    @Entity
    data class ChartBatchList(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("key")
            var key: String? = null,
            @SerializedName("hatchdate")
            var hatchdate: String? = null,
            @TypeConverters(ChartDetailListConventer::class)
            @SerializedName("value")
            var valueList: ArrayList<ChartDetailList>? = null,
            var combinedKey: String? = null,
            @TypeConverters(ChartDetailListConventer::class)
            var combinedValueList: ArrayList<ChartDetailList>? = null
    ) : BaseModel()


    @Entity
    data class ChartDetailList1(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("data")
            var value: Long? = null,
            @SerializedName("rightAxies")
            var rightAxies: Long? = null
    ) : BaseModel()


    data class DaygrainChartResponse(
            @SerializedName("response")
            var response: ArrayList<DaygrainChart>? = null
    ) : BaseModel()

    data class DaygrainChart(
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("dayGrain")
            var bodyWeight: Long? = null
    ) : BaseModel()

    @Entity
    data class PerformanceReport(
            @PrimaryKey
            @SerializedName("batchId")
            var batchId: Long? = null,
            @SerializedName("hatchDate")
            var hatchDate: String? = null,
            @SerializedName("chicksHoused")
            var chicksHoused: Long? = null,
            @SerializedName("liftedQuantityInKG")
            var liftedQuantityInKG: Double? = null,
            @SerializedName("averageWeight")
            var averageWeight: Double? = null,
            @SerializedName("meanAge")
            var meanAge: Long? = null,
            @SerializedName("fcr")
            var FCR: Double? = null,
            @SerializedName("dayGain")
            var dayGain: Long? = null,
            @SerializedName("mortalityPercent")
            var mortalityPercent: Double? = null,
            @SerializedName("feedIntake")
            var feedIntake: Double? = null

    ) : BaseModel()

    @Entity
    data class PerformanceChartResponse(
            @PrimaryKey
            var id: Long? = null,
            @TypeConverters(Chart1TypeConventer::class)
            @SerializedName("response")
            var response: Chart1? = null

    ) : BaseModel()

    @Entity
    data class Chart1(
            @PrimaryKey
            var id: Long? = null,
            @TypeConverters(PerformanceChartListConventer::class)
            @SerializedName("batchData")
            var batchData: ArrayList<PerformanceChart>? = null,
            @TypeConverters(CombinedChartListConventer::class)
            @SerializedName("chart")
            var chart: ArrayList<NewChart>? = null
    ) : BaseModel()


    data class KeyModel(
            var id: Long? = null,
            var key: String? = null
    ) {
        override fun toString(): String {
            return key.toString()
        }
    }

    @Entity
    data class NewChart(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("key")
            var key: String? = null,
            @SerializedName("value")
            var keyValue: Int? = null,
            var keyValue2: Int? = null,
            var fcrkey: String? = null,

            @TypeConverters(ChartDetailsListConventer::class)
            @SerializedName("list")
            var value: ArrayList<ChartBatchList>? = null,
            @TypeConverters(ChartDetailsListConventer::class)
            var combinedChart: ArrayList<ChartBatchList>? = null

    ) : BaseModel()


    @Entity
    data class ChartData(
            @PrimaryKey
            var id: Long? = null,
            @SerializedName("key")
            var key: String? = null,
            @TypeConverters(ChartDetailListConventer::class)
            @SerializedName("value")
            var value: ArrayList<ChartDetailList>? = null,
            var batchList: ArrayList<String>? = null,
            @TypeConverters(ChartDetailListConventer::class)
            @SerializedName("combinedValue")
            var combinedValue: ArrayList<ChartDetailList>? = null

    ) : BaseModel()


    data class Batch1(

            var batchId: Long? = 0,
            var listData: ArrayList<String>? = null
    )

    @Entity
    data class PerformanceChart(
            @PrimaryKey
            @SerializedName("batchId")
            var batchId: Long? = null,
            @TypeConverters(ChartDataListConventer::class)
            @SerializedName("chart")
            var chart: ArrayList<ChartData>? = null,
            @SerializedName("hatchDate")
            var hatchDate: String? = null,
            @SerializedName("farmCode")
            var farmCode: String? = null,
            @SerializedName("averageChicksHoused")
            var averageChicksHoused: Long? = null,
            @SerializedName("averageLiftedQuantityInKG")
            var averageLiftedQuantityInKG: Double? = null,
            @SerializedName("averageWeight")
            var averageWeight: Double? = null,
            @SerializedName("averageMeanAge")
            var averageMeanAge: Long? = null,
            @SerializedName("averageFCR")
            var averageFCR: Double? = null,
            @SerializedName("averageDayGain")
            var averageDayGain: Long? = null,
            @SerializedName("averageMortalityPercent")
            var averageMortalityPercent: Double? = null

    ) : BaseModel()

    //Weather model
    @Entity
    data class Weather(
            @PrimaryKey
            var drawableId: Int? = null,
            @SerializedName("icon")
            var icon: String? = null,
            @SerializedName("code")
            var code: Long? = null,
            @SerializedName("description")
            var description: String? = null
    ) : BaseModel()

    @Entity
    data class DailyWeatherData(
            @PrimaryKey
            val id: Long? = 1,
            @SerializedName("rh")
            var rh: Double? = null,
            @SerializedName("wind_spd")
            var wind_spd: Double? = null,
            @SerializedName("timestamp_utc")
            var timestamp_utc: String? = null,
            @SerializedName("vis")
            var vis: Double? = null,
            @SerializedName("slp")
            var slp: Double? = null,
            @SerializedName("pod")
            var pod: String? = null,
            @SerializedName("dni")
            var dni: Double? = null,
            @SerializedName("elev_angle")
            var elev_angle: Double? = null,
            @SerializedName("pres")
            var pres: Double? = null,
            @SerializedName("h_angle")
            var h_angle: Long? = null,
            @SerializedName("dewpt")
            var dewpt: Double? = null,
            @SerializedName("snow")
            var snow: Double? = null,
            @SerializedName("uv")
            var uv: Double? = null,
            @SerializedName("solar_rad")
            var solar_rad: Double? = null,
            @SerializedName("wind_dir")
            var wind_dir: Double? = null,
            @TypeConverters(WeatherConventer::class)
            @SerializedName("weather")
            var weather: Weather? = null,
            @SerializedName("ghi")
            var ghi: Double? = null,
            @SerializedName("dhi")
            var dhi: Double? = null,
            @SerializedName("timestamp_local")
            var timestamp_local: String? = null,
            @SerializedName("app_temp")
            var app_temp: Double? = null,
            @SerializedName("azimuth")
            var azimuth: Double? = null,
            @SerializedName("datetime")
            var datetime: String? = null,
            @SerializedName("temp")
            var temp: Double? = null,
            @SerializedName("precip")
            var precip: Double? = null,
            @SerializedName("clouds")
            var clouds: Long? = null,
            @SerializedName("ts")
            var ts: Long? = null,
            @SerializedName("pop")
            var pop: Double? = null,
            var unit: String? = null,
            var value: Double? = null
    ) : BaseModel()


    data class ChartInput(
            @SerializedName("FarmCode")
            var farmCode: String? = null,
            @SerializedName("BatchId")
            var batchId: ArrayList<Long>? = null
    )

    @Entity
    data class WeatherData(
            @PrimaryKey
            var id: Long? = 1,
            @SerializedName("timezone")
            var timezone: String? = null,
            @SerializedName("state_code")
            var state_code: String? = null,
            @SerializedName("country_code")
            var country_code: String? = null,
            @SerializedName("lat")
            var lat: Double? = null,
            @SerializedName("url")
            var url: String? = null,
            @SerializedName("lon")
            var lon: Double? = null,
            @SerializedName("postal_code")
            var postal_code: String? = null,
            @SerializedName("city_name")
            var city_name: String? = null,
            @SerializedName("station_id")
            var station_id: String? = null,
            @SerializedName("city_id")
            var city_id: String? = null,
            @TypeConverters(DailyWeatherDataListConventer::class)
            @SerializedName("data")
            var data: ArrayList<DailyWeatherData>? = null) : BaseModel()


    data class HttpResponse(
            @SerializedName("protocol")
            var protocol: String? = null,
            @SerializedName("code")
            var code: Long? = null,
            @SerializedName("message")
            var message: String? = null,
            @SerializedName("url")
            var url: String? = null
    ) : BaseModel()


    data class WeatherErrorResponse(
            @SerializedName("error")
            var error: String? = null
    ) : BaseModel()
//Resource string file api model

    data class ResourceStringResponse(
            @SerializedName("response")
            var response: ArrayList<Model.ResourceStringDetails>? = null
    ) : BaseModel()

    data class ResourceStringDetails(
            @SerializedName("id")
            var id: Double? = null,
            @SerializedName("key")
            var key: String? = null,
            @SerializedName("value")
            var value: String? = null
    ) : BaseModel()


    @Entity
    data class DailyReportReasonsResponse(
            @PrimaryKey
            var id: Long? = 0,
            @TypeConverters(ReasonsConventer::class)
            @SerializedName("response")
            var response: DailyReportReasons? = null
    ) : BaseModel()

    @Entity
    data class DailyReportReasons(
            @PrimaryKey
            var id: Long? = 0,
            @SerializedName("age")
            var age: Long? = null,
            @SerializedName("birdStock")
            var birdStock: String? = null,
            @SerializedName("feedConsumptionUnit")
            var feedConsumptionUnit: Long? = null,
            @SerializedName("feedStandard")
            var feedStandard: Long? = null,
            @SerializedName("limit")
            var limit: Double? = null,
            @TypeConverters(ReasonsConventer::class)
            @SerializedName("reasons")
            var reasons: ArrayList<Reasons>? = null,
            @TypeConverters(FeedBackDetailsConventer::class)
            @SerializedName("dailySupport")
            var dailySupport: FeedBackDetails? = null,
            @SerializedName("feedConsumptionMin")
            var feedConsumptionMin: Long? = null,
            @SerializedName("feedConsumptionMax")
            var feedConsumptionMax: Long? = null,
            @SerializedName("uomConversion")
            var uomConversion: Long? = null,
            @SerializedName("enableFeedConsumption")
            var enableFeedConsumption: Boolean? = null,
            var feedBags: Double? = null,
            var feedKg: Double? = null


    ) : BaseModel()




}


enum class MediaType {
    Video, Audio, Image, Pdf
}

enum class ModuleType {
    Activity, Repository
}