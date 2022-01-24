package com.gm.controllers.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.GMApplication
import com.gm.R
import com.gm.WebServices.DataProvider
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.DailyReportAdapter
import com.gm.controllers.adapter.FeedBackHistoryAdapter
import com.gm.listener.OnGetCallListener
import com.gm.listener.OnItemClickListener
import com.gm.listener.OnItemSelectedListenerFeedback
import com.gm.listener.ServiceRequestListener
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.controllers.ui.GMSpinner
import com.gmcoreui.utils.DateUtils
import fr.maxcom.libmedia.a
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.fragment_dailyreport.*
import kotlinx.android.synthetic.main.item_body_weight_layout.*
import kotlinx.android.synthetic.main.item_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.item_dailyreport_list.view.*
import kotlinx.android.synthetic.main.layout_dailyreport_table.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DailyReportFragment : GMBaseFragment(), OnItemClickListener, OnItemSelectedListenerFeedback {


    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }


    private var responseValue: Model.DailyReportDetails? = null
    private var totalgrams: Double? = 0.0
    private var feedKiloGram: Double? = 0.0
    private var feedBags: Double? = 0.0
    private var reasonString1: Long? = null
    private var feedStandard:Int?=null
    var feedValue:Double?=0.0
    var numberList=ArrayList<Double>()
    private var spinner:GMSpinner?=null
    var requestValue = Model.FeedBackSubmitRequest()
    var minFeedValue:Double?=0.0
    var maxFeedValue:Double?=0.0
    var responseFeedKg: Double?= 0.0
    companion object {
        fun newInstance(args: Bundle): DailyReportFragment {
            val fragment = DailyReportFragment()
            fragment.arguments = args
            return fragment
        }
        var modifiedfeedBackList = ArrayList<Model.FeedBack>()
        var feedConsumptionUnits:Int =0

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dailyreport, container, false)
    }


    fun setResourceString() {
        ageTextView?.text = getResourceString("label_age")
        birdStockTextView?.text = getResourceString("label_bird_stock")
        feedTextView?.text = getResourceString("label_stdfeed")
        newFeedbackTextView?.text = getResourceString("new_report")
        historyTextView?.text = getResourceString("history")
        feedbackTypetextView?.text = getResourceString("body_weight")
        feedbackQuantityTypetextView?.text = getResourceString("in_kg")
        reasonGMTextInputLayout?.hint = getResourceString("reason")
        submitTextView?.text = getResourceString("submit")
        ageTextView?.text = getResourceString("label_age")
        //label_select_reason

    }

    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (item is Long) {
            reasonString1 = item
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        numberList.clear()
        initToolbar(R.menu.menu_fliter, getResourceString("title_daily_report"), true)
        val navMenu = toolbar?.menu
        navMenu?.findItem(R.id.menu_notification)?.isVisible = resources.getBoolean(R.bool.visible)
        getFeedBackList()
        feedbackRecyclerView?.isNestedScrollingEnabled = false

        submitTextView?.setOnClickListener {
            if (validateReport()) {
                var value=0.0
                if (responseValue?.feedConsumptionUnit?.toInt()==2)
                {
                    val roundOff = feedBags?.times( 100.0)?.let { Math.round(it) }?.div(100.0)
                    feedValue= roundOff?.let { checkcorrectvalue(it) }
                    value= feedValue?:0.0
                }else{
                    value= feedKiloGram?.let { Math.ceil(it) }!!
                }


                if (modifiedfeedBackList[1].count==value) {
                    showConfirmationDialog()
                } else if (modifiedfeedBackList[1].count!=value  && reasonString1!=null && reasonString1!=0.toLong()) {
                    showConfirmationDialog()
                }else if (responseValue?.dailySupport?.enableFeedConsumption==false)
                {
                    showConfirmationDialog()
                }
                else{
                    showSnackBar(getResourceString("message_select_reason"))
                }
            }
        }
        reportView?.visibility = View.VISIBLE
        reportHistoryView?.visibility = View.GONE
        historyLineView?.visibility = View.GONE
        newFeedbackLineView?.visibility = View.VISIBLE
        reportView?.visibility = View.VISIBLE

        feedback.setOnClickListener {
            newFeedbackTextView?.setTextColor(resources.getColor(R.color.white))
            historyTextView?.setTextColor(resources.getColor(R.color.unselected))
            reportView?.visibility = View.VISIBLE
            reportHistoryView?.visibility = View.GONE
            historyLineView?.visibility = View.GONE
            newFeedbackLineView?.visibility = View.VISIBLE
            reportView?.visibility = View.VISIBLE
            standardLayout?.visibility=View.VISIBLE
        }
        history?.setOnClickListener {
            moveToHistory()
        }

        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            getDailyReportHistory(true)
        }

        for (j in 0..100) {
            var num=j.toDouble()
            for (i in 0..4) {
                num=num.plus(0.25)
                numberList.add(num)
            }
        }


    }

    fun moveToHistory(isShowProgess: Boolean = true) {
        historyTextView?.setTextColor(resources.getColor(R.color.white))
        newFeedbackTextView?.setTextColor(resources.getColor(R.color.unselected))
        reportView?.visibility = View.GONE
        reportHistoryView?.visibility = View.VISIBLE
        newFeedbackLineView?.visibility = View.GONE
        historyLineView?.visibility = View.VISIBLE
        standardLayout?.visibility = View.GONE
        getDailyReportHistory(isShowProgess)
    }



 fun checkcorrectvalue( myval:Double):Double {
     val index = numberList.indexOf(myval)
     if (index != -1) {
         return numberList[index];
     } else {
         var temp=myval
         temp
         val maxval = numberList.filter { myval<it }
         val minval = numberList.filter { myval > it }
         var pre=0.0
        var next=0.0
         if (maxval.size!=0 && minval.size!=0) {
             pre = minval.get(minval.size - 1)
             next = maxval.get(0)
         }
             val avg = (pre + next) / 2
             if (avg > myval) {
                 return pre;
             } else {
                 return next;
             }
         }


 }
     private fun getDailyReportHistory(isShowProgess: Boolean) {
        showProgressBar()
        DataProvider.getDailyReportHistory(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                dismissProgressBar()
                swipeContainer?.isRefreshing = false
                if (responseObject is Model.DailyReportHistoryResponse) {
                    responseObject.response?.let {
                        feedBackListHistory(it)
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                swipeContainer?.isRefreshing = false
                dismissProgressBar()
            }
        })
    }


    private fun saveFeedBackDetail() {
            requestValue.UserId = GMApplication.loginUserId
            requestValue.feedConsumptionReason = reasonString1?.toLong()
            requestValue.Receipt = modifiedfeedBackList[0].count
            requestValue.FeedConsumption = modifiedfeedBackList[1].count
            requestValue.FeedTransferIn = modifiedfeedBackList[2].count
            requestValue.FeedTransferOut = modifiedfeedBackList[3].count
            requestValue.FeedStock = modifiedfeedBackList[4].count
            requestValue.Mortality = modifiedfeedBackList[5].count?.toInt() ?: 0
            if (weightEditText?.text.toString().isEmpty()) {
                requestValue.BodyWeight = 0.00
            } else {
                requestValue.BodyWeight = weightEditText?.text.toString().toDouble() ?: 0.00
            }
            requestValue.Reason = reasonEditText?.text.toString()
            postFeedBack(requestValue)
    }

    fun validateReport(): Boolean {
        var reportBool = modifiedfeedBackList?.any { it.count != 0.0 } ?: false
        val value = weightEditText.text
        if (!value.isNullOrEmpty()) {
            if ((value.toString().toDouble() == 0.0 || value.toString().toDouble() > 5000.0)) {
                setErrorMessage(weightEditText, getResourceString("error_invalid"))
                return false
            } else {
                reportBool = true
            }
        }
        if (!reportBool) {
            showSnackBar(getResourceString("error_daily_report"))
        }
        modifiedfeedBackList.forEach {
            if(it.feedName==getResourceString("feed_consumption"))
            {
                if (it.count!! <= it.maxValue?.let { it1-> responseFeedKg?.plus(it1) }!! && it.count!! >= it.minValue?.let { it2->responseFeedKg?.minus(it2) }!!)
                    Log.d("isToleranceAllowed", "toleranceLimit accepted")
                else
                {
                    reportBool = false
                    showSnackBar(getResourceString("error_message_tolerance_limit") + " "+ it.minValue?.let { it2->responseFeedKg?.minus(it2) }!!+" - "+ it.maxValue?.let { it1-> responseFeedKg?.plus(it1) }!!)
                }
            }
        }
        return reportBool
    }

    private fun postFeedBack(requestValue1: Model.FeedBackSubmitRequest) {
        DataProvider.postDailyReportFeedBack(requestValue1, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.FeedBackResponse) {
                    showSuccessDialog(getResourceString("feed_back_updated"), DialogInterface.OnClickListener { _, _ ->
                        moveToHistory(false)
                        reasonString1=null
                        requestValue= Model.FeedBackSubmitRequest()
                        getFeedBackList1()
                    })

                } else {
                    showSuccessDialog("Record saved Successfully", DialogInterface.OnClickListener { _, _ -> })
                }
                dismissProgressBar()
            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)

                dismissProgressBar()
            }
        })
    }

    fun initView(responseValue: Model.DailyReportDetails?) {
        ageValueTextView?.text = responseValue?.age.toString()
        birdStockValueTextView?.text = responseValue?.birdStock.toString()
        feedValueTextView?.text = responseValue?.feedStandard.toString()
    }

    private fun getFeedBackList() {
        showProgressBar()
        DataProvider.getFeedBackDetails(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.DailyReportDetailsResponse) {
                    responseObject.response?.let {
                        responseValue=it
                        totalgrams = it.feedStandard?.toDouble()?.let { it1 -> it.birdStock?.toInt()?.times(it1) }
                        feedKiloGram = totalgrams?.div(1000)
                        if(it.uomConversion!=0L){
                            feedBags = feedKiloGram?.div(it.uomConversion ?: 1)
                        }else{
                            feedBags = 0.0
                        }
                        if (responseValue?.isFeedStandardAvailable==false)
                        {
                            submitTextView?.visibility=View.GONE
                        }
                        else{
                            submitTextView?.visibility=View.VISIBLE
                        }
                        it.feedKg=feedKiloGram
                        it.feedBags=feedBags
                        feedStandard=it.feedStandard
                        initView(it)
                        feedBackList(it)
                        if (responseValue?.isFeedStandardAvailable==false)
                        {
                         showSnackBar(getResourceString("message_standard_missing"))
                        }

                    }
                }

                dismissProgressBar()

            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
                dismissProgressBar()

            }
        })
    }

    private fun getFeedBackList1() {
        DataProvider.getFeedBackDetails(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.DailyReportDetailsResponse) {
                    responseObject.response?.let {
                        responseValue=it
                        totalgrams = it.feedStandard?.toDouble()?.let { it1 -> it.birdStock?.toInt()?.times(it1) }
                        feedKiloGram = totalgrams?.div(1000)
                        feedBags = feedKiloGram?.div(it.uomConversion ?: 0)
                        it.feedKg=feedKiloGram
                        it.feedBags=feedBags
                        feedStandard=it.feedStandard
                        initView(it)
                        feedBackList(it)
                    }
                }
            }

            override fun onRequestFailed(responseObject: String) {
                dismissProgressBar()

            }
        })
    }


    fun feedBackList(responseValue: Model.DailyReportDetails?) {
        setReasonValue(responseValue)
        var feedBackList = ArrayList<Model.FeedBack>()
        modifiedfeedBackList.clear()
        var feedConsumptionUnit:String?=""
        var incrementvalue=0.0
        responseValue?.feedConsumptionUnit?.let {
            feedConsumptionUnits=it
        }
        if (responseValue?.feedConsumptionUnit==1)
        {
            //feedValue=feedKiloGram
            feedConsumptionUnit="Kgs"                                                                                                                                                                                                                                                                                           
            feedValue= feedKiloGram?.let { Math.round(it).toDouble() }
            requestValue.feedConsumptionUOM=feedConsumptionUnit
            incrementvalue=1.0
            minFeedValue= feedValue?.let { calculatePersentageMin(it) }?.let { Math.round(it) }?.toDouble()
            maxFeedValue= feedValue?.let { calculatePersentageMax(it) }?.let { Math.round(it) }?.toDouble()
        }else{
            incrementvalue= responseValue?.dailySupport?.toleranceIncrementDecrementValue?:0.0
          val roundOff = feedBags?.times( 100.0)?.let { Math.round(it) }?.div(100.0)
            feedValue= roundOff?.let { checkcorrectvalue(it) }
            feedConsumptionUnit=getResourceString("no_of_bags")
            requestValue.feedConsumptionUOM=feedConsumptionUnit
            minFeedValue= feedValue?.let { checkcorrectvalue(calculatePersentageMin(it) )}
            maxFeedValue= feedValue?.let { checkcorrectvalue(calculatePersentageMax(it)) }
        }
            if (responseValue?.dailySupport?.enableFeedConsumption==false)
            {
                feedValue=responseValue?.dailySupport?.feedConsumption
            }

        responseValue?.feedKg=feedValue
        if (context != null) {
            feedBackList.add(Model.FeedBack(R.drawable.ic_recieot, getResourceString("reciept"), getResourceString("no_of_bags"), responseValue?.dailySupport?.receipt
                    ?: 0.0,responseValue?.dailySupport?.toleranceIncrementDecrementValue , false,false,true,0.0,0.0))
            feedBackList.add(Model.FeedBack(R.drawable.ic_feedcomsumption, getResourceString("feed_consumption"),feedConsumptionUnit, feedValue
                    ?: 0.0, incrementvalue, true,false,responseValue?.dailySupport?.enableFeedConsumption,minFeedValue?.toDouble(),maxFeedValue?.toDouble() ))
            feedBackList.add(Model.FeedBack(R.drawable.ic_feedtransferin, getResourceString("feed_transfer_in"), getResourceString("no_of_bags"), responseValue?.dailySupport?.feedTransfer
                    ?: 0.0, responseValue?.dailySupport?.toleranceIncrementDecrementValue, false,false,true,0.0,0.0))
            feedBackList.add(Model.FeedBack(R.drawable.ic_feedtransferout, getResourceString("feed_transfer"), getResourceString("no_of_bags"), responseValue?.dailySupport?.feedTransfer
                    ?: 0.0, responseValue?.dailySupport?.toleranceIncrementDecrementValue, false,false,true,0.0,0.0))
            feedBackList.add(Model.FeedBack(R.drawable.ic_feedstock, getResourceString("feed_stock"), getResourceString("no_of_bags"), responseValue?.dailySupport?.feedStock
                    ?: 0.0, responseValue?.dailySupport?.toleranceIncrementDecrementValue, false,false,true,0.0,0.0))
            feedBackList.add(Model.FeedBack(R.drawable.ic_mortality, getResourceString("mortality_count"), getResourceString("no_of_hen"), responseValue?.dailySupport?.mortality?.toDouble()
                    ?: 0.0, 1.0, false,true,true,0.0,0.0))
            modifiedfeedBackList.addAll(feedBackList)
        }
        responseValue?.dailySupport?.bodyWeight?.let { if (it != 0.0) weightEditText?.text = SpannableStringBuilder(it.toString()) }
        context?.let {
            feedbackRecyclerView?.layoutManager = LinearLayoutManager(context)
            feedBackList.forEach {
                if(it.feedName==getResourceString("feed_consumption"))
                    responseFeedKg=it.count
            }
            feedbackRecyclerView?.adapter = responseValue?.let { it1 -> DailyReportAdapter(feedBackList, this, context!!, it1,this,this) }
        }
    }

    internal inner class StringDateComparator : Comparator<Model.DailyReportDateHistory> {
        var dateFormat = SimpleDateFormat(DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED, Locale.getDefault())
        override fun compare(lhs: Model.DailyReportDateHistory, rhs: Model.DailyReportDateHistory): Int {
            return dateFormat.parse(rhs.reportDate).compareTo(dateFormat.parse(lhs.reportDate))
        }
    }

    fun feedBackListHistory(list: ArrayList<Model.DailyReportDateHistory>) {
        Collections.sort(list, StringDateComparator())
        setReasonValue(responseValue)
        context?.let {
            feedbackHistoryRecyclerView?.layoutManager = LinearLayoutManager(context)
            feedbackHistoryRecyclerView?.adapter = FeedBackHistoryAdapter(list, this, context!!, this)
        }
    }

    private fun setReasonValue(responseValue: Model.DailyReportDetails?) {
        reasonEditText?.setText(responseValue?.dailySupport?.reason ?: "")
    }
    fun calculatePersentageMax(value: Double): Double {
        return (value.times(responseValue?.feedConsumptionMax ?: 0.0)).div(100)

    }

    fun calculatePersentageMin(value: Double): Double {
        return (value.times(responseValue?.feedConsumptionMin ?: 0.0)).div(100)
    }

    private fun showConfirmationDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_confirmation_dialog, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        confirmationDialog.setCancelable(false)
        confirmationDialogView.cancelTextView.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        confirmationDialogView.cancelTextView.text = getResourceString("cancel")
        confirmationDialogView.confirmTextView.text = getResourceString("ok")
        confirmationDialogView.confirmationMsgTextView?.text = getResourceString("confirm_submission")
        confirmationDialogView.confirmTextView.setOnClickListener {
            confirmationDialogView.confimationStartImageView.visibility = View.GONE
            confirmationDialogView.confimationEndImageView.visibility = View.VISIBLE
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    confirmationDialog.dismiss()
                    showProgressBar()
                    saveFeedBackDetail()
                }
            }
            handler.postDelayed(runnable, 1000)
        }
        confirmationDialog?.show()
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_notification -> {
                var bundle = Bundle()
                (activity as HomeActivity).replaceFragment(NotificationFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

    override fun onItemSelected(item: Any?, totalItem: Any?, selectedIndex: Int) {
        if (item is GMSpinner)
        {
            spinner=item
        }

    }
}
