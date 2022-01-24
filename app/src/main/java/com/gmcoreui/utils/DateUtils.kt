@file:Suppress("SENSELESS_COMPARISON")

package com.gmcoreui.utils

import android.content.Context
import android.util.Log
import com.gm.db.SingleTon
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


object DateUtils {
    val DECI_SECOND_IN_MILLIS = 100
    val SECOND_IN_MILLIS = 1000
    val MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS
    val HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS

    val SERVER_FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    val SERVER_FORMAT_DATE_TIME_TRIMMED = "yyyy-MM-dd'T'HH:mm:ss"
    val SERVER_FORMAT_DATE = "dd MMM YYYY HH:mm:ss"
    val SERVER_TIME_TRIMMED = "HH:mm:ss"
    var DEFAULT_DATE_FORMAT: String = "dd/MM/yyyy"
    var DISPLAY_DATE_FORMAT: String = "dd MMM, yyyy"
    var DISPLAY_DATE: String = "dd MMM yyyy"
    var DISPLAY_MONTH_DATE: String = "dd MMMM yyyy"
    val SERVER_DATE_FORMAT = "yyyy-MM-dd"
    var DATE_FORMAT: String = "dd/MM/yyyy"
    var LAST_UPDATE_DATE_FORMAT: String = "dd/MM/yyyy - hh:mm a"
    val API_TIMING_LOG: String = "######"

    var WEATHER_DATE:String="dd MMM, yyyy "

    var WEATHER_TIME:String="hh:mm"
    var CHECK_HOURS="HH"
    var CHECK_HOURS1="hh"


    fun toServerDate(timeInMillis: Long, formatString: String): String {
        val dateFormat = SimpleDateFormat(formatString)
        return dateFormat.format(Date(timeInMillis))
    }

    fun toServerDate(date: Date, formatString: String): String {
        val dateFormat = SimpleDateFormat(formatString)
        return dateFormat.format(date)
    }

    fun toServerDate(date: Date): String {
        val dateFormat = SimpleDateFormat(SERVER_DATE_FORMAT)
        return dateFormat.format(date)
    }

    fun toServerDateFormat(date: Date): String {
        val dateFormat = SimpleDateFormat(DISPLAY_DATE)
        return dateFormat.format(date)
    }

