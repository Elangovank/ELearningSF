package com.gm.db

import androidx.room.*
import com.gm.models.Model

class DAO {
    companion object {
        var TRUE = 1
        var FALSE = 0
    }

    @Dao
    interface ActivityListDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: List<Model.ActivityList>)

        @Query("SELECT * FROM ActivityList WHERE isPending=:isPending")
        fun getListByStatus(isPending: Int): List<Model.ActivityList>

        @Query("SELECT * FROM ActivityList WHERE activityId=:activityId")
        fun getItemById(activityId: Int): Model.ActivityList

        @Delete
        fun deleteItems(item: List<Model.ActivityList>)

        @Query("DELETE FROM ActivityList")
        fun deleteAll()

        @Query("SELECT * FROM ActivityList")
        fun getall(): List<Model.ActivityList>

    }


    @Dao
    interface ManageDownloadDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: List<Model.Download>)

        @Update
        fun update(item: Model.Download)

        @Query("SELECT * FROM Download WHERE downloadId=:downloadId")
        fun getItemByDownloadId(downloadId: Long): List<Model.Download>

        @Query("SELECT * FROM Download WHERE url=:mediaUrl")
        fun getItemByUrl(mediaUrl: String): Model.Download

        @Query("SELECT * FROM Download WHERE isDownloaded=:status")
        fun getPendingDownloadList(status: Int = FALSE): List<Model.Download>

        @Query("SELECT * FROM Download WHERE isDownloaded=:status")
        fun getCompletedList(status: Int = TRUE): List<Model.Download>

        @Delete
        fun deleteItems(item: List<Model.Download>)

        @Query("DELETE FROM Download")
        fun deleteAll()

        @Query("SELECT * FROM Download")
        fun getAll(): List<Model.Download>

    }


    @Dao
    interface RepositoryListDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.RepositoryListResponse)

        @Update
        fun update(item: Model.RepositoryListResponse)

        @Delete
        fun deleteItems(item: Model.RepositoryListResponse)

        @Query("DELETE FROM RepositoryListResponse")
        fun deleteAll()

        @Query("DELETE FROM RepositoryListResponse Where pId=:repositoryCategoryId")
        fun deleteById(repositoryCategoryId: Long)


        @Query("SELECT *  FROM RepositoryListResponse Where pId=:repositoryCategoryId")
        fun getById(repositoryCategoryId: Long): Model.RepositoryListResponse

        @Query("SELECT * FROM RepositoryListResponse")
        fun getall(): List<Model.RepositoryListResponse>
    }


    @Dao
    interface RepositoryCategoriesDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.RepositoryCategoriesList)

        @Query("DELETE FROM RepositoryCategoriesList")
        fun deleteAll()

        @Query("SELECT * FROM RepositoryCategoriesList")
        fun getall(): Model.RepositoryCategoriesList
    }


    @Dao
    interface SupportDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportListResponse)

        @Update
        fun update(item: Model.SupportListResponse)

        @Query("DELETE FROM SupportListResponse")
        fun deleteAll()

        @Query("DELETE FROM SupportListResponse Where id=:supportCategoryId")
        fun deleteById(supportCategoryId: Long)

        @Query("SELECT * FROM SupportListResponse Where id=:supportCategoryId")
        fun getById(supportCategoryId: Long): Model.SupportListResponse
    }


    @Dao
    interface NotificationDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.NotificaitonListResponse)

        @Query("DELETE FROM NotificaitonListResponse")
        fun deleteAll()

        @Query("SELECT * FROM NotificaitonListResponse")
        fun getall(): Model.NotificaitonListResponse


    }

    @Dao
    interface RepositoryDetailDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.RepositoryDetailResponse)

        @Query("DELETE FROM RepositoryDetailResponse")
        fun deleteAll()

        @Query("SELECT * FROM RepositoryDetailResponse")
        fun getall(): Model.RepositoryDetailResponse


    }

    @Dao
    interface DownLoadedVideoDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.DownLoadInput)

        @Query("DELETE FROM DownLoadInput")
        fun deleteAll()

        @Query("SELECT * FROM DownLoadInput")
        fun getAll(): Model.DownLoadInput

        @Query("SELECT * FROM DownLoadInput  WHERE activityId=:activityId AND videoId=:videoId")
        fun getVideoPath(activityId: Long, videoId: Long): Model.DownLoadInput

        @Query("DELETE FROM DownLoadInput  WHERE activityId=:activityId AND videoId=:videoId")
        fun deleteVideoPath(activityId: Long, videoId: Long)
    }

    @Dao
    interface ProgressBarDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.PrograssBarResponse)

        @Query("DELETE FROM PrograssBarResponse")
        fun deleteAll()

        @Query("SELECT * FROM PrograssBarResponse")
        fun getall(): Model.PrograssBarResponse

    }

    @Dao
    interface SupportDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportResponse)

        @Query("DELETE FROM SupportResponse")
        fun deleteAll()

        @Query("SELECT * FROM SupportResponse")
        fun getall(): Model.SupportResponse

    }

    @Dao
    interface SupportListDetailDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportTicketDetails)

        @Query("DELETE FROM SupportTicketDetails")
        fun deleteAll()

        @Query("SELECT * FROM SupportTicketDetails")
        fun getAll(): Model.SupportTicketDetails

        @Query("SELECT * FROM SupportTicketDetails WHERE supportTicketId=:supportId")
        fun getDetailBySupportId(supportId: Long): Model.SupportTicketDetails

        @Query("DELETE FROM SupportTicketDetails  WHERE supportTicketId=:supportId")
        fun deleteDetailBySupportId(supportId: Long)

    }


    @Dao
    interface RaisedRequestDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportDetailListResponse)

        @Query("DELETE FROM SupportDetailListResponse")
        fun deleteAll()

        @Query("SELECT * FROM SupportDetailListResponse")
        fun getall(): Model.SupportDetailListResponse

    }

    @Dao
    interface RaisedRequestDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportIssueDetailsResponse)

        @Query("DELETE FROM SupportIssueDetailsResponse")
        fun deleteAll()

        @Query("SELECT * FROM SupportIssueDetailsResponse")
        fun getall(): Model.SupportIssueDetailsResponse

    }

    @Dao
    interface DailyReportDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.FeedBackSubmitRequest)

        @Query("DELETE FROM FeedBackSubmitRequest")
        fun deleteAll()

        @Query("SELECT * FROM FeedBackSubmitRequest")
        fun getall(): List<Model.FeedBackSubmitRequest>

    }


    @Dao
    interface FeedBackDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.DailyReportDetailsResponse)

        @Query("DELETE FROM DailyReportDetailsResponse")
        fun deleteAll()

        @Query("SELECT * FROM DailyReportDetailsResponse")
        fun getall(): Model.DailyReportDetailsResponse

    }


    @Dao
    interface FeedBackMediaDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.OfflineFeedBackMediaList)

        @Query("DELETE FROM OfflineFeedBackMediaList")
        fun deleteAll()

        @Delete
        fun deleteItem(item: Model.OfflineFeedBackMediaList)

        @Query("SELECT * FROM OfflineFeedBackMediaList")
        fun getall(): List<Model.OfflineFeedBackMediaList>

        @Update
        fun update(item: Model.OfflineFeedBackMediaList)

        @Query("SELECT * FROM OfflineFeedBackMediaList WHERE feedBackId=:feedBackId")
        fun getItemById(feedBackId: Int): List<Model.OfflineFeedBackMediaList>

    }


    @Dao
    interface ActivityMediaDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.OfflineMediaActivity)

        @Query("DELETE FROM OfflineMediaActivity")
        fun deleteAll()

        @Delete
        fun deleteItem(item: Model.OfflineMediaActivity)

        @Query("SELECT * FROM OfflineMediaActivity")
        fun getall(): List<Model.OfflineMediaActivity>

        @Update
        fun update(item: Model.OfflineMediaActivity)

        @Query("SELECT * FROM OfflineMediaActivity WHERE activityId=:activityId")
        fun getItemById(activityId: Int): List<Model.OfflineMediaActivity>

    }

    @Dao
    interface SupportMediaDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.OfflineSupportMediaList)

        @Query("DELETE FROM OfflineSupportMediaList")
        fun deleteAll()

        @Delete
        fun deleteItem(item: Model.OfflineSupportMediaList)

        @Query("SELECT * FROM OfflineSupportMediaList")
        fun getall(): List<Model.OfflineSupportMediaList>

        @Update
        fun update(item: Model.OfflineSupportMediaList)

        @Query("SELECT * FROM OfflineSupportMediaList WHERE supportId=:supportId")
        fun getItemById(supportId: Int): List<Model.OfflineSupportMediaList>

    }


    @Dao
    interface FeedBackSaveDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.FeedBackSaveDetails)

        @Query("DELETE FROM FeedBackSaveDetails")
        fun deleteAll()

        @Query("SELECT * FROM FeedBackSaveDetails")
        fun getall(): List<Model.FeedBackSaveDetails>

        @Query("DELETE FROM FeedBackSaveDetails  WHERE feedBackId=:feedBackId")
        fun deleteSynFeedback(feedBackId: Int)

        @Update
        fun update(item: Model.FeedBackSaveDetails)

    }


    @Dao
    interface ActivitySaveDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SaveActivityDetails)

        @Query("DELETE FROM SaveActivityDetails")
        fun deleteAll()

        @Query("SELECT * FROM SaveActivityDetails")
        fun getall(): List<Model.SaveActivityDetails>

        @Query("SELECT * FROM SaveActivityDetails  WHERE activityId=:activityId")
        fun getActivityById(activityId: Long): Model.SaveActivityDetails

        @Query("DELETE FROM SaveActivityDetails  WHERE activityId=:activityId")
        fun deleteSynActivity(activityId: Int)

        @Update
        fun update(item: Model.SaveActivityDetails)

    }


    @Dao
    interface SupportTicketSaveDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportCreateTicket)

        @Query("DELETE FROM SupportCreateTicket")
        fun deleteAll()

        @Query("SELECT * FROM SupportCreateTicket")
        fun getall(): List<Model.SupportCreateTicket>

        @Query("DELETE FROM SupportCreateTicket  WHERE supportId=:supportId")
        fun deleteSynSupport(supportId: Int)

        @Update
        fun update(item: Model.SupportCreateTicket)

    }

    @Dao
    interface UpdateSupportTicketSaveDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportUpdate)

        @Query("DELETE FROM SupportUpdate")
        fun deleteAll()

        @Query("SELECT * FROM SupportUpdate")
        fun getall(): List<Model.SupportUpdate>

        @Query("DELETE FROM SupportUpdate WHERE supportOfflineId=:supportId")
        fun deleteSynSupport(supportId: Int)

        @Update
        fun update(item: Model.SupportUpdate)

    }


    @Dao
    interface FeedBackRatingDetailsDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.FeedBackQuestionListResponse)

        @Query("DELETE FROM FeedBackQuestionListResponse")
        fun deleteAll()

        @Query("SELECT * FROM FeedBackQuestionListResponse")
        fun getall(): Model.FeedBackQuestionListResponse

    }


    @Dao
    interface DailyReportHistoryDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.DailyReportHistoryResponse)

        @Query("DELETE FROM DailyReportHistoryResponse")
        fun deleteAll()

        @Query("SELECT * FROM DailyReportHistoryResponse")
        fun getall(): Model.DailyReportHistoryResponse

    }


    @Dao
    interface PerformanceChartDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.PerformanceChartResponse)

        @Query("DELETE FROM PerformanceChartResponse")
        fun deleteAll()

        @Query("SELECT * FROM PerformanceChartResponse")
        fun getall(): Model.PerformanceChartResponse

    }


    @Dao
    interface PerformanceReportDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.PerformanceReportResponse)

        @Query("DELETE FROM PerformanceReportResponse")
        fun deleteAll()

        @Query("SELECT * FROM PerformanceReportResponse")
        fun getall(): Model.PerformanceReportResponse

    }
    @Dao
    interface ActivityDetailsByIdDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.ActivityResponse)

        @Query("DELETE FROM ActivityResponse")
        fun deleteAll()

        @Query("SELECT * FROM ActivityResponse")
        fun getall(): Model.ActivityResponse

    }




    @Dao
    interface ReasonListDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.DailyReportReasonsResponse)

        @Query("DELETE FROM DailyReportReasonsResponse")
        fun deleteAll()

        @Query("SELECT * FROM DailyReportReasonsResponse")
        fun getall(): Model.DailyReportReasonsResponse

    }


    @Dao
    interface WeatherListDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item:  Model.WeatherData)

        @Query("DELETE FROM WeatherData")
        fun deleteAll()

        @Query("SELECT * FROM WeatherData")
        fun getall(): Model.WeatherData

    }




    //PerformanceReportResponse
    @Dao

    interface FeedbackHistoryDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.FeedbackHistoryResponse)

        @Query("DELETE FROM FeedbackHistoryResponse")
        fun deleteAll()

        @Query("SELECT * FROM FeedbackHistoryResponse")
        fun getall(): Model.FeedbackHistoryResponse
    }

    @Dao
    interface SupportQuestionDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportQuestionListResponse)

        @Query("DELETE FROM SupportQuestionListResponse")
        fun deleteAll()

        @Query("SELECT * FROM SupportQuestionListResponse")
        fun getall(): Model.SupportQuestionListResponse
    }

    @Dao
    interface CommentMediaDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.OfflineUpdateSupportMediaList)

        @Query("DELETE FROM OfflineUpdateSupportMediaList")
        fun deleteAll()

        @Delete
        fun deleteItem(item: Model.OfflineUpdateSupportMediaList)

        @Query("SELECT * FROM OfflineUpdateSupportMediaList")
        fun getall(): List<Model.OfflineUpdateSupportMediaList>

        @Update
        fun update(item: Model.OfflineUpdateSupportMediaList)

        @Query("SELECT * FROM OfflineUpdateSupportMediaList WHERE supportId=:supportId")
        fun getItemById(supportId: Int): List<Model.OfflineUpdateSupportMediaList>

    }

    @Dao
    interface AddCommentDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.SupportCreateTicket)

        @Query("DELETE FROM SupportCreateTicket")
        fun deleteAll()

        @Query("SELECT * FROM SupportCreateTicket")
        fun getall(): List<Model.SupportCreateTicket>

        @Query("DELETE FROM SupportCreateTicket  WHERE supportId=:supportId")
        fun deleteSynSupport(supportId: Int)

        @Update
        fun update(item: Model.SupportCreateTicket)

    }


    @Dao
    interface DispatchArrivalDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.MaterialArrivalResponse)

        @Query("DELETE FROM MaterialArrivalResponse")
        fun deleteAll()

        @Query("SELECT * FROM MaterialArrivalResponse")
        fun getall(): Model.MaterialArrivalResponse

    }

    @Dao
    interface UpdateDispatchArrivalDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.DispatchArrivalDetailsList)

        @Query("DELETE FROM DispatchArrivalDetailsList")
        fun deleteAll()

        @Query("SELECT * FROM DispatchArrivalDetailsList")
        fun getall(): List<Model.DispatchArrivalDetailsList>
    }

    @Dao
    interface DispatchArrivalHistoryDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.HistoryMaterialArrivalResponse)

        @Query("DELETE FROM HistoryMaterialArrivalResponse")
        fun deleteAll()

        @Query("SELECT * FROM HistoryMaterialArrivalResponse")
        fun getall(): Model.HistoryMaterialArrivalResponse
    }

    @Dao
    interface DispatchArrivalHistoryDetailsDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(item: Model.HistoryMaterialArrivalDetailResponse)

        @Query("DELETE FROM HistoryMaterialArrivalDetailResponse")
        fun deleteAll()

        @Query("SELECT * FROM HistoryMaterialArrivalDetailResponse")
        fun getall(): Model.HistoryMaterialArrivalDetailResponse
    }


}