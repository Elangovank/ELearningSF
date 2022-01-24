package com.gm.controllers.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.core.utils.AppPreferences
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.gm.R
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gm.utilities.LocaleHelper
import com.gmcoreui.controllers.BaseActivity
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.activity_chart_view.*
import java.text.DecimalFormat

class ChartViewActivity : BaseActivity() {
    var lineChart: LineChart? = null
    var max: Double? = 0.0
    var min: Double? = 0.0
    var maxAgeValue: Long? = 0
    var chartDetail: Model.NewChart? = null
    var keySting: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguagePreference()
        setContentView(R.layout.activity_chart_view)
        if (intent?.getSerializableExtra(GMKeys.chartDetails) != null) {
            chartDetail = intent?.getSerializableExtra(GMKeys.chartDetails) as Model.NewChart
            dayTextView?.text = getResourceString("d_age")
            if (chartDetail?.key?.contains("&")!!) {
                keySting = chartDetail?.key
            } else {
                when (chartDetail?.keyValue) {
                    GMKeys.dayGain -> {
                        keySting = getResourceString("day_gain")
                    }
                    GMKeys.mortality -> {
                        keySting = getResourceString("mortality")
                    }
                    GMKeys.feedintake -> {
                        keySting = getResourceString("feed_intake")
                    }
                    GMKeys.bodyWeight -> {
                        keySting =  getResourceString("body_weight")
                    }
                    GMKeys.fcr -> {
                        keySting =  getResourceString("fcr_ratio")
                    }
                }

            }
            headingTextView?.text = keySting
            loadXaxies()
            var chartItem = 0
            lineChart?.setVisibleXRangeMaximum(15f)
            rightScroll.setOnClickListener {
                if (chartItem < maxAgeValue!!) {
                    rightScroll.visibility = View.VISIBLE
                    chartItem = chartItem + GMKeys.ITEM_IN_CHART
                    lineChart?.moveViewToX(chartItem.toFloat())
                    leftScroll.visibility = View.VISIBLE


                } else {
                    rightScroll.visibility = View.INVISIBLE
                }
            }
            leftScroll.setOnClickListener {
                if (chartItem > 0) {
                    leftScroll.visibility = View.VISIBLE
                    rightScroll.visibility = View.VISIBLE
                    chartItem = chartItem - GMKeys.ITEM_IN_CHART
                    lineChart?.moveViewToX(chartItem.toFloat())
                } else {
                    leftScroll.visibility = View.INVISIBLE
                    rightScroll.visibility = View.VISIBLE
                }


            }


            if (chartDetail?.key?.contains("&")!!) {
                rightLableNameTextView.visibility = View.VISIBLE
                val list = chartDetail?.fcrkey?.split("&")
                rightLableNameTextView.text = list?.get(1)
                leftLableNameTextView.text = list?.get(0)
            } else {
                rightLableNameTextView.visibility = View.GONE
                leftLableNameTextView.text = keySting
            }
            lineChartView?.let {
                if (chartDetail?.keyValue == GMKeys.bodyWeight || chartDetail?.keyValue === GMKeys.dayGain) {
                    if ((chartDetail?.keyValue == GMKeys.bodyWeight || chartDetail?.keyValue === GMKeys.dayGain) && chartDetail?.value?.size != 1) {
                        loadChart(it)
                    } else {
                        loadEmpty(it)
                    }
                } else {
                    if (chartDetail?.value?.size != 0) {
                        loadChart(it)
                    } else {
                        loadEmpty(it)
                    }
                }


            }
        }

    }

    fun setResourceString()
    {
        headingTextView?.text=getResourceString("cancel")
        dayTextView?.text=getResourceString("d_age")
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }


    private fun setData(chartDetails: Model.NewChart) {
        addEntry(chartDetails, 0.0f)
    }


    fun loadEmpty(view: View) {
        lineChart = view.findViewById(R.id.lineChart)
        customLegend()
        lineChart?.legend?.isEnabled = false
        // loadXaxies()
        // loadRightAxie()
        //loadLeftAxies()
        // setData(item, postion)
        lineChart?.isClickable = false
        lineChart?.isDoubleTapToZoomEnabled = false
        lineChart?.invalidate()
        lineChart?.setTouchEnabled(true)
        lineChart?.notifyDataSetChanged()
        //lineChart?.setVisibleXRangeMaximum(15f)
        //    lineChart?.setDrawUnitsInChart(true);
        lineChart?.getDescription()?.setEnabled(false);
        val xLabels = lineChart?.getLabelFor()

        // xLabels.setPosition(XLabelPosition.BOTTOM)
        //xLabels.setAvoidFirstLastClipping(true)
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        lineChart?.getLabelFor()


        //lineChart.setDrawYLabels(true
        lineChart?.moveViewToX(0f)

        lineChart?.setHighlightPerTapEnabled(false)
        lineChart?.description?.isEnabled = false
        lineChart?.isScrollContainer = false
        lineChart?.getData()?.setHighlightEnabled(false);
        lineChart?.isHorizontalScrollBarEnabled = true
        lineChart?.isScaleXEnabled = true
        lineChart?.invalidate()
        lineChart?.setVisibleYRangeMaximum(50f, YAxis.AxisDependency.RIGHT)


        lineChart?.fitScreen()
        lineChart?.data?.clearValues()
        lineChart?.xAxis?.valueFormatter = null
        lineChart?.notifyDataSetChanged()
        lineChart?.clear()
        lineChart?.invalidate()
    }


    private fun combinedValue(chartDetails: Model.NewChart): ArrayList<ILineDataSet> {
        val lines = ArrayList<LineDataSet>()

        val dataSets = ArrayList<ILineDataSet>()

        enableYAxies()

        if (chartDetails.combinedChart?.size != 0) {
            var data = lineChart?.getData();
            chartDetails.combinedChart?.forEachIndexed { index, chartBatchList ->
                val newEntry = ArrayList<Entry>()
                val list = ArrayList<Model.ChartDetailList>()

                chartBatchList.valueList?.let { list.addAll(it) }
                list.sortBy { it.age }

                list.forEach {
                    newEntry.add(Entry(it.age?.toFloat() ?: 0f, it.value?.toFloat() ?: 0f))
                }
                var color = R.color.redShade;
                if (index == 0) {
                    color = R.color.morality_color;
                } else if (index == 1) {
                    color = R.color.body_weight_color;
                } else if ((index == 2)) {
                    color = R.color.black;
                } else {
                    color = R.color.redShade;
                }
                if (chartDetails.combinedChart?.get(index)?.key?.equals("0")!!) {
                    color = R.color.black;
                }


                val newSet = chartBatchList.key?.let { createSet(newEntry, R.color.redShade, color, "", true) };
                newSet?.let { dataSets.add(it) }

            }
        }
        return dataSets
    }


    fun addEntry(chartDetails: Model.NewChart, base: Float, position: Int? = null) {

        val lines = ArrayList<LineDataSet>()
        val dataSets = ArrayList<ILineDataSet>()
        if (chartDetails.value?.size != 0) {
            var data = lineChart?.getData();
            chartDetails.value?.forEachIndexed { index, chartBatchList ->
                val newEntry = ArrayList<Entry>()
                var list = ArrayList<Model.ChartDetailList>()
                chartBatchList.valueList?.let { list.addAll(it) }
                list.sortBy { it.age }

                list.forEach {
                    newEntry.add(Entry(it.age?.toFloat() ?: 0f, it.value?.toFloat() ?: 0f))
                }
                var color = R.color.redShade;
                if (index == 0) {
                    color = R.color.morality_color;
                } else if (index == 1) {
                    color = R.color.body_weight_color;
                } else if ((index == 2)) {
                    color = R.color.black;
                } else {
                    color = R.color.redShade;
                }
                if (chartDetails?.value?.get(index)?.key?.equals("0")!!) {
                    color = R.color.black;
                }
                val newSet = chartBatchList.key?.let { createSet(newEntry, R.color.redShade, color, it, false) };
                newSet?.let { dataSets.add(it) }
            }
            if (chartDetails.combinedChart != null) {
                dataSets.addAll(combinedValue(chartDetails))
            }
            val data1 = LineData(dataSets)
            lineChart?.setData(data1)
            lineChart?.computeScroll()
            lineChart?.getData()?.notifyDataChanged();
            lineChart?.notifyDataSetChanged();
            lineChart?.invalidate();
            lineChart?.setScaleEnabled(false)
            lineChart?.setPinchZoom(false)
            lineChart?.setTouchEnabled(true);

            // limit the number of visible entries
            lineChart?.setVisibleXRangeMaximum(15f);
            lineChart?.isHorizontalScrollBarEnabled = true
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart?.moveViewToX(base);
        }
    }

    fun createSet(entryList: java.util.ArrayList<Entry>, circleColor: Int, lineColor: Int, batchId: String, boolean: Boolean): LineDataSet? {
        val set = LineDataSet(entryList, batchId)
        if (entryList.size != 0) {
            set.axisDependency = YAxis.AxisDependency.LEFT
            set.setCircleColor(ContextCompat.getColor(applicationContext, lineColor))
            set.color = ContextCompat.getColor(applicationContext, lineColor)
            if (boolean) {
                set.setCircleColorHole(lineColor)
                set.circleHoleRadius = 1f
                set.axisDependency = YAxis.AxisDependency.RIGHT
                set.enableDashedLine(7.5f, 5.5f, 2.6f)
            }
            set.lineWidth = 2f
            set.circleRadius = 3f
            set.fillAlpha = 65
            set.fillColor = ContextCompat.getColor(applicationContext, lineColor)
            set.highLightColor = Color.rgb(244, 117, 117)
            set.valueTextColor = Color.GREEN
            set.valueTextSize = 9f
            set.setDrawValues(false)
        }
        return set
    }


    fun loadLeftAxies() {
        var maxRightAxis: Double? = 0.0
        var minRightAxis: Double? = 0.0
        var max: Long? = 0
        var min: Double? = 0.0
        val item = chartDetail
        item?.value?.forEach {
            min = item.value?.get(0)?.valueList?.minBy { it.value?.toDouble() ?: 0.0 }?.value



            if (maxRightAxis ?: 0.0 < it.valueList?.maxBy { it.value ?: 0.0 }?.value ?: 0.0) {
                maxRightAxis = it.valueList?.maxBy { it.value ?: 0.0 }?.value
            }
            if (minRightAxis ?: 0.0 > it.valueList?.minBy { it.value ?: 0.0 }?.value ?: 0.0) {
                minRightAxis = it.valueList?.minBy { it.value ?: 0.0 }?.value
            }
        }
        val leftAxis = lineChart?.getAxisLeft()
        leftAxis?.setTextSize(12f);
        leftAxis?.setTextColor(resources.getColor(R.color.day_range))
        maxRightAxis?.let {
            leftAxis?.setAxisMaximum((it).toFloat())
        }
        minRightAxis?.toFloat()?.let { leftAxis?.setAxisMinimum(it) }
        leftAxis?.setAxisLineColor(resources.getColor(R.color.view_shade1));
        leftAxis?.setDrawGridLines(false)
        leftAxis?.setGranularityEnabled(false)

        item?.key?.let {
            if (it.contains("&")) {
                if (item.keyValue == GMKeys.bodyWeight || item.keyValue == GMKeys.dayGain) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, applicationContext)}${getResourceString("label_g")}"
                        }
                    }
                }  else if (item.keyValue == GMKeys.feedintake || item.keyValue == GMKeys.mortality){
                leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, applicationContext)} %"
                        }
                    }
                }
                else if (item.keyValue == GMKeys.fcr) {
                    leftAxis?.setLabelCount(10, true)
                    leftAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return String.format("%.2f", value)
                        }
                    }
                }
            } else {
                if (item?.keyValue == GMKeys.bodyWeight || item?.keyValue == GMKeys.dayGain) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, applicationContext)}${getResourceString("label_g")}"
                        }
                    }
                } else if (item.keyValue == GMKeys.feedintake || item.keyValue == GMKeys.mortality){
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, applicationContext)} %"
                        }
                    }
                }
                else if (item.keyValue == GMKeys.fcr) {
                    leftAxis?.setLabelCount(10, true)
                    leftAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return String.format("%.2f", value)
                        }
                    }
                }
            }
        }
    }


    fun loadChart(lineChart1: LineChart) {
        val item = chartDetail
        lineChart = lineChart1
        lineChart?.legend?.isEnabled = false
        customLegend()
        loadXaxies()
        loadRightAxie()
        loadLeftAxies()
        // To set Right yAxis of Line Chart
        lineChart?.isClickable = true
        item?.let { setData(it) }
        lineChart?.invalidate()
        lineChart?.setTouchEnabled(true)
        lineChart?.notifyDataSetChanged()
        lineChart?.setVisibleXRangeMaximum(15f)
        lineChart?.getData()?.setHighlightEnabled(false);
        lineChart?.getDescription()?.setEnabled(false);
        lineChart?.isHighlightPerTapEnabled = false
        lineChart?.isHorizontalFadingEdgeEnabled = false
        //// lineChart?.moveViewToX(0f)
        lineChart?.setHighlightPerTapEnabled(false)
        lineChart?.isDoubleTapToZoomEnabled = false
        lineChart?.setHighlightPerTapEnabled(false)
        lineChart?.isHorizontalScrollBarEnabled = true
    }


    fun customLegend(): ArrayList<LegendEntry> {
        val listLegend = ArrayList<LegendEntry>()
        val item = chartDetail
        val colourList = resources.getIntArray(R.array.colour)
        chartLabelLayout?.removeAllViews()
        chartTypeLayout?.removeAllViews()

        if (item?.combinedChart != null) {
            item.combinedChart?.forEachIndexed { index, chartBatchList ->
                val newLegend = LegendEntry()
                newLegend.label = DateUtils.toDisplayDate(chartBatchList.hatchdate)
                newLegend.formColor = colourList.get(index)
                if (chartBatchList.key.equals("0")) {
                    newLegend.label = getResourceString("label_standard")
                    newLegend.formColor = Color.BLACK
                }
                newLegend.form = Legend.LegendForm.SQUARE
                val chartLabelLegent = LayoutInflater.from(this).inflate(R.layout.chart_legent_color_label, null, false)
                chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                chartLabelLayout?.addView(chartLabelLegent)
                // listLegend.add(newLegend)
            }
            if (item.key?.contains("&")!!) {
                val list = item.key?.split("&")
                val chartTypeLegent = LayoutInflater.from(this).inflate(R.layout.chart_type_legend, null, false)
                chartTypeLegent?.findViewById<View>(R.id.typeBackgroundView)?.background = ContextCompat.getDrawable(this, R.drawable.horizontal_line)
                chartTypeLegent?.findViewById<TextView>(R.id.chartTypeTextView)?.text = list?.getOrNull(0)
                        ?: ""
                chartTypeLayout?.addView(chartTypeLegent)
                val chartTypeLegent2 = LayoutInflater.from(this).inflate(R.layout.chart_type_legend, null, false)
                chartTypeLegent2?.findViewById<View>(R.id.typeBackgroundView)?.background = ContextCompat.getDrawable(this, R.drawable.horizontal_dash_line)
                chartTypeLegent2?.findViewById<TextView>(R.id.chartTypeTextView)?.text = list?.getOrNull(1)
                        ?: ""
                chartTypeLayout?.addView(chartTypeLegent2)
                item.value?.forEachIndexed { index, chartBatchList ->
                    val newLegend = LegendEntry()
                    newLegend.formColor = colourList.get(index)
                    if (chartBatchList.key.equals("0")) {
                        newLegend.label = getResourceString("label_standard")
                        newLegend.formColor = Color.BLACK
                        val chartLabelLegent = LayoutInflater.from(this).inflate(R.layout.chart_legent_color_label, null, false)
                        chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                        chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                        chartTypeLayout?.addView(chartLabelLegent)

                    }
                }

            }

        } else {
            item?.value?.forEachIndexed { index, chartBatchList ->
                val newLegend = LegendEntry()
                newLegend.form = Legend.LegendForm.SQUARE
                newLegend.label = DateUtils.toDisplayDate(chartBatchList.hatchdate)
                newLegend.formColor = colourList.get(index)
                if (chartBatchList.key.equals("0")) {
                    newLegend.label = getResourceString("label_standard")
                    newLegend.formColor = Color.BLACK
                }
                val chartLabelLegent = LayoutInflater.from(this).inflate(R.layout.chart_legent_color_label, null, false)
                chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                chartLabelLayout?.addView(chartLabelLegent)
                // listLegend.add(newLegend)
            }
        }
        return listLegend
    }


    private fun setLanguagePreference() {
        if (AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId)!!.toInt() != 0) {
            var it = AppPreferences.getInstance()?.getLongSharedPreference(GMKeys.languageId)!!.toInt()
            var languageToLoad = IntentUtils.assignLanguage(it!!.toInt())
            // your language
            LocaleHelper.setLocale(applicationContext, languageToLoad)
        } else {
            var languageToLoad = "en"
            // your language
            LocaleHelper.setLocale(applicationContext, languageToLoad)
        }
    }


    fun enableYAxies() {
        lineChart?.getAxisRight()?.setEnabled(true)
    }

    fun loadRightAxie() {
        var maxRightAxis: Double? = 0.0
        var minRightAxis: Double? = 0.0
        val item = chartDetail
        item?.combinedChart?.forEach {
            min = item?.value?.get(0)?.valueList?.minBy { it.value ?: 0.0 }?.value
            if (maxRightAxis ?: 0.0 < it.valueList?.maxBy { it.value ?: 0.0 }?.value ?: 0.0) {
                maxRightAxis = it.valueList?.maxBy { it.value ?: 0.0 }?.value
            }

            if (minRightAxis ?: 0.0 > it.valueList?.minBy { it.value ?: 0.0 }?.value ?: 0.0) {
                minRightAxis = it.valueList?.minBy { it.value ?: 0.0 }?.value
            }
        }
        val rightAxis = lineChart?.getAxisRight()
        if (item?.keyValue2 == GMKeys.bodyWeight || item?.keyValue2 == GMKeys.dayGain) {
            rightAxis?.valueFormatter = object : LargeValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return "${DateUtils.roundOFF(value, applicationContext)}${ getResourceString("label_g")}"
                }
            }
        } else if (item?.keyValue2 == GMKeys.feedintake || item?.keyValue2 == GMKeys.mortality) {
            rightAxis?.valueFormatter = object : LargeValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return "${DateUtils.roundOFF(value, applicationContext)} %"
                }
            }
        }
        else if (item?.keyValue2 == GMKeys.fcr) {
            rightAxis?.setLabelCount(10, true)
            rightAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return String.format("%.2f", value)
                }
            }
        }

        //  val minValue: Model.ChartDetailList? = item.combinedValue?.minBy { it.value ?: 0 }
        //  val maxValue: Model.ChartDetailList? = item.combinedValue?.maxBy { it.value ?: 0 }

        rightAxis?.setEnabled(false)
        rightAxis?.setTextColor(Color.RED)
        maxRightAxis?.let {

            rightAxis?.setAxisMaximum((it.toFloat()))
        }
        minRightAxis?.toFloat()?.let { rightAxis?.setAxisMinimum(it) }
        rightAxis?.setDrawGridLines(false)
        rightAxis?.setAxisLineColor(resources.getColor(R.color.view_shade1));
        rightAxis?.setDrawZeroLine(false)
        rightAxis?.setGranularityEnabled(false)
        rightAxis?.setTextSize(12f);
        rightAxis?.setTextColor(resources.getColor(R.color.day_range))

    }

    fun loadXaxies() {
        var min: Long? = 0
        var max: Long? = 0
        val item = chartDetail
        item?.value?.forEach {
            min = item?.value?.get(0)?.valueList?.minBy { it.age ?: 0 }?.age
            val maxXAxis: Model.ChartDetailList? = it.valueList?.maxBy { it.age ?: 0 }
            val minXAxis: Model.ChartDetailList? = it.valueList?.minBy { it.age ?: 0 }
            if (max ?: 0 < it.valueList?.maxBy { it.age ?: 0 }?.age ?: 0) {
                max = it.valueList?.maxBy { it.age ?: 0 }?.age
            }
            if (min ?: 0 > it.valueList?.minBy { it.age ?: 0 }?.age ?: 0) {
                min = it.valueList?.minBy { it.age ?: 0 }?.age
            }
        }

        val xAxis = lineChart?.xAxis
        xAxis?.setTextSize(11f)

        xAxis?.setPosition(XAxis.XAxisPosition.BOTTOM);
        maxAgeValue = max
        max?.toFloat()?.let { xAxis?.setAxisMaximum(it) }
        max?.toInt()?.let { xAxis?.setLabelCount(it) }
        min?.toFloat()?.let { xAxis?.setAxisMinimum(it) }

        xAxis?.setTextSize(12f);
        xAxis?.setTextColor(resources.getColor(R.color.day_range))
        xAxis?.setDrawAxisLine(true)
        xAxis?.setAxisLineColor(resources.getColor(R.color.view_shade1));
        xAxis?.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis?.setDrawGridLines(false)
        xAxis?.setGranularity(1f) // only intervals of 1 day
        xAxis?.isGranularityEnabled = true
    }
}