    fun toServerDate(date: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        val dateParsed = sdf.parse(date)
        val format = SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault())
        return format.format(dateParsed)
    }

    fun toServer(date: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE, Locale.getDefault())
        val dateParsed = sdf.parse(date)
        val format = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        return format.format(dateParsed)
    }

    fun toDisplayDate(inputDate: String, inputDateFormat: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        val date = sdf.parse(inputDate)
        var value = date

        val format = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        return format.format(date)
    }

    fun toDisplayDate1(inputDate: String, inputDateFormat: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
        val date = sdf.parse(inputDate)
        var value = date

        val format = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        return format.format(date)
    }





    fun milliSecondsToTimer(duration: Int): String {
        var finalTimerString = ""
        var secondsString = ""
        val hours = (duration / (1000 * 60 * 60))
        val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((duration % (1000 * 60 * 60)) % (1000 * 60) / 1000)

        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }
        if (seconds < 10) {
            secondsString = "0$seconds"
        } else {
            secondsString = "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"
        return finalTimerString
    }


    fun toDisplayDateTH(inputDate: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        val date = sdf.parse(inputDate)
        var format = SimpleDateFormat("d", Locale.getDefault())
        if (date.toString().endsWith("1") && !date.toString().endsWith("11"))
            format = SimpleDateFormat("dd 'st',MMM, yyyy", Locale.getDefault())
        else if (date.toString().endsWith("2") && !date.toString().endsWith("12"))
            format = SimpleDateFormat("dd'nd' MMM yyyy", Locale.getDefault())
        else if (date.toString().endsWith("3") && !date.toString().endsWith("13"))
            format = SimpleDateFormat("dd'rd' MMM yyyy", Locale.getDefault())
        else
            format = SimpleDateFormat("dd'th' MMM yyyy", Locale.getDefault())

        return format.format(date)
    }


    fun toServerDate(inputDate: String, inputDateFormat: String): String {
        val sdf = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        val date = sdf.parse(inputDate)
        val format = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
        return format.format(date)
    }

    fun toDisplayDate(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }


    fun toDisplayDateHour(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(CHECK_HOURS, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }

    fun toDisplayDateHour1(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(CHECK_HOURS1, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }


    fun toDisplayServerDate(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val date = sdf.parse(inputDate)

            val format = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }
    fun toDisplayDate1(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }
    fun toDisplayDateWeather(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(WEATHER_DATE, Locale.getDefault())
            return format.format(date)
        }
        return ""
    }
    fun toDisplayTimeWeatherReport(inputDate: String?): String {
        if (!inputDate.isNullOrEmpty()) {
            val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
            val date = sdf.parse(inputDate)
            val format = SimpleDateFormat(WEATHER_TIME, Locale.getDefault())
            return format.format(date)
        }
        return ""

    }

    fun dateToDisplayDate(date: Date): String {
        val format = SimpleDateFormat(DISPLAY_DATE_FORMAT)
        return format.format(date)
    }


    fun getTommarrowDate(): ArrayList<String> {
        var startDateList = ArrayList<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        for (i in 0..1) {
            val calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day = sdf.format(calendar.time)
            startDateList.add(day)
        }
        return startDateList
    }


    fun getTommarrowDateInput(): ArrayList<String> {
        var startDateList = ArrayList<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        for (i in 0..1) {
            val calendar = GregorianCalendar()
            calendar.add(Calendar.DATE, i)
            val day = sdf.format(calendar.time)
            startDateList.add(day)
        }
        return startDateList
    }

    fun getPerviousDate(): String {

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val calendar = GregorianCalendar()
        calendar.add(Calendar.DATE, -1)
        val day = sdf.format(calendar.time)
        return day

    }

    fun getPerviousDate(date: String):String
    {        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, -1)
         return dateFormat.format(calendar.time)
    }


    private fun getFirstMillis(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }



    fun getLastMillis(timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun getFirstMillis(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    fun getLastMillis(date: Date): Long {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        return calendar.timeInMillis
    }

    fun getTodaysFirstMillis(): Long {
        return getFirstMillis(Date())
    }

    fun getTodaysLastMillis(): Long {
        return getLastMillis(Date())
    }

    fun getTodayDate(dateFormat: String): String {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        return simpleDateFormat.format(System.currentTimeMillis())
    }

    fun getTodayDate(): String {
        val simpleDateFormat = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
        return simpleDateFormat.format(System.currentTimeMillis())
    }

    fun getTodayDateWeather(): String {
        val simpleDateFormat = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        return simpleDateFormat.format(System.currentTimeMillis())
    }


    fun getTimestamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    fun getDateByAddingDays(nDays: Int): String {
        val simpleDateFormat = SimpleDateFormat(SERVER_FORMAT_DATE_TIME, Locale.getDefault())
        return simpleDateFormat.format(getFirstMillis(addDays(getTodaysFirstMillis(), nDays)))
    }

    fun addDays(dateTime: Long, nDays: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTime
        calendar.add(Calendar.DAY_OF_YEAR, nDays)
        return calendar.timeInMillis
    }

    fun isToday(timeInMillis: Long): Boolean {
        return timeInMillis >= getTodaysFirstMillis() && timeInMillis <= getTodaysLastMillis()
    }

    fun isYesterday(timeInMillis: Long): Boolean {
        val yesterdaysFirstMillis = addDays(getTodaysFirstMillis(), -1)
        val yesterdaysLastMillis = getLastMillis(yesterdaysFirstMillis)

        return timeInMillis in yesterdaysFirstMillis..yesterdaysLastMillis
    }

    fun addDays(date: Date, days: Int): Date {
        val cal = GregorianCalendar()
        cal.time = date
        cal.add(Calendar.DATE, days)
        return cal.time
    }

    fun getCalendarQuarter(): Int {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        return month / 3
    }

    fun getFinancialQuarter(): Int {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        var currentQuarter = month / 3 + 1
        if (currentQuarter == 1) {
            currentQuarter = 4
        } else {
            currentQuarter -= 1
        }
        return currentQuarter
    }

    fun firstDayOfQuarter(quarter: Int): Date {
        val cal = Calendar.getInstance()
        if (quarter == 4) {
            if (Calendar.getInstance().get(Calendar.MONTH) >= 3) {
                cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)
            }
        } else {
            if (Calendar.getInstance().get(Calendar.MONTH) < 3) {
                cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1)
            }
        }
        cal.set(Calendar.MONTH, quarter * 3)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        com.core.utils.Logger.i("@@@@ Start Quarter - $quarter", "${cal.time}")
        return cal.time
    }

    fun lastDayOfQuarter(quarter: Int): Date {
        val cal = Calendar.getInstance()
        if (quarter == 4) {
            if (Calendar.getInstance().get(Calendar.MONTH) >= 3) {
                cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1)
            }
        } else {
            if (Calendar.getInstance().get(Calendar.MONTH) < 3) {
                cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1)
            }
        }
        val monthNo = (quarter * 3) + 2
