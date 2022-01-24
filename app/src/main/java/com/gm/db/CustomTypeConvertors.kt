package com.gm.db

import android.view.Display
import androidx.room.TypeConverter
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.models.ModuleType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class CustomTypeConvertors {

    open class MediaTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.Media> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.Media>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.Media>?): String? {
            if (data == null) {
                return null
            }
            return Gson().toJson(data)
        }
    }


    open class EnumMediaTypeConvertor {
        @TypeConverter
        fun stringToEnum(data: String): MediaType {
            return MediaType.valueOf(data)
        }

        @TypeConverter
        fun enumToString(data: MediaType): String {
            return data.name
        }
    }

    open class EnumModuleTypeConvertor {
        @TypeConverter
        fun stringToEnum(data: String): ModuleType {
            return ModuleType.valueOf(data)
        }

        @TypeConverter
        fun enumToString(data: ModuleType): String {
            return data.name
        }
    }


    open class RepositoryTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.Repository> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.Repository>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.Repository>): String {
            return Gson().toJson(data)
        }
    }


    open class RepositoryCategoriesTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.RepositoryCategories> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.RepositoryCategories>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.RepositoryCategories>): String {
            return Gson().toJson(data)
        }
    }

    open class SupportTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.Support> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.Support>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.Support>): String {
            return Gson().toJson(data)
        }
    }


    open class NotificationTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.Notificaiton> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.Notificaiton>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.Notificaiton>): String {
            return Gson().toJson(data)
        }
    }


    open class RepositoryTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.Repository {
            if (data == null) {
                return Model.Repository()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.Repository::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.Repository): String {
            return Gson().toJson(data)
        }
    }


    open class ProgressConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.PrograssBar {
            if (data == null) {
                return Model.PrograssBar()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.PrograssBar::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.PrograssBar): String {
            return Gson().toJson(data)
        }
    }


    open class StatusNotificationConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.StatusNotificaiton {
            if (data == null) {
                return Model.StatusNotificaiton()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.StatusNotificaiton::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.StatusNotificaiton): String {
            return Gson().toJson(data)
        }
    }


    open class SupportDetailsConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.SupportDetailsList {
            if (data == null) {
                return Model.SupportDetailsList()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.SupportDetailsList::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.SupportDetailsList): String {
            return Gson().toJson(data)
        }
    }



    open class ActivityListConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.ActivityList {
            if (data == null) {
                return Model.ActivityList()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.ActivityList::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.ActivityList): String {
            return Gson().toJson(data)
        }
    }






    open class SupportDetailsListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SupportDetails> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SupportDetails>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SupportDetails>): String {
            return Gson().toJson(data)
        }
    }

    open class RaisedRequestListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SuppourtDetailList> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SuppourtDetailList>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SuppourtDetailList>): String {
            return Gson().toJson(data)
        }
    }


    open class QuestionListTypeConvertor {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<String> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<String>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<String>): String {
            return Gson().toJson(data)
        }
    }


    open class SupportTicketDetailsConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.SupportTicketDetails {
            if (data == null) {
                return Model.SupportTicketDetails()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.SupportTicketDetails::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.SupportTicketDetails): String {
            return Gson().toJson(data)
        }
    }

    // FeedBackDetails

    open class FeedBackDetailsConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.FeedBackDetails {
            if (data == null) {
                return Model.FeedBackDetails()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.FeedBackDetails::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.FeedBackDetails): String {
            return Gson().toJson(data)
        }
    }

    open class MediaListTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SupportTicketsResponse> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SupportTicketsResponse>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SupportTicketsResponse>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.SupportTicketsResponse>())
            }
            return Gson().toJson(data)
        }
    }


    open class SupportQueriesTypeConverter {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SupportQueries> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SupportQueries>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SupportQueries>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.SupportQueries>())
            }
            return Gson().toJson(data)
        }
    }


    open class SupportMediaListTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SupportTicketsMediaResponse> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SupportTicketsMediaResponse>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SupportTicketsMediaResponse>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.SupportTicketsMediaResponse>())
            }
            return Gson().toJson(data)
        }
    }




    open class ReasonConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.Reasons> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.Reasons>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.Reasons>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.Reasons>())
            }
            return Gson().toJson(data)
        }
    }
    open class FeedBackQuestionListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Long> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Long>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Long>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Long>())
            }
            return Gson().toJson(data)
        }
    }

    open class FeedBackMediaTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.SupportTicketsResponse {
            if (data == null) {
                return Model.SupportTicketsResponse()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.SupportTicketsResponse::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.SupportTicketsResponse): String {
            return Gson().toJson(data)
        }
    }

    open class DailyReportDetailsConventer{
        @TypeConverter
        fun stringToList(data: String?): Model.DailyReportDetails {
            if (data == null) {
                return Model.DailyReportDetails()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.DailyReportDetails::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.DailyReportDetails): String {
            return Gson().toJson(data)
        }

    }


    open class FeedbackRatingListTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.FeedBackQuestionList> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.FeedBackQuestionList>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.FeedBackQuestionList>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.FeedBackQuestionList>())
            }
            return Gson().toJson(data)
        }
    }



    open class StarDetailsListTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.StarDetails> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.StarDetails>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.StarDetails>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.StarDetails>())
            }
            return Gson().toJson(data)
        }
    }



    open class QuestionListDetailsTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.QuestionListDetails> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.QuestionListDetails>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.QuestionListDetails>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.QuestionListDetails>())
            }
            return Gson().toJson(data)
        }
    }


    open class DailyReportDateHistoryTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.DailyReportDateHistory>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.DailyReportDateHistory>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.DailyReportDateHistory>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.DailyReportDateHistory>())
            }
            val dataTemp:List<Model.DailyReportDateHistory> =ArrayList<Model.DailyReportDateHistory>(data)
            return Gson().toJson(dataTemp)
        }
    }



    open class FeedBackHistoryTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.FeedBackRatingHistory>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.FeedBackRatingHistory>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.FeedBackRatingHistory>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.FeedBackRatingHistory>())
            }
            val dataTemp:List<Model.FeedBackRatingHistory> =ArrayList<Model.FeedBackRatingHistory>(data)
            return Gson().toJson(dataTemp)
        }
    }
    //ArrayList<Model.FeedBackRatingHistory>

    open class DailyReportTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.DailyReportHistory {
            if (data == null) {
                return Model.DailyReportHistory()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.DailyReportHistory::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.DailyReportHistory): String {
            return Gson().toJson(data)
        }
    }




    open class MaterialArrivalPendingTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.MaterialArrivalPending>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.MaterialArrivalPending>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.MaterialArrivalPending>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.MaterialArrivalPending>())
            }
            return Gson().toJson(data)
        }

    }



    open class SupportQuestionTypeConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.SupportQuestion>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.SupportQuestion>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.SupportQuestion>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.SupportQuestion>())
            }
            return Gson().toJson(data)
        }

    }

    open class MaterialArrivalTypeConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.MaterialArrivalList {
            if (data == null) {
                return Model.MaterialArrivalList()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.MaterialArrivalList::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.MaterialArrivalList): String {
            return Gson().toJson(data)
        }
    }


    open class Chart1TypeConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.Chart1 {
            if (data == null) {
                return Model.Chart1()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.Chart1::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.Chart1): String {
            return Gson().toJson(data)
        }
    }

    open class MaterialArrivalListConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.MaterialArrival>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.MaterialArrival>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.MaterialArrival>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.MaterialArrival>())
            }
            return Gson().toJson(data)
        }

    }

    open class HistoryMaterialArrivalListConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.HistoryMaterialArrival>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.HistoryMaterialArrival>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.HistoryMaterialArrival>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.HistoryMaterialArrival>())
            }
            return Gson().toJson(data)
        }

    }

    open class HistoryDetailMaterialArrivalListConventer{
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.HistoryMaterialArrivalDetail>
        {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.HistoryMaterialArrivalDetail>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.HistoryMaterialArrivalDetail>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.HistoryMaterialArrivalDetail>())
            }
            return Gson().toJson(data)
        }

    }

    open class CombinedChartListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.NewChart> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.NewChart>>() {

            }.type

            return gson.fromJson(data, listType)
        }


        @TypeConverter
        fun listToString(data: ArrayList<Model.NewChart>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.NewChart>())
            }
            return Gson().toJson(data)
        }


    }
    open class DailyWeatherDataListConventer{

        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.DailyWeatherData> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.DailyWeatherData>>() {

            }.type

            return gson.fromJson(data, listType)
        }


        @TypeConverter
        fun listToString(data: ArrayList<Model.DailyWeatherData>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.DailyWeatherData>())
            }
            return Gson().toJson(data)
        }




    }

    open class  ChartDetailsListConventer{

        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.ChartBatchList> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.ChartBatchList>>() {

            }.type

            return gson.fromJson(data, listType)
        }


        @TypeConverter
        fun listToString(data: ArrayList<Model.ChartBatchList>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.ChartBatchList>())
            }
            return Gson().toJson(data)
        }




    }


    open class PerformanceChartListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.PerformanceChart> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.PerformanceChart>>() {

            }.type

            return gson.fromJson(data, listType)
        }


        @TypeConverter
        fun listToString(data: ArrayList<Model.PerformanceChart>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.PerformanceChart>())
            }
            return Gson().toJson(data)
        }


    }
    open class ChartDataListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.ChartData> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.ChartData>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.ChartData>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.ChartData>())
            }
            return Gson().toJson(data)
        }

    }


    open class ChartDetailListConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.ChartDetailList> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.ChartDetailList>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.ChartDetailList>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.ChartDetailList>())
            }
            return Gson().toJson(data)
        }

    }



    open class PerformanceReportConventer {
        @TypeConverter
        fun stringToList(data: String?): ArrayList<Model.PerformanceReport> {
            if (data == null) {
                return arrayListOf()
            }
            val gson = Gson()
            val listType = object : TypeToken<ArrayList<Model.PerformanceReport>>() {

            }.type

            return gson.fromJson(data, listType)
        }

        @TypeConverter
        fun listToString(data: ArrayList<Model.PerformanceReport>?): String {
            if (data == null) {
                return Gson().toJson(arrayListOf<Model.PerformanceReport>())
            }
            return Gson().toJson(data)
        }

    }

    //PerformanceReport



    open class ReasonsConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.DailyReportReasons {
            if (data == null) {
                return Model.DailyReportReasons()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.DailyReportReasons::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.DailyReportReasons): String {
            return Gson().toJson(data)
        }
    }

    open class WeatherConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.Weather {
            if (data == null) {
                return Model.Weather()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.Weather::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.Weather): String {
            return Gson().toJson(data)
        }
    }


    open class DailyReportSupportConventer {
        @TypeConverter
        fun stringToList(data: String?): Model.DailyReportSupport {
            if (data == null) {
                return Model.DailyReportSupport()
            }
            val gson = Gson()
            return gson.fromJson(data, Model.DailyReportSupport::class.java)
        }

        @TypeConverter
        fun listToString(data: Model.DailyReportSupport): String {
            return Gson().toJson(data)
        }
    }




}