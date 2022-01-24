package com.gm.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gm.db.CustomTypeConvertors.*
import com.gm.db.DAO.*
import com.gm.models.Model


@Database(entities = arrayOf(Model.ActivityList::class, Model.Media::class, Model.RepositoryListResponse::class,
        Model.RepositoryCategoriesList::class, Model.SupportListResponse::class, Model.NotificaitonListResponse::class
        , Model.RepositoryDetailResponse::class, Model.DownLoadInput::class, Model.PrograssBarResponse::class, Model.SupportResponse::class
        , Model.SupportDetailListResponse::class, Model.SupportIssueDetailsResponse::class, Model.FeedBackSubmitRequest::class, Model.FeedBackDetailsResponses::class
        , Model.OfflineFeedBackMediaList::class, Model.OfflineSupportMediaList::class, Model.OfflineMediaActivity::class, Model.SaveActivityDetails::class,
        Model.FeedBackSaveDetails::class, Model.FeedBackQuestionListResponse::class, Model.Download::class, Model.SupportCreateTicket::class
        , Model.FeedbackHistoryResponse::class, Model.DailyReportHistoryResponse::class, Model.SupportQuestionListResponse::class, Model.SupportTicketDetails::class
        , Model.OfflineUpdateSupportMediaList::class,Model.SupportUpdate::class,Model.MaterialArrivalResponse::class,Model.ActivityResponse::class,
        Model.DispatchArrivalDetailsList::class,Model.HistoryMaterialArrivalDetailResponse::class,Model.DailyReportDetailsResponse::class
        ,Model.HistoryMaterialArrivalResponse::class,Model.PerformanceChartResponse::class,Model.PerformanceReportResponse::class,Model.DailyReportReasonsResponse::class,Model.WeatherData::class), version = 6)


@TypeConverters(MediaTypeConvertor::class, EnumMediaTypeConvertor::class, RepositoryTypeConvertor::class,
        RepositoryCategoriesTypeConvertor::class, SupportTypeConvertor::class, NotificationTypeConvertor::class, StatusNotificationConventer::class, ProgressConventer::class,
        RepositoryTypeConventer::class, SupportDetailsConventer::class, SupportDetailsListConventer::class, FeedBackHistoryTypeConventer::class,
        RaisedRequestListConventer::class, QuestionListTypeConvertor::class, MediaListTypeConventer::class, SupportTicketDetailsConventer::class, EnumModuleTypeConvertor::class
        , FeedBackDetailsConventer::class, FeedBackQuestionListConventer::class, FeedBackMediaTypeConventer::class, FeedbackRatingListTypeConventer::class, SupportQuestionTypeConventer::class,MaterialArrivalPendingTypeConventer::class,
        StarDetailsListTypeConventer::class, SupportMediaListTypeConventer::class, DailyReportDateHistoryTypeConventer::class, DailyReportTypeConventer::class
        , SupportQueriesTypeConverter::class,MaterialArrivalTypeConventer::class,MaterialArrivalListConventer::class,HistoryDetailMaterialArrivalListConventer::class,CombinedChartListConventer::class,ChartDetailsListConventer::class,
        HistoryMaterialArrivalListConventer::class,PerformanceChartListConventer::class, ChartDataListConventer::class,ActivityListConventer::class,DailyReportSupportConventer::class,ReasonConventer::class,
        ChartDetailListConventer::class,PerformanceReportConventer::class,Chart1TypeConventer::class,ReasonsConventer::class,DailyWeatherDataListConventer::class,WeatherConventer::class,
        DailyReportDetailsConventer::class
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun getActivityListDao(): ActivityListDao
    abstract fun getRepositoryListDao(): RepositoryListDao
    abstract fun getRepositoryCategoriesDao(): RepositoryCategoriesDao
    abstract fun getSupportDao(): SupportDao
    abstract fun getNotificationDao(): NotificationDao
    abstract fun getRepositoryDetailDao(): RepositoryDetailDao
    abstract fun getDownLoadedVideoDao(): DownLoadedVideoDao
    abstract fun getProgressDao(): ProgressBarDao
    abstract fun getSupportDetailsDao(): SupportDetailsDao
    abstract fun getRaisedRequestDao(): RaisedRequestDao
    abstract fun getRaisedRequestDetailsDao(): RaisedRequestDetailsDao
    abstract fun getSupportListDetailDao(): SupportListDetailDao
    abstract fun getManageDownloadDao(): ManageDownloadDao
    abstract fun addDailyReportDao(): DailyReportDao
    abstract fun getFeedBackDetailsDao(): FeedBackDetailsDao
    abstract fun addFeedBackMediaDao(): FeedBackMediaDao
    abstract fun addSupportMediaDao(): SupportMediaDao
    abstract fun addFeedBackSaveDetailsDao(): FeedBackSaveDetailsDao
    abstract fun getFeedBackRatingDetailsDao(): FeedBackRatingDetailsDao
    abstract fun addSupportTicketDao(): SupportTicketSaveDetailsDao
    abstract fun updateSupportTicketDao(): UpdateSupportTicketSaveDetailsDao
    abstract fun addActivityDetailsDao(): ActivitySaveDetailsDao
    abstract fun uploadActivityMediaDao(): ActivityMediaDao
    abstract fun getDailyReportHistoryDao(): DailyReportHistoryDao
    abstract fun getFeedbackHistoryDao(): FeedbackHistoryDao
    abstract fun getSupportQuestionDao(): SupportQuestionDao
    abstract fun addCommentMediaDao(): CommentMediaDao
    abstract fun addCommentDao(): AddCommentDao
    abstract fun getMaterialArrivalDao():DispatchArrivalDao
    abstract fun submitMaterialArrivalDao():UpdateDispatchArrivalDao
    abstract fun getHistoryArrivalDao():DispatchArrivalHistoryDao
    abstract fun getHistoryDetailsDao():DispatchArrivalHistoryDetailsDao
    abstract fun getPerformanceChartDao():PerformanceChartDao
    abstract fun getPerformanceReportDao():PerformanceReportDao
    abstract fun getActivityDetailsByIdDao():ActivityDetailsByIdDao
    abstract fun getReasonListDao():ReasonListDao
    abstract fun getWeatherListDao():WeatherListDao

}