//        cal.set(Calendar.MONTH, monthNo)
        val month = cal.get(Calendar.MONTH)
        val add = monthNo - month
        cal.add(Calendar.MONTH, add)
        Log.i("@@@@", "${cal.get(Calendar.MONTH)}")
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        Log.i("@@@@ End Quarter1 - $quarter", "${cal.time}")
        cal.set(Calendar.HOUR_OF_DAY, 22)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        Log.i("@@@@ End Quarter2 - $quarter", "${cal.time}")
        return cal.time
    }

    fun firstDayOfFinancialYear(): Date {
        val cal = Calendar.getInstance()
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        if (month < 3) {
            cal.set(Calendar.YEAR, year - 1)
        }
        cal.set(Calendar.MONTH, 3)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        com.core.utils.Logger.i("@@@@ Start", "${cal.time}")
        return cal.time
    }

    fun lastDayOfFinancialYear(): Date {
        val cal = Calendar.getInstance()
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        if (month >= 3) {
            cal.set(Calendar.YEAR, year + 1)
        }
        cal.set(Calendar.MONTH, 2)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 22)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        com.core.utils.Logger.i("@@@@ End", "${cal.time}")
        return cal.time
    }

    fun getQuarterYear(quarter: Int): Int {
        var year = Calendar.getInstance().get(Calendar.YEAR)
        if (quarter == 4) {
            if (Calendar.getInstance().get(Calendar.MONTH) >= 3) {
                year += 1
            }
        } else {
            if (Calendar.getInstance().get(Calendar.MONTH) < 3) {
                year -= 1
            }
        }
        return year
    }

    fun firstDateofCurrentMonth(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
        return cal.time
    }

    fun lastDateofCurrentMonth(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return cal.time
    }

    fun toMT(value: Double?): String {
        return "${DecimalFormat("##.###").format(value ?: 0.0)} MT"
    }

    fun toRoundOff(value: Double?): String {
        return "${DecimalFormat("##.##").format(value ?: 0.0)}"
    }

    fun toCase(value: Double?): String {
        return "${DecimalFormat("##.##").format(value ?: 0.0)} Case"
    }

    fun toMTValues(case: Double?, constant: Double?): String {
        return "${DecimalFormat("##.##").format(((case ?: 0.0) * (constant ?: 0.0)))}"
    }

    fun toRs(value: Double?): String {
        value?.let {
            /*if (it == 0.0 || it.isInfinite()) {
                return "Rs.0.00"
            }
            if (it < 1000) {
                return "Rs.${String.format("%.2f", it)}"
            }
            if ((it / 100000.00).toInt() <= 0) {
                return "Rs.${DecimalFormat("##.##").format(it / 1000.00)} K"
            }
            if ((it / 10000000.00).toInt() <= 0) {
                return "Rs.${DecimalFormat("##.##").format(it / 100000.00)} L"
            }
            return "Rs.${DecimalFormat("##.##").format(it / 10000000.00)} Cr"*/
            if (it == 0.0 || it.isInfinite()) {
                return "Rs.0.00"
            }
            return "Rs.${String.format("%.2f", it)}"
        }
        return "Rs.0.00"
    }

    fun formatStringToDecimalText(amount: String?): String? {
        try {
            if (amount != null && !amount.isEmpty()) {
                val bd = BigDecimal(amount)
                Log.d("1234444",DecimalFormat("0.0").format(bd).toString());
                return DecimalFormat("0.0").format(bd)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return amount
    }
    fun convertCelsiusToFahrenheit(celsius: Double):String
    {
        val fahrenheit=celsius * 9 / 5 + 32
        val fahrenheitText=fahrenheit.toString();
        if(fahrenheitText.contains(".") && fahrenheitText.split(".")[1].isNotEmpty() && fahrenheitText.split(".")[1][0]!=null){
            val fahrenheitVal = fahrenheitText.split(".")[0]+"."+fahrenheitText.split(".")[1][0].plus("°F")
            return fahrenheitVal
        }
        else
            return  fahrenheit.toString().plus("°F")

    }
    fun getCelsius(celsius: Double):String
    {

        return  String.format("%.1f", celsius).plus("°C")
    }




    fun roundOFF(value: Float?, context: Context): String {
        value?.toDouble()?.let {
            if (it == 0.0 || it.isInfinite()) {
                return "0 "
            }
            if (it < 1000) {
                return "${DecimalFormat("##.##").format(it)} "
            }
            if ((it / 100000.00).toInt() <= 0) {
                return "${DecimalFormat("##.##").format(it / 1000.00)}${SingleTon.getResourceStringValue("label_k")}"
            }
            if ((it / 10000000.00).toInt() <= 0) {
                return "${DecimalFormat("##.##").format(it / 100000.00)} L"
            }
            return "${DecimalFormat("##.##").format(it / 10000000.00)} Cr"
        }
        return "0"
    }

    fun getDayDifference(date1: String): Long {
        val calendar = Calendar.getInstance()
        calendar.time = convertStringToDate(date1, SERVER_FORMAT_DATE_TIME_TRIMMED)
        val today = Calendar.getInstance()
        val diff = today.timeInMillis - calendar.timeInMillis
        return diff / (24 * 60 * 60 * 1000)
    }


    fun convertStringToDate(strDate: String, format: String): Date {
        return SimpleDateFormat(format, Locale.getDefault()).parse(strDate)
    }


    fun toDisplayTime(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("h:mm a").format(dateObj)
        }
        return timeConverted
    }


    fun toDisplayTimeWeather1(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("h").format(dateObj)
        }
        return timeConverted
    }


    fun toDisplayTimeMaterial(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {

            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("h:mm").format(dateObj)
        }
        return timeConverted
    }


    fun toDisplayTimeMaterialAM(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("a").format(dateObj)
        }
        return timeConverted
    }


    fun toDisplayTimeHistory(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat("HH:mmm:ss")
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("h:mm").format(dateObj)
        }
        return timeConverted
    }
    fun toDisplayTimeHistoryAM(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat("HH:mmm:ss")
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("a").format(dateObj)
        }
        return timeConverted
    }

    fun toDisplayTimeWeather(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("yyyy-MM-dd'T'HH").format(dateObj)
        }
        return timeConverted
    }
    fun toDisplayTimeWeatherDate(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("yyyy-MM-dd'T'HH").format(dateObj)
        }
        return timeConverted
    }

    fun toDisplayTimeWeatherAM(inputTime: String?): String {
        var timeConverted = ""
        inputTime?.let {
            val convert = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
            val dateObj = convert.parse(inputTime)
            timeConverted = SimpleDateFormat("a").format(dateObj)
        }
        return timeConverted
    }


    val quarterMap = hashMapOf<Int, IntArray>()
    fun constructMap(startMonth: Int) {
        var quarterCount = 0
        var month = startMonth
        while (quarterCount < 4) {
            quarterCount++
            var count = 0
            val monthArray = IntArray(3)
            while (count < 3) {
                val monthNo = month++ % 12
                if (monthNo == 0) {
                    monthArray[count] = 12
                } else {
                    monthArray[count] = monthNo
                }
                count++
            }
            quarterMap.put(quarterCount, monthArray)
        }
    }

    fun firstDayOfCurrentQuarter(currentMonth: Int): Int {
        Log.i("####cm", currentMonth.toString())
        for ((key, value) in quarterMap) {
            for (element in value) {
                if (element == currentMonth) {
                    return value[0]
                }
            }
        }
        return -1
    }

    fun lastDayOfCurrentQuarter(currentMonth: Int): Int {
        Log.i("####cm", currentMonth.toString())
        for ((key, value) in quarterMap) {
            for (element in value) {
                if (element == currentMonth) {
                    return value[2]
                }
            }
        }
        return -1
    }

    fun firstDayOfCurrentQuarter(): Date {
        val cal = Calendar.getInstance()
        val currentMonth = (cal.get(Calendar.MONTH)) + 1
        Log.i("####currentMonth", currentMonth.toString())
        val firstMonth = (firstDayOfCurrentQuarter(currentMonth))
        Log.i("####firstMonth", firstMonth.toString())
        if (firstMonth > currentMonth) {
            val currentYear = cal.get(Calendar.YEAR)
            Log.i("####currentYear", currentYear.toString())
            val setYear = cal.get(Calendar.YEAR) - 1
            Log.i("####setYear", setYear.toString())
            cal.set(Calendar.YEAR, setYear)
        }
        cal.set(Calendar.MONTH, firstMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Log.i("####fq", cal.time.toString())
        return cal.time
    }

    fun lastDayOfCurrentQuarter(): Date {
        val cal = Calendar.getInstance()
        val currentMonth = (cal.get(Calendar.MONTH)) + 1
        Log.i("####currentMonth", currentMonth.toString())
        val endMonth = (lastDayOfCurrentQuarter(currentMonth))
        Log.i("####endMonth", endMonth.toString())
        if (endMonth < currentMonth) {
            val currentYear = cal.get(Calendar.YEAR)
            Log.i("####currentYear", currentYear.toString())
            val setYear = cal.get(Calendar.YEAR) + 1
            Log.i("####setYear", setYear.toString())
            cal.set(Calendar.YEAR, setYear)
        }
        cal.set(Calendar.MONTH, endMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 22)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        Log.i("####lq", cal.time.toString())
        return cal.time
    }

    fun getFinancialYearStartMonth(firstDate: String?): Int {
        if (!firstDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.time = convertStringToDate(firstDate!!, SERVER_FORMAT_DATE_TIME_TRIMMED)
            Log.i("$$$$$$$$$$$${firstDate}", (calendar.get(Calendar.MONTH) + 1).toString())
            return calendar.get(Calendar.MONTH) + 1
        } else {
            return 1
        }

    }

    fun toDisplayDateFormat(inputDate: String): String {
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        val date = sdf.parse(inputDate)
        val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return format.format(date)
    }

    fun getQuarter(month: Int, financialYearMonth: Int): Int {
        var rows = 4
        var columns = 3
        var rowCount = 0
        val sum1 = Array(rows) { IntArray(columns) }
        var n = financialYearMonth
        for (i in 0..rows - 1) {
            for (j in 0..columns - 1) {
                if (n <= 12) {
                    sum1[i][j] = n
                } else {
                    n = 1
                    sum1[i][j] = n
                }
                n++

            }
        }
        var quater = -1
        var count = 0
        for (row in sum1) {
            count++
            for (column in row) {
                if (column == month) {
                    //rowCount = row.count()
                    quater = count
                }
            }
            println()
        }
        //return rowCount
        return quater
    }

    fun getMonth(firstDate: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = firstDate
        cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH)) + 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }


    fun getDatesBetween(
            startDate: Date, endDate: Date): List<Date> {
        val datesInRange = arrayListOf<Date>()
        val calendar = GregorianCalendar()
        calendar.setTime(startDate)
        val endCalendar = GregorianCalendar()
        endCalendar.setTime(endDate)
        while (calendar.before(endCalendar)) {
            val result = calendar.getTime()
            datesInRange.add(result)
            calendar.add(Calendar.DATE, 1)
        }
        return datesInRange
    }

    fun convertDate(date: String): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = convertStringToDate(date, SERVER_DATE_FORMAT)
        return calendar
    }

    fun getTimeDifference(hitTime: Date): String {
        val durationInMillis = Calendar.getInstance().time.time - hitTime.time
        val millis = durationInMillis % 1000
        val second = durationInMillis / 1000 % 60
        val minute = durationInMillis / (1000 * 60) % 60
        val hour = durationInMillis / (1000 * 60 * 60) % 24
        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis)
    }


    fun comparedToTime(time: String):Boolean
    {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH")
        val getCurrentTime = sdf.format(c.time)
        val getTestTime = time

        return getCurrentTime.compareTo(getTestTime) == 0
    }


    fun comparedToTime1(time: String):Boolean
    {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(SERVER_FORMAT_DATE_TIME_TRIMMED)
        val getCurrentTime = sdf.format(c.time)
        val getTestTime = time

        return getCurrentTime.compareTo(getTestTime) < 0
    }

    fun getDayDifference(inputDate1: String?, inputDate2: String? = null, inputDateFormat: String = SERVER_DATE_FORMAT): Long? {
        if (inputDate2.isNullOrEmpty()) {
            convertStringToDateFormat(inputDate = inputDate1, inputDateFormat = inputDateFormat)?.let {
                return getDayDifference(inputDate1 = it)
            }
        } else {
            val date1 = convertStringToDateFormat(inputDate = inputDate1, inputDateFormat = inputDateFormat)
            val date2 = convertStringToDateFormat(inputDate = inputDate2, inputDateFormat = inputDateFormat)
            date1?.let {
                if (date2 != null) {
                    return getDayDifference(inputDate1 = it, inputDate2 = date2)
                } else {
                    return getDayDifference(inputDate1 = it)
                }
            }
        }
        return null
    }

    fun getDayDifference(inputDate1: Date, inputDate2: Date = Date()): Long {
        val diff = getFirstMillis(inputDate2) - getFirstMillis(inputDate1)
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    fun convertStringToDateFormat(inputDate: String?, inputDateFormat: String = SERVER_DATE_FORMAT): Date? {
        if (!inputDate.isNullOrEmpty()) {
            val dateFormat = SimpleDateFormat(inputDateFormat, Locale.getDefault())
            return dateFormat.parse(inputDate)
        }
        return null
    }
}
