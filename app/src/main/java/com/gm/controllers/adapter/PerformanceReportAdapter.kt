package com.gm.controllers.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.gm.R
import com.gm.controllers.fragments.PerformanceReportFragment
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_performance_chart.view.*
import java.text.DecimalFormat
import kotlin.collections.maxBy as maxBy1
import kotlin.collections.minBy as minBy1


class PerformanceReportAdapter(var itemList: ArrayList<Model.NewChart>, var resources: Resources, var context: Context, var onItemClickListener: PerformanceReportFragment) : PagingAdapter<RecyclerView.ViewHolder>() {


    var lineChart: LineChart? = null
    var max: Double? = 0.0
    var min: Double? = 0.0
    var maxAgeValue: Long = 0
    var checkData: Boolean = true
    var keySting: String? = null
    override fun onCreateHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_performance_chart, viewGroup, false))
    }

    override fun onBindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ViewHolder) {
            viewHolder.itemView.ageInDaysTextView.text = onItemClickListener.getResourceString("d_age")
            viewHolder.itemView.viewFullChartTextView.text = onItemClickListener.getResourceString("label_view_chart")
            if (itemList.get(position).key?.contains("&")!!) {
                keySting = itemList.get(position).key
            } else {
                when (itemList.get(position).keyValue) {

                    GMKeys.dayGain -> {
                        keySting = onItemClickListener.getResourceString("day_gain")
                    }
                    GMKeys.mortality -> {
                        keySting = onItemClickListener.getResourceString("mortality")
                    }
                    GMKeys.feedintake -> {
                        keySting = onItemClickListener.getResourceString("feed_intake")
                    }
                    GMKeys.bodyWeight -> {
                        keySting = onItemClickListener.getResourceString("body_weight")
                    }
                    GMKeys.fcr -> {
                        keySting = onItemClickListener.getResourceString("fcr")
                    }
                }

            }

            viewHolder.itemView.titleTextView.text = keySting

            when (itemList.get(position).keyValue) {
                GMKeys.fcr -> {
                    keySting = onItemClickListener.getResourceString("fcr_ratio")
                }
            }



            viewHolder.itemView.fullViewLayout?.setOnClickListener {
                onItemClickListener.onItemSelected(itemList.get(position), position)
            }
            if (itemList.get(position).key?.contains("&")?:false) {
                viewHolder.itemView.rightLableNameTextView.visibility = View.VISIBLE
                val list = itemList.get(position).fcrkey?.split("&")
                viewHolder.itemView.rightLableNameTextView.text = list?.get(1)
                viewHolder.itemView.leftLableNameTextView.text = list?.get(0)
            } else {
                viewHolder.itemView.rightLableNameTextView.visibility = View.GONE
                viewHolder.itemView.leftLableNameTextView.text = keySting


                var chartItem = 0
                viewHolder.itemView.lineChart?.setVisibleXRangeMaximum(15f)
                viewHolder.itemView.rightScroll.setOnClickListener {
                    if (chartItem < maxAgeValue) {
                        viewHolder.itemView.rightScroll.visibility = View.VISIBLE
                        chartItem = chartItem + GMKeys.ITEM_IN_CHART
                        viewHolder.itemView.lineChart?.moveViewToX(chartItem.toFloat())
                        viewHolder.itemView.leftScroll.visibility = View.VISIBLE


                    } else {
                        viewHolder.itemView.rightScroll.visibility = View.INVISIBLE
                    }
                }
                viewHolder.itemView.leftScroll.setOnClickListener {
                    if (chartItem > 0) {
                        viewHolder.itemView.leftScroll.visibility = View.VISIBLE
                        chartItem = chartItem - GMKeys.ITEM_IN_CHART
                        viewHolder.itemView.rightScroll.visibility = View.VISIBLE
                        viewHolder.itemView.lineChart?.moveViewToX(chartItem.toFloat())
                    } else {
                        viewHolder.itemView.leftScroll?.visibility = View.INVISIBLE
                        viewHolder.itemView.rightScroll?.visibility = View.VISIBLE
                    }
                }
            }
            if (itemList.get(position).keyValue == GMKeys.bodyWeight || itemList.get(position).keyValue === GMKeys.dayGain) {
                if ((itemList.get(position).keyValue == GMKeys.bodyWeight || itemList.get(position).keyValue === GMKeys.dayGain) && itemList.get(position).value?.size != 1) {
                    loadChart(viewHolder.itemView, position)
                } else {
                    loadEmpty(viewHolder.itemView, position)
                }
            } else {
                if (itemList.get(position).value?.size != 0) {
                    loadChart(viewHolder.itemView, position)
                } else {
                    loadEmpty(viewHolder.itemView, position)
                }
            }

            viewHolder.itemView.headerLayout?.setOnClickListener {
                if (viewHolder.itemView.chartLayout?.visibility == View.GONE) {
                    viewHolder.itemView.expandImageView.rotation = 90f
                    viewHolder.itemView.chartLayout?.visibility = View.VISIBLE
                    //    if (checkFullViewVisibility(position))
                    viewHolder.itemView.fullViewLayout?.visibility = View.VISIBLE

                } else {
                    viewHolder.itemView.expandImageView.rotation = 0f
                    viewHolder.itemView.chartLayout?.visibility = View.GONE
                    viewHolder.itemView.fullViewLayout?.visibility = View.GONE
                }
            }

        }
    }

    override fun getCount(): Int {
        return itemList.size
    }

    fun checkFullViewVisibility(position: Int): Boolean {
        var checkEmpty: Boolean? = false

        if (itemList.get(position).keyValue == GMKeys.bodyWeight || itemList.get(position).keyValue == GMKeys.dayGain) {
            checkEmpty = (itemList.get(position).keyValue == GMKeys.bodyWeight || itemList.get(position).keyValue == GMKeys.dayGain) && itemList.get(position).value?.size != 1
        } else {
            checkEmpty = itemList.get(position).value?.size != 0
        }
        if (itemList.get(position)?.combinedChart != null) {
            checkEmpty = true
        }
        return checkEmpty
    }

    override fun setEmptyViewText(textView: TextView) {

    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.titleTextView
            itemView.leftScroll
            itemView.rightScroll
            itemView.rightLableNameTextView
            itemView.leftLableNameTextView
            itemView.headerLayout
            itemView.chartLayout
            itemView.expandImageView
            itemView.fullViewLayout
            itemView.ageInDaysTextView
            itemView.viewFullChartTextView
        }
    }


    private fun setData(chartDetails: Model.NewChart, position: Int) {
        addEntry(chartDetails, 0.0f, position)

    }


    private fun combinedValue(chartDetails: Model.NewChart, position: Int): ArrayList<ILineDataSet> {
        val dataSets = ArrayList<ILineDataSet>()
        enableYAxies()
        if (chartDetails.combinedChart?.size != 0) {
            var data = lineChart?.getData();
            chartDetails.combinedChart?.forEachIndexed { index, chartBatchList ->
                val newEntry = ArrayList<Entry>()
                val list = ArrayList<Model.ChartDetailList>()
                if (chartBatchList.valueList?.size != 0) {
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
        }
        return dataSets
    }


    fun addEntry(chartDetails: Model.NewChart, base: Float, position: Int) {
        val lines = ArrayList<LineDataSet>()
        val dataSets = ArrayList<ILineDataSet>()
        //  chart(chartDetails,position)
        if (chartDetails.value?.size != 0) {
            var data = lineChart?.getData();
            chartDetails.value?.forEachIndexed { index, chartBatchList ->
                val newEntry = ArrayList<Entry>()
                var list = ArrayList<Model.ChartDetailList>()
                if (chartBatchList.valueList?.size != 0) {
                    chartBatchList.valueList?.let { list.addAll(it) }
                    list.sortBy { it.age }
                    list.forEach {
                        newEntry.add(Entry(it.age?.toFloat() ?: 0f, it.value?.toFloat() ?: 0f))
                    }


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
                if (chartDetails?.value?.size != 0)
                    if (chartDetails?.value?.get(index)?.key?.equals("0")!!) {
                        color = R.color.black;
                    }
                val newSet = chartBatchList.key?.let { createSet(newEntry, R.color.redShade, color, it, false) };
                newSet?.let { dataSets.add(it) }
            }
            if (chartDetails.combinedChart != null) {
                dataSets.addAll(combinedValue(chartDetails, position))
            }
            val data1 = LineData(dataSets)
            lineChart?.setData(data1)
            //  lineChart?.computeScroll()
            lineChart?.getData()?.notifyDataChanged();
            lineChart?.notifyDataSetChanged();
            /*Disable the zoom option */
            lineChart?.setScaleEnabled(false)
            lineChart?.setPinchZoom(false)
            lineChart?.invalidate();
            // move to the latest entry
            lineChart?.moveViewToX(base)
            lineChart?.setTouchEnabled(true);

        }
    }


    fun createSet(entryList: java.util.ArrayList<Entry>, circleColor: Int, lineColor: Int, batchId: String, boolean: Boolean): LineDataSet? {
        val set = LineDataSet(entryList, batchId)
        if (entryList.size != 0) {
            set.axisDependency = YAxis.AxisDependency.LEFT
            set.setCircleColor(ContextCompat.getColor(context, lineColor))
            set.color = ContextCompat.getColor(context, lineColor)
            if (boolean) {
                set.setCircleColorHole(lineColor)
                set.circleHoleRadius = 1f
                set.axisDependency = YAxis.AxisDependency.RIGHT
                set.enableDashedLine(7.5f, 5.5f, 2.6f)
            }
            set.lineWidth = 2f
            set.circleRadius = 3f
            set.fillAlpha = 65
            set.fillColor = ContextCompat.getColor(context, lineColor)
            set.highLightColor = Color.rgb(244, 117, 117)
            set.valueTextColor = Color.GREEN
            set.valueTextSize = 9f
            set.setDrawValues(false)
        }
        return set
    }


    fun loadLeftAxies(position: Int) {
        var maxRightAxis: Double? = 0.0
        var minRightAxis: Double? = 0.0
        var max: Long? = 0
        var min: Double? = 0.0
        val item = itemList.get(position)
        item.value?.forEach {
            if (item.value?.get(0)?.valueList?.size != 0) {
                min = item.value?.get(0)?.valueList?.minBy1 { it.value?.toDouble() ?: 0.0 }?.value
                maxRightAxis = item.value?.get(0)?.valueList?.maxBy1 {
                    it.value?.toDouble() ?: 0.0
                }?.value
            }

            if (it.valueList?.size != 0) {
                if (maxRightAxis ?: 0.0 < it.valueList?.maxBy1 { it.value ?: 0.0 }?.value ?: 0.0) {
                    maxRightAxis = it.valueList?.maxBy1 { it.value ?: 0.0 }?.value
                }
                if (min ?: 0.0 > it.valueList?.minBy1 { it.value ?: 0.0 }?.value ?: 0.0) {
                    minRightAxis = it.valueList?.minBy1 { it.value ?: 0.0 }?.value
                }

            }

        }

        val leftAxis = lineChart?.getAxisLeft()
        item.key?.let { Log.e("key", it) }

        item.key?.let {
            if (it.contains("&")) {
                if (item.keyValue == GMKeys.bodyWeight || item.keyValue == GMKeys.dayGain) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, context)}${onItemClickListener.getResourceString("label_g")}"
                        }
                    }
                } else if (item.keyValue == GMKeys.feedintake || item.keyValue == GMKeys.mortality) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, context)} %"
                        }
                    }
                }else if (item.keyValue == GMKeys.fcr) {
                    leftAxis?.setLabelCount(10, true)
                    leftAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return String.format("%.2f", value)
                        }
                    }
                }
            } else {
                if (item.keyValue == GMKeys.bodyWeight || item.keyValue == GMKeys.dayGain) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, context)}${onItemClickListener.getResourceString("label_g")}"
                        }
                    }
                } else if (item.keyValue == GMKeys.feedintake || item.keyValue == GMKeys.mortality) {
                    leftAxis?.valueFormatter = object : LargeValueFormatter() {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return "${DateUtils.roundOFF(value, context)} %"
                        }
                    }
                } else if (item.keyValue == GMKeys.fcr) {
                    leftAxis?.setLabelCount(10, true)
                    leftAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                            return String.format("%.2f", value)
                        }
                    }
                }
            }
        }




        leftAxis?.setTextSize(12f);
        leftAxis?.setTextColor(resources.getColor(R.color.day_range))
        maxRightAxis?.let {

            leftAxis?.setAxisMaximum((it).toFloat())
            Log.e("LmaxRange", (it).toString())
        }
        minRightAxis?.toFloat()?.let {
            // leftAxis?.setAxisMinimum(0.8f)
            leftAxis?.setAxisMinimum(it)
            Log.e("LminRange", (it).toString())
        }
        leftAxis?.setAxisLineColor(resources.getColor(R.color.view_shade1));
        leftAxis?.setDrawGridLines(false)
      //  leftAxis?.setGranularity(0.1f)
         leftAxis?.setGranularityEnabled(false)
    }


    fun customLegend(view: View, position: Int): ArrayList<LegendEntry> {

        val listLegend = ArrayList<LegendEntry>()

        val item = itemList.get(position)
        val colourList = resources.getIntArray(R.array.colour)

        view.findViewById<LinearLayout>(R.id.chartLabelLayout)?.removeAllViews()
        view.findViewById<LinearLayout>(R.id.chartTypeLayout)?.removeAllViews()

        if (item.combinedChart != null) {
            item.combinedChart?.forEachIndexed { index, chartBatchList ->
                val newLegend = LegendEntry()
                newLegend.label = DateUtils.toDisplayDate(chartBatchList.hatchdate)
                newLegend.formColor = colourList.get(index)
                if (chartBatchList.key.equals("0")) {
                    newLegend.label = onItemClickListener.getResourceString("label_standard")
                    newLegend.formColor = Color.BLACK
                }
                newLegend.form = Legend.LegendForm.SQUARE
                val chartLabelLegent = LayoutInflater.from(view.context).inflate(R.layout.chart_legent_color_label, null, false)
                chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                view.findViewById<LinearLayout>(R.id.chartLabelLayout)?.addView(chartLabelLegent)
                // listLegend.add(newLegend)
            }
            if (item.key?.contains("&")!!) {
                val list = item.key?.split("&")

                val chartTypeLegent = LayoutInflater.from(view.context).inflate(R.layout.chart_type_legend, null, false)
                chartTypeLegent?.findViewById<View>(R.id.typeBackgroundView)?.background = ContextCompat.getDrawable(view.context, R.drawable.horizontal_line)
                chartTypeLegent?.findViewById<TextView>(R.id.chartTypeTextView)?.text = list?.getOrNull(0)
                        ?: ""
                view.findViewById<LinearLayout>(R.id.chartTypeLayout)?.addView(chartTypeLegent)
                val chartTypeLegent2 = LayoutInflater.from(view.context).inflate(R.layout.chart_type_legend, null, false)
                chartTypeLegent2?.findViewById<View>(R.id.typeBackgroundView)?.background = ContextCompat.getDrawable(view.context, R.drawable.horizontal_dash_line)
                chartTypeLegent2?.findViewById<TextView>(R.id.chartTypeTextView)?.text = list?.getOrNull(1)
                        ?: ""
                view.findViewById<LinearLayout>(R.id.chartTypeLayout)?.addView(chartTypeLegent2)
                item.value?.forEachIndexed { index, chartBatchList ->
                    val newLegend = LegendEntry()
                    newLegend.formColor = colourList.get(index)
                    if (chartBatchList.key.equals("0")) {
                        newLegend.label = onItemClickListener.getResourceString("label_standard")
                        newLegend.formColor = Color.BLACK
                        val chartLabelLegent = LayoutInflater.from(view.context).inflate(R.layout.chart_legent_color_label, null, false)
                        chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                        chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                        view.findViewById<LinearLayout>(R.id.chartTypeLayout)?.addView(chartLabelLegent)

                    }
                }

            }

        } else {
            item.value?.forEachIndexed { index, chartBatchList ->
                val newLegend = LegendEntry()
                newLegend.form = Legend.LegendForm.SQUARE
                newLegend.label = DateUtils.toDisplayDate(chartBatchList.hatchdate)
                newLegend.formColor = colourList.get(index)
                if (chartBatchList.key.equals("0")) {
                    newLegend.label = onItemClickListener.getResourceString("label_standard")
                    newLegend.formColor = Color.BLACK
                }
                val chartLabelLegent = LayoutInflater.from(view.context).inflate(R.layout.chart_legent_color_label, null, false)
                chartLabelLegent?.findViewById<View>(R.id.colorView)?.setBackgroundColor(newLegend.formColor)
                chartLabelLegent?.findViewById<TextView>(R.id.labelTextView)?.text = newLegend.label
                view.findViewById<LinearLayout>(R.id.chartLabelLayout)?.addView(chartLabelLegent)
                // listLegend.add(newLegend)
            }
        }
        return listLegend
    }


    fun loadChart(view: View, postion: Int) {
        val item = itemList.get(postion)

        lineChart = view.findViewById(R.id.lineChart)
        customLegend(view, postion)
        lineChart?.legend?.isEnabled = false
        loadXaxies(postion)
        loadRightAxie(postion)
        loadLeftAxies(postion)
        setData(item, postion)
        lineChart?.isClickable = false
        lineChart?.isDoubleTapToZoomEnabled = false
        lineChart?.invalidate()
        lineChart?.setTouchEnabled(true)
        lineChart?.notifyDataSetChanged()
        lineChart?.setVisibleXRangeMaximum(15f)
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
        lineChart?.lineChart?.isHorizontalScrollBarEnabled = true
        lineChart?.isScaleXEnabled = true
        lineChart?.invalidate()
        lineChart?.setVisibleYRangeMaximum(50f, YAxis.AxisDependency.RIGHT)
    }


    fun loadEmpty(view: View, postion: Int) {

        val item = itemList.get(postion)

        lineChart = view.findViewById(R.id.lineChart)
        customLegend(view, postion)
        lineChart?.legend?.isEnabled = false
        loadXaxies(postion)
        loadRightAxie(postion)
        loadLeftAxies(postion)
        // setData(item, postion)
        lineChart?.isClickable = false
        lineChart?.isDoubleTapToZoomEnabled = false
        lineChart?.invalidate()
        lineChart?.setTouchEnabled(true)
        lineChart?.notifyDataSetChanged()
        //lineChart?.setVisibleXRangeMaximum(15f)
        //    lineChart?.setDrawUnitsInChart(true);
        lineChart?.getDescription()?.setEnabled(false);

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


    fun enableYAxies() {
        lineChart?.getAxisRight()?.setEnabled(true)
    }

    fun loadRightAxie(position: Int) {
        var maxRightAxis: Double? = 0.0
        var minRightAxis: Double? = 0.0
        val item = itemList.get(position)
        item.combinedChart?.forEach {
            if (item.value?.get(0)?.valueList?.size != 0) {
                min = item.value?.get(0)?.valueList?.minBy1 { it.value ?: 0.0 }?.value
                if (maxRightAxis ?: 0.0 < it.valueList?.maxBy1 { it.value ?: 0.0 }?.value ?: 0.0) {
                    maxRightAxis = it.valueList?.maxBy1 { it.value ?: 0.0 }?.value
                }
            }

            if (it.valueList?.size != 0) {
                if (minRightAxis ?: 0.0 > it.valueList?.minBy1 { it.value ?: 0.0 }?.value ?: 0.0) {
                    minRightAxis = it.valueList?.minBy1 { it.value ?: 0.0 }?.value
                }
            }

        }

        val rightAxis = lineChart?.getAxisRight()


        if (item.keyValue2 == GMKeys.bodyWeight || item.keyValue2 == GMKeys.dayGain) {
            rightAxis?.valueFormatter = object : LargeValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return "${DateUtils.roundOFF(value, context)}${onItemClickListener.getResourceString("label_g")}"
                }
            }
        } else if (item.keyValue == GMKeys.feedintake || item.keyValue == GMKeys.mortality) {
            rightAxis?.valueFormatter = object : LargeValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return "${DateUtils.roundOFF(value, context)} %"
                }
            }
        } else if (item.keyValue == GMKeys.fcr) {
            rightAxis?.valueFormatter = object : DecimalFormat("###,###,##0.0"), IAxisValueFormatter {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    return String.format("%.2f", value)
                }
            }
        }
        rightAxis?.setEnabled(false)
        rightAxis?.setTextColor(Color.RED)
        maxRightAxis?.let {

            rightAxis?.setAxisMaximum((it.toFloat()))
            Log.e("RmaxRange", (it).toString())
        }
        minRightAxis?.toFloat()?.let {
            rightAxis?.setAxisMinimum(it)
            Log.e("RminRange", (it).toString())
        }
        rightAxis?.setDrawGridLines(false)
        rightAxis?.axisLineColor = resources.getColor(R.color.view_shade1);
        rightAxis?.setDrawZeroLine(false)
        // rightAxis?.setLabelCount(9)

        rightAxis?.setTextSize(12f);
        rightAxis?.setTextColor(resources.getColor(R.color.day_range))
        rightAxis?.isGranularityEnabled=false
        /*if (item.combinedValue != null) {
        enableYAxies()
    }*/
    }

    fun loadXaxies(position: Int) {
        var minXAxis1: Long? = 0
        var maxXAxis1: Long? = 0
        val item = itemList.get(position)
        item.value?.forEach {
            if (item?.value?.get(0)?.valueList?.size != 0) {
                minXAxis1 = item?.value?.get(0)?.valueList?.minBy1 { it.age ?: 0 }?.age
            }

            if (it.valueList?.size != 0) {
                val maxXAxis: Model.ChartDetailList? = it.valueList?.maxBy1 { it.age ?: 0 }
                // var a= it.valueList?.maxBy1 { it.age ?: 0 }?.age ?: 0

                val minXAxis: Model.ChartDetailList? = it.valueList?.minBy1 { it.age ?: 0 }
                if (maxXAxis1 ?: 0 < it.valueList?.maxBy1 { it.age ?: 0 }?.age ?: 0) {
                    maxXAxis1 = it.valueList?.maxBy1 { it.age ?: 0 }?.age
                }
                if (minXAxis1 ?: 0 > it.valueList?.minBy1 { it.age ?: 0 }?.age ?: 0) {
                    minXAxis1 = it.valueList?.minBy1 { it.age ?: 0 }?.age
                }
            }

        }
        maxAgeValue = maxXAxis1 ?: 0

        val xAxis = lineChart?.xAxis
        xAxis?.valueFormatter = object : LargeValueFormatter() {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                //return value.toString() + "day"
                //return "12" + " day"
                var data = value.toInt()
                return data.toString()
            }
        }
        xAxis?.setTextSize(11f)
        xAxis?.setDrawLabels(true)

        xAxis?.textSize = 11f

        //  xAxis?.setEnabled(false)
        xAxis?.setPosition(XAxis.XAxisPosition.BOTTOM);
        maxXAxis1?.toFloat()?.let { xAxis?.setAxisMaximum(it) }
        maxXAxis1?.toInt()?.let { xAxis?.setLabelCount(it) }
        minXAxis1?.toFloat()?.let { xAxis?.setAxisMinimum(it) }
        xAxis?.textSize = 12f;
        xAxis?.textColor = resources.getColor(R.color.day_range)
        xAxis?.setDrawAxisLine(true)
        xAxis?.axisLineColor = resources.getColor(R.color.view_shade1);
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawGridLines(false)
        xAxis?.granularity = 1f // only intervals of 1 day
        xAxis?.isGranularityEnabled = true

    }


}



