package com.gm.controllers.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.ChartViewActivity
import com.gm.controllers.adapter.PerformanceReportAdapter
import com.gm.listener.OnBookGridTouched
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.alert_dialog_filter.view.*
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.fragment_performance_report.*
import java.util.*
import kotlin.collections.ArrayList


class PerformanceReportFragment : GMBaseFragment(), OnBookGridTouched, OnItemClickListener {


    var parameterList = ArrayList<String>()
    var selectedBatchList = ArrayList<String>()
    var batchIdList = ArrayList<Model.PerformanceReport>()
    var selectedBatchIdList = ArrayList<Long>()
    var chartResponsList = ArrayList<Model.PerformanceChart>()
    var chartList = ArrayList<Model.NewChart>()
    var selectedString: String? = null
    private var navMenu: Menu? = null
    var keyList = ArrayList<Model.KeyModel>()
    var adapter: ArrayAdapter<Model.KeyModel>? = null
    var adapter1: ArrayAdapter<Model.KeyModel>? = null
    var parameter1KeyValue: Long? = null
    var parameter2KeyValue: Long? = null

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

    companion object {
        fun newInstance(args: Bundle): PerformanceReportFragment {
            val fragment = PerformanceReportFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_performance_report, container, false)
    }

    fun setResourceString()
    {
        chickTextView?.text=getResourceString("Chicks")
        mortalityTextView?.text=getResourceString("mortality_count")
        feedintakeTextView?.text=getResourceString("feedintake")
        fcrLabelTextView?.text=getResourceString("fcr")
        daygainTextView?.text=getResourceString("day_gain")
        weightTextView?.text=getResourceString("average_weight")
        meanAgeTextView?.text=getResourceString("mean_age")
        liftedQtyTextView?.text=getResourceString("lifted_quantity")


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        initToolbar(R.menu.menu_fliter, getResourceString("performance_report"))
        batchSpinner?.setSelectedLabel()
        navMenu = toolbar.menu
        navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.invisible)
        addTheSpinnerData()
        getPerformanceReport()

    }


    fun initView(str: CharSequence?) {
        batchIdList.forEachIndexed { index, performanceChart ->
            if (str?.equals(DateUtils.toDisplayDate(performanceChart.hatchDate))!!) {
                selectedString = performanceChart.batchId.toString()
                meanAge?.text = performanceChart.meanAge.toString()
                fcrTextView?.text = performanceChart.FCR.toString()
// countNameTextView?.text = performanceChart.averageChicksHoused.toString()
                chicksCount?.text = performanceChart.chicksHoused.toString()
                liftedQty?.text = performanceChart.liftedQuantityInKG.toString().plus(" ").plus(getResourceString("kg"))
                val avgWeight = performanceChart.averageWeight?.times(100.0)?.let { Math.round(it) }?.div(100.0)
                average_weight?.text = avgWeight.toString().plus(" ").plus(getResourceString("kg"))
                mortality?.text = performanceChart.mortalityPercent.toString().plus("%")
                dayGain?.text = performanceChart.dayGain.toString().plus(" ").plus(getResourceString("grms").toLowerCase(Locale.getDefault()))
                feedIntake?.text = performanceChart.feedIntake.toString().plus("%")
            }
        }
    }

    fun initAdapter(chart: ArrayList<Model.NewChart>) {
        context?.let {
            if (recyclerViewRepository?.adapter == null) {
                val adapter = chart.let { it1 -> PerformanceReportAdapter(it1, resources, context!!, this) }
                adapter.setPagingEnabled(false)
                recyclerViewRepository?.adapter = adapter
            } else {
                recyclerViewRepository?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {

        if (item is Model.NewChart) {
            val intent = Intent(activity, ChartViewActivity::class.java)
            val bundle = Bundle()
            val chartDetails = item as Model.NewChart
            bundle.putSerializable(GMKeys.chartDetails, chartDetails)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    override fun onTouchGrid() {
        tabView()
    }

    fun setListener(batchList: ArrayList<Model.PerformanceReport>) {
        val batchIdList = ArrayList<String>()
        if (batchList.size != 0) {
            batchList.forEach {
                batchIdList.add(DateUtils.toDisplayDate(it.hatchDate))
            }
            batchSpinner?.setItems(batchIdList)
            //   var list=ArrayList<String>()
            //    list.add(batchIdList.get(0))
            //  batchSpinner.setItemsChecked(list)
            batchSpinner.setCallback(this)
        }

    }

    fun addTheSpinnerData() {
        val parametersList = ArrayList<String>()
        parametersList.add(getResourceString("choose"))
        parametersList.add(getResourceString("day_gain"))
        parametersList.add(getResourceString("mortality"))
        parametersList.add(getResourceString("feed_intake"))
        parametersList.add(getResourceString("body_weight"))
        parametersList.add(getResourceString("fcr"))


        parametersList.forEach {
            val spinnerData = Model.KeyModel()
            spinnerData.id = keyList.size.toLong()
            spinnerData.key = it
            keyList.add(spinnerData)
            // parameterList.add(it)
        }


    }


    fun removeEmpty() {
        chartList?.forEachIndexed { index, newChart ->
            if (newChart.keyValue == GMKeys.bodyWeight || newChart.keyValue == GMKeys.dayGain) {
                if ((newChart.keyValue == GMKeys.bodyWeight || newChart.keyValue === GMKeys.dayGain) && newChart.value?.size != 1) {
                    keyList
                } else {
                    val it: MutableIterator<Model.KeyModel> = keyList.iterator()
                    while (it.hasNext()) {
                        val aDrugStrength: Model.KeyModel = it.next()
                        if (aDrugStrength.id?.toInt() == newChart.keyValue) {
                            it.remove()
                        }
                    }
                }
            } else {
                if (newChart.value?.size != 0) {

                } else {
                    val it: MutableIterator<Model.KeyModel> = keyList.iterator()
                    while (it.hasNext()) {
                        val aDrugStrength: Model.KeyModel = it.next()
                        if (aDrugStrength.id?.toInt() == newChart.keyValue) {
                            it.remove()
                        }
                    }
                }
            }


        }
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_fliter) {
            showConfirmationDialog()
        }
        return super.onMenuItemClick(item)
    }

    private var apiHitCount = 0

    private fun startProgress() {
        if (apiHitCount <= 0) {
            showProgressBar()
        }
        apiHitCount++
    }

    private fun checkAPIHit() {
        apiHitCount--
        if (apiHitCount <= 0) {
            dismissProgressBar()
        }
    }


    fun getPerformanceReport() {
        startProgress()
        DataProvider.getPerformanceReport(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.PerformanceReportResponse) {

                    if (responseObject.response?.size != 0) {
                        batchSpinnerLayout?.visibility = View.VISIBLE
                        batchIdList.clear()
                        setListener(responseObject.response as ArrayList<Model.PerformanceReport>)
                        batchIdList.addAll(responseObject.response!!)
                        val list = ArrayList<Long>()
                        tabView()
                        initView(DateUtils.toDisplayDate(responseObject.response?.get(0)?.hatchDate.toString()))

                        batchIdList.get(0).batchId?.let { list.add(it) }
                        getPerformanceChart(list)
                    } else {
                        batchSpinnerLayout?.visibility = View.GONE
                        showSnackBar("no batches found")
                    }
                    checkAPIHit()
                }

            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                checkAPIHit()
            }
        })

    }

    private fun showConfirmationDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.alert_dialog_filter, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.newComparisionTextView?.text=getResourceString("label_new_comparision")
        confirmationDialogView.parameter1TextView?.text=getResourceString("parameter1")
        confirmationDialogView.parameter2TextView?.text=getResourceString("parameter2")
        confirmationDialogView.cancelTextView?.text=getResourceString("cancel")
        confirmationDialogView.confirmTextView?.text=getResourceString("submit")
        confirmationDialogView.cancelTextView.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialogView.closeImageView?.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        loadSpinner(keyList, confirmationDialogView.parameter1Spinner)

        confirmationDialogView.parameter1Spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val list = ArrayList<Model.KeyModel>()
                    list.addAll(keyList)
                    list.removeAt(position)
                    parameter1KeyValue = adapter?.getItem(position)?.id
                    loadSpinner1(list, confirmationDialogView.parameter2Spinner)
                }
            }
        }
        confirmationDialogView.parameter2Spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                parameter2KeyValue = adapter1?.getItem(p2)?.id
            }
        }
        confirmationDialogView.confirmTextView.setOnClickListener {
            if (parameter2KeyValue != 0.toLong()) {
                manipulateCombineChartData(confirmationDialogView.parameter1Spinner.selectedItem.toString(),
                        confirmationDialogView.parameter2Spinner.selectedItem.toString(), confirmationDialog)

            }
        }
        confirmationDialog?.show()
    }

    fun manipulateCombineChartData(parameter1: String, parameter2: String, confirmationDialogView: AlertDialog) {
        var combinedChartString: String? = null
        var keySting: String? = null
        var keyValue: Long? = null
        var isParameter1: Boolean? = false
        var isParameter2: Boolean? = false
        var check: Boolean? = false
        val combinedChart = Model.NewChart()
        chartList.forEachIndexed { index, newChart ->
            when (newChart.keyValue) {

                GMKeys.dayGain -> {
                    keySting = getResourceString("day_gain")
                }
                GMKeys.mortality -> {
                    keySting = getResourceString("mortality")
                }
                GMKeys.feedintake -> {
                    keySting =  getResourceString("feed_intake")
                }
                GMKeys.bodyWeight -> {
                    keySting = getResourceString("body_weight")
                }
                GMKeys.fcr -> {
                    keySting = getResourceString("fcr_ratio")
                }
            }

            if (parameter1KeyValue == newChart.keyValue?.toLong()) {
                if (parameter1KeyValue == GMKeys.mortality.toLong() &&
                        parameter2KeyValue == GMKeys.bodyWeight.toLong()) {
                    combinedChartString = newChart.key
                    combinedChart.combinedChart = newChart.value
                    combinedChart.keyValue2 = newChart.keyValue
                } else {
                    combinedChart.value = newChart.value
                    combinedChart.keyValue = newChart.keyValue

                }

            }
            if (parameter2KeyValue == newChart.keyValue?.toLong()) {
                combinedChartString.plus(" & ").plus(newChart.key)
                if (parameter1KeyValue == GMKeys.mortality.toLong()
                        && parameter2KeyValue == GMKeys.bodyWeight.toLong()) {
                    combinedChart.value = newChart.value
                    combinedChart.keyValue = newChart.keyValue
                } else {
                    combinedChart.combinedChart = newChart.value
                    combinedChart.keyValue2 = newChart.keyValue
                }
            }

        }
        if (parameter1KeyValue == GMKeys.mortality.toLong()
                && parameter2KeyValue == GMKeys.bodyWeight.toLong()) {
            combinedChart.key = parameter2.plus(" & ").plus(parameter1)
            combinedChart.fcrkey= parameter2.plus(" & ").plus(parameter1)
        } else {
            if (parameter1KeyValue?.toInt()==GMKeys.fcr)
            {
                combinedChart.key = parameter1.plus(" & ").plus(parameter2)
                combinedChart.fcrkey=getResourceString("fcr_ratio").plus(" & ").plus(parameter2)
            }
            else if (parameter2KeyValue?.toInt()==GMKeys.fcr)
            {
                combinedChart.key = parameter1.plus(" & ").plus(parameter2)
                combinedChart.fcrkey=parameter1.plus(" & ").plus(getResourceString("fcr_ratio"))
            }else{
                combinedChart.key = parameter1.plus(" & ").plus(parameter2)
                combinedChart.fcrkey= parameter1.plus(" & ").plus(parameter2)
            }


        }
        var count = 0;
        for (item in chartList) {
            if (item.key?.contains("&")!!) {
                if (item.key.equals(parameter2.plus(" & ").plus(parameter1))) {
                    check = true
                    break
                }

            }
        }

        if (!chartList.contains(combinedChart) && check == false) {
            chartList.add(combinedChart)
            initAdapter(chartList)
        }
        confirmationDialogView.dismiss()

    }

    fun loadSpinner(list: ArrayList<Model.KeyModel>, spinner: Spinner) {
        adapter = ArrayAdapter<Model.KeyModel>(context!!, R.layout.item_spinner, list)
        adapter?.setDropDownViewResource(R.layout.item_report_spinner)
        spinner.adapter = adapter
    }

    fun loadSpinner1(list: ArrayList<Model.KeyModel>, spinner: Spinner) {
        adapter1 = ArrayAdapter<Model.KeyModel>(context!!, R.layout.item_spinner, list)
        adapter1?.setDropDownViewResource(R.layout.item_report_spinner)
        spinner.adapter = adapter1
    }


    private fun tabView() {
        if (batchSpinner?.getSelectedStrings()?.size != 0) {
            batchTabView.removeAllTabs()
            batchSpinner?.getSelectedStrings()?.forEach {
                batchTabView.addTab(batchTabView.newTab().setText(it))
                selectedBatchList.add(it)
            }
            selectedBatchIdList.clear()
            getPerformanceChart(getSelectedList())
        } else {
            selectedBatchIdList.clear()
            dataLayout?.visibility = View.GONE
            recyclerViewRepository?.visibility = View.GONE
            navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.invisible)
        }
        batchTabView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let {
                    initView(tab.text)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


    }

    fun getSelectedList(): ArrayList<Long> {

        val batchIdList1 = ArrayList<Long>()
        for (s in batchSpinner?.getSelectedStrings()!!) {
            val a = batchIdList.singleOrNull { it.hatchDate == com.gmcoreui.utils.DateUtils.toDisplayServerDate(s).trim() }
            a?.batchId?.let { batchIdList1.add(it) }
        }
        return batchIdList1
    }

    private fun getPerformanceChart(batchList: ArrayList<Long>) {
        startProgress()
        val list = ArrayList<Long>()

        val data = Model.ChartInput()
        data.farmCode = GMApplication.farmCode
        data.batchId = batchList
        DataProvider.getPerformanceChart(data, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.PerformanceChartResponse) {
                    chartList.clear()
                    keyList.clear()
                    addTheSpinnerData()
                    checkAPIHit()
                    dataLayout?.visibility = View.VISIBLE
                    recyclerViewRepository?.visibility = View.VISIBLE
                    navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.visible)
                    responseObject.response?.let {
                        it.chart?.let { it1 -> chartList.addAll(it1) }
                        initAdapter(chartList)
                        it.batchData?.let { it1 -> chartResponsList.addAll(it1) }
                        /*if (chartResponsList.size != 0)
                            initView(DateUtils.toDisplayDate(it.batchData?.get(0)?.hatchDate.toString()))*/

                    }
                    removeEmpty()
                }
            }

            override fun onRequestFailed(responseObject: String) {
                checkAPIHit()
                showErrorSnackBar(responseObject)
            }
        })
    }


}





