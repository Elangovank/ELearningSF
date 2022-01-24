package com.gm.controllers.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.core.utils.AppPreferences
import com.gm.R
import com.gm.WebServices.APIClient
import com.gm.WebServices.APIInterface
import com.gm.WebServices.DataProvider
import com.gm.WebServices.ServiceWrapper
import com.gm.controllers.activities.HomeActivity
import com.gm.controllers.adapter.ActivityAdapter
import com.gm.listener.OnItemClickListener
import com.gm.listener.ServiceRequestListener
import com.gm.models.MediaType
import com.gm.models.Model
import com.gm.receiver.NetworkAvailability
import com.gm.utilities.GMKeys
import com.gm.utilities.IntentUtils
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.controllers.ui.GMSpinner
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.activity_recyclerview.*
import kotlinx.android.synthetic.main.appbar_normal.*
import kotlinx.android.synthetic.main.item_filter_dialogu.view.*
import kotlinx.android.synthetic.main.layout_no_record.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class ActivityFragment : GMBaseFragment(), OnItemClickListener, Animation.AnimationListener {
    var pendingActivitiesList = ArrayList<Model.ActivityList>()
    var completedActivitiesList = ArrayList<Model.ActivityList>()
    var fliterActivityList = ArrayList<Model.ActivityList>()
    var historyActivityList = ArrayList<Model.ActivityList>()
    var completedActivityList = ArrayList<Model.ActivityList>()
    var fliterFlage: Boolean? = null
    var fliterdate = ""
    var progressStatus = Model.PrograssBar()
    var chickCount = -1
    var pendingCount = 0
    private var adapter: ActivityAdapter? = null
    private var isClicked: Boolean = false
    var selectedItem: Int? = 0
    var animationCountPending = 0
    var animationCountCompleted = -1
    var isCompletedTab: Boolean = false
    private var isShedReadyActivity: Boolean = false
    var apiInterface: APIInterface? = null
    var weatherList = Model.WeatherData()


    override fun onItemSelected(item: Any?, selectedIndex: Int) {
        if (selectedIndex == -1 || isCompletedTab) {
            gotoActivityViewFragment(item as Model.ActivityList)
        }
    }

    private fun setResourceString() {
        activityProgress?.text = getResourceString("activity_progresss")
        pending?.text = getResourceString("pending")
        completed?.text = getResourceString("history")
        noDataTextView?.text=getResourceString("no_record_found")
    }

    private fun gotoActivityViewFragment(item: Model.ActivityList, mediaType: MediaType? = null, isGallery: Boolean? = null) {
        val bundle = Bundle()
        bundle.putBoolean(GMKeys.isCompleted, isCompletedTab)
        bundle.putSerializable(GMKeys.selectedActivity, item)
        (activity as HomeActivity).replaceFragment(ActivityViewFragment.newInstance(bundle), "")
    }


    fun toGetWeather() {
        val postal = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.postalCode, ""
                ?: "")
        val weatherAPIKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.WEATHER_API_KEY, "")
        val call: Call<Model.WeatherData?>? = apiInterface?.doGetUserList(
                postal,
                weatherAPIKey)
        call?.enqueue(object : Callback<Model.WeatherData?> {
            override fun onResponse(call: Call<Model.WeatherData?>?, response: Response<Model.WeatherData?>) {
                weatherList = Model.WeatherData()
                response.body()?.let {
                    weatherList = it
                    //varList?.list=tempList
                    weatherList.data?.forEach {
                        if (DateUtils.toDisplayTimeWeatherDate(DateUtils.getTodayDate(DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)).equals(DateUtils.toDisplayTimeWeatherDate(it.timestamp_local))) {
                            if (DateUtils.toDisplayDateHour(it.timestamp_local).equals(DateUtils.toDisplayDateHour(DateUtils.getTodayDate()))) {
                                weatherTextView?.text = it.temp?.let { it1 -> DateUtils?.convertCelsiusToFahrenheit(it1) }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Model.WeatherData?>, t: Throwable) {
            }
        })
    }

    fun getProgressBar(date: String, age: Int) {
        DataProvider.getProgressResult(date, age, object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.PrograssBarResponse) {
                    responseObject.progressStatus.let {
                        progressStatus = it ?: Model.PrograssBar()
                        progressBar?.progress = (progressStatus.completedProgress ?: 0.0).toInt()
                        percentageView?.text = (progressStatus.completedProgress
                                ?: 0.0).toInt().toString().plus("%")
                        toGetWeather()
                    }
                }

            }

            override fun onRequestFailed(responseObject: String) {
                showErrorSnackBar(responseObject)
            }
        })
    }


    companion object {
        fun newInstance(args: Bundle): ActivityFragment {
            val fragment = ActivityFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_recyclerview, container, false)
    }


    private fun showFilterDialog() {
        val factory = LayoutInflater.from(context)
        val confirmationDialogView = factory.inflate(R.layout.item_filter_dialogu, null)
        val confirmationDialog = AlertDialog.Builder(context).create()
        confirmationDialog.setView(confirmationDialogView)
        //confirmationDialogView.chick_count.hint = "0"

        var statusList = ArrayList<String>()
        getResourceString("label_all").let { statusList.add(it) }
        getResourceString("completed").let { statusList.add(it) }
        getResourceString("incomplete").let { statusList.add(it) }
        loadSpinnerData(statusList, confirmationDialogView.spinner, confirmationDialog, confirmationDialogView)
        confirmationDialogView.cross.setOnClickListener {
            isClicked = false
            isShedReadyActivity = false
            confirmationDialog.dismiss()
        }
        confirmationDialogView?.dateLayout?.setOnClickListener {
            isShedReadyActivity = false
            if (confirmationDialogView.datePicker?.visibility == View.VISIBLE) {
                confirmationDialogView.datePicker?.visibility = View.GONE
                confirmationDialogView.selected?.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.calender?.setImageResource(R.drawable.ic_gallery)

                confirmationDialogView.dateTextView?.setTextColor(Color.parseColor("#666666"))
            } else {
                confirmationDialogView.selected?.setImageResource(R.drawable.ic_checked)
                confirmationDialogView.calender?.setImageResource(R.drawable.ic_calendar)
                confirmationDialogView.datePicker?.visibility = View.VISIBLE
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(confirmationDialogView.chick_count?.getWindowToken(), 0)
                confirmationDialogView.submitLayout?.visibility = View.VISIBLE

                serverDateTime?.let {
                    val date = DateUtils.convertStringToDate(it, com.gmcoreui.utils.DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)
                    val cal = Calendar.getInstance()
                    cal.time = date
                    confirmationDialogView.datePicker.maxDate = cal.timeInMillis - 1000
                    confirmationDialogView.datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                }
                confirmationDialogView.chickAgeLayout.visibility = View.GONE
                confirmationDialogView.chick_checked.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.chick_bird.setImageResource(R.drawable.ic_chick)
                confirmationDialogView.chick_age.setTextColor(Color.parseColor("#666666"))
                confirmationDialogView.dateTextView.setTextColor(Color.parseColor("#FF6B72"))
                confirmationDialogView.shedReadyCheckedView.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.shedReadyImageView.setImageResource(R.drawable.ic_shedready)
                confirmationDialogView.shedReadyTextView.setTextColor(Color.parseColor("#666666"))
            }
        }
        confirmationDialogView.historyLayout.setOnClickListener {
            confirmationDialogView.submitLayout.visibility = View.VISIBLE
        }
        confirmationDialogView.chickLayout.setOnClickListener {
            isShedReadyActivity = false
            if (confirmationDialogView.chickAgeLayout.visibility == View.VISIBLE) {
                confirmationDialogView.chickAgeLayout.visibility = View.GONE
                confirmationDialogView.chick_checked.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.chick_bird.setImageResource(R.drawable.ic_chick)
                confirmationDialogView.chick_age.setTextColor(Color.parseColor("#666666"))
            } else {
                confirmationDialogView.datePicker.visibility = View.GONE
                confirmationDialogView.chickAgeLayout.visibility = View.VISIBLE
                confirmationDialogView.chick_checked.setImageResource(R.drawable.ic_checked)
                confirmationDialogView.chick_bird.setImageResource(R.drawable.ic_bird)
                confirmationDialogView.submitLayout.visibility = View.VISIBLE
                confirmationDialogView.chick_age.setTextColor(Color.parseColor("#FF6B72"))
                confirmationDialogView.selected.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.calender.setImageResource(R.drawable.ic_gallery)
                confirmationDialogView.dateTextView.setTextColor(Color.parseColor("#666666"))
                confirmationDialogView.shedReadyCheckedView.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.shedReadyImageView.setImageResource(R.drawable.ic_shedready)
                confirmationDialogView.shedReadyTextView.setTextColor(Color.parseColor("#666666"))
            }

        }

        confirmationDialogView.shedReadyLayout.setOnClickListener {
            if (isShedReadyActivity) {
                isShedReadyActivity = false
                confirmationDialogView.shedReadyCheckedView.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.shedReadyImageView.setImageResource(R.drawable.ic_shedready)
                confirmationDialogView.shedReadyTextView.setTextColor(Color.parseColor("#666666"))
            } else {
                isShedReadyActivity = true
                confirmationDialogView.shedReadyCheckedView.setImageResource(R.drawable.ic_checked)
                confirmationDialogView.shedReadyImageView.setImageResource(R.drawable.ic_shedready_selected)
                confirmationDialogView.shedReadyTextView.setTextColor(Color.parseColor("#FF6B72"))

                confirmationDialogView.datePicker.visibility = View.GONE
                confirmationDialogView.chickAgeLayout.visibility = View.GONE
                confirmationDialogView.selected.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.calender.setImageResource(R.drawable.ic_gallery)
                confirmationDialogView.dateTextView.setTextColor(Color.parseColor("#666666"))
                confirmationDialogView.chick_checked.setImageResource(R.drawable.ic_unchecked)
                confirmationDialogView.chick_bird.setImageResource(R.drawable.ic_chick)
                confirmationDialogView.chick_age.setTextColor(Color.parseColor("#666666"))

            }

        }
        confirmationDialogView.chick_count.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.isEmpty()) {
                    if (s.toString().toInt() == 0) {
                        //  confirmationDialogView.chick_count.setText("0")
                    } else if (completedActivitiesList.size != 0) {
                        if (s.toString().toInt() > completedActivitiesList.get(0).age ?: 0) {
                            confirmationDialogView.chick_count.setText("0")
                            confirmationDialogView.chick_count.setSelection(confirmationDialogView.chick_count?.text.toString().length)
                        }
                    } else if (historyActivityList.size != 0) {
                        if (s.toString().toInt() > historyActivityList.get(0).age ?: 0) {
                            confirmationDialogView.chick_count.setText("0")
                            confirmationDialogView.chick_count.setSelection(confirmationDialogView.chick_count?.text.toString().length)
                        }
                    }
                }
            }
        })
        confirmationDialogView.fliterTextView.text = getResourceString("fliter")
        confirmationDialogView.dateTextView?.text = getResourceString("label_date")
        confirmationDialogView.chick_age?.text = getResourceString("chick_days")
        confirmationDialogView.shedReadyTextView?.text = getResourceString("shed_ready_activity_label")
        confirmationDialogView.cancelFilterTextView?.text = getResourceString("cancel")
        confirmationDialogView.submit?.text = getResourceString("submit")
        confirmationDialogView.minus.setOnClickListener {
            var chickdays: String = "0"
            if (confirmationDialogView.chick_count?.text.toString().equals("")) {
                chickdays = "0"
            } else {
                chickdays = confirmationDialogView.chick_count?.text.toString()
            }
            if (chickdays.toInt() >= 1) {
                confirmationDialogView.chick_count.setText(chickdays.toInt().minus(1).toString())
            } else {
                confirmationDialogView.chick_count.setText("1")
                confirmationDialogView.chick_count.setSelection(confirmationDialogView.chick_count?.text.toString().length)

            }
        }
        confirmationDialogView.plus.setOnClickListener {

            val list = (recyclerView?.adapter as ActivityAdapter).nameList
            if (list.size != 0) {
                val chickDays: String
                if (confirmationDialogView.chick_count?.text.toString().equals("")) {
                    chickDays = "0"
                } else {
                    chickDays = confirmationDialogView.chick_count?.text.toString()
                }

                if (chickDays.toInt() <= (list[0].age ?: 0).toInt()) {
                    confirmationDialogView.chick_count.setText(chickDays.toInt().plus(1).toString())
                } else {
                    confirmationDialogView.chick_count.setText("1")
                    confirmationDialogView.chick_count.setSelection(confirmationDialogView.chick_count?.text.toString().length)

                }
            }
        }
        confirmationDialogView.submit.setOnClickListener {
            if (confirmationDialogView.datePicker.visibility == View.VISIBLE) {
                filterData()
                fliterdate = confirmationDialogView.datePicker.year.toString().plus("-")
                        .plus(confirmationDialogView.datePicker.month.toString().toInt() + 1).plus("-").plus(confirmationDialogView.datePicker?.dayOfMonth.toString()).plus("T00:00:00")
                filterResult(fliterdate, -1, selectedItem ?: 0)
                getProgressBar(fliterdate, -1)
            } else if (confirmationDialogView.chickAgeLayout.visibility == View.VISIBLE) {

                if (confirmationDialogView.chick_count?.text.toString().equals("")) {
                    Toast.makeText(context, getResourceString("chick_days_error"), Toast.LENGTH_SHORT).show()
                } else {
                    filterData()
                    chickCount = confirmationDialogView.chick_count?.text?.toString()?.toInt() ?: 0
                    filterResult("", chickCount, selectedItem ?: 0)
                    getProgressBar("", chickCount)
                }
            } else if (isShedReadyActivity) {
                filterData()
                isShedReadyActivity = true
                filterResult("", chickCount, selectedItem ?: 0)
                getProgressBar("", chickCount)
            } else {
                filterData()
                filterResult("", -1, selectedItem ?: 0)
                getProgressBar("", -1)
            }

            confirmationDialog.dismiss()

        }
        confirmationDialogView.cancelFilterTextView?.setOnClickListener {
            isClicked = false
            isShedReadyActivity = false
            confirmationDialog.dismiss()
        }
        confirmationDialog.setCancelable(false)
        confirmationDialog?.show()
    }


    fun filterData() {
        resetFilter()
        isClicked = false
        isShedReadyActivity = false
        fliterActivityList.clear()
        fliterFlage = true
        activitiesNestedScrollView?.fullScroll(View.FOCUS_UP)
        toolbar.title = getResourceString("title_activity")
    }

    private var navMenu: Menu? = null
    var serverDateTime: String? = null

    fun loadSpinnerData(spinnerArray: ArrayList<String>, spinnerName: GMSpinner?, alertDialog: AlertDialog, view: View) {
        val adapter1 = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, spinnerArray) }
        adapter1?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerName?.setAdapter(adapter1)
        completedActivityList.clear()
        historyActivityList.clear()
        view.submitLayout.visibility = View.VISIBLE
        spinnerName?.setSelection(selectedItem ?: 0)
        spinnerName?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = position
            }

        })

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setResourceString()
        apiInterface = APIClient.client!!.create(APIInterface::class.java)
        toGetWeather()
        initToolbar(R.menu.menu_fliter_activity, getResourceString("today_activity") ?: "", true)
        navMenu = toolbar.menu
        navMenu?.findItem(R.id.menu_notification)?.isVisible = resources.getBoolean(R.bool.visible)
        toolbar?.setSubtitleTextColor(ContextCompat.getColor(context!!, R.color.activity_text_color))
        toolbar?.titleMarginTop = 20
        getServerTime()
        swipeContainer?.isRefreshing = false
        swipeContainer?.setOnRefreshListener {
            swipeContainer?.isRefreshing = true
            resetFilter()
            activitiesNestedScrollView?.fullScroll(View.FOCUS_UP)
            selectedItem = 0
            if (isCompletedTab) {
                fliterActivityList.clear()
                completedActivitiesList.clear()
                getActivityList(isCompletedTab)
            } else {
                pendingActivitiesList.clear()
                getActivityList(isCompletedTab)
            }
            getProgressBar("", -1)
        }
        weatherTextView?.setText(("25") + "\u2103");
        pendingButton.setOnClickListener {
            pendingCount = 1
            isCompletedTab = false
            navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.invisible)
            toolbar?.title = getResourceString("today_activity")
            selectedItem = 0
            resetFilter()
            buttonUpdate()
        }
        completedButton.setOnClickListener {
            toolbar.title = getResourceString("title_activity")
            if (NetworkAvailability.isNetworkAvailable(context!!)) {
                navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.visible)
            } else {
                navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.invisible)
            }
            isCompletedTab = true
            buttonUpdate()
        }
        buttonLayouts.visibility = View.VISIBLE
        buttonUpdate()
        initAdapter()
        activitiesNestedScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                var position = 0
                var lastPosition = 0
                v?.getChildAt(0)?.measuredHeight?.let {
                    position = it
                }
                v?.measuredHeight?.let {
                    lastPosition = it
                }
                if (scrollY == position - lastPosition) {
                    if (NetworkAvailability.isNetworkAvailable(context!!)) {
                        if (isCompletedTab) {
                            if (fliterdate.equals("") && chickCount == -1) {
                                filterResult("", -1, selectedItem ?: 0)
                            } else if (!fliterdate.equals("")) {
                                filterResult(fliterdate, -1, selectedItem ?: 0)
                            } else {
                                filterResult("", chickCount, selectedItem ?: 0)
                            }
                        } else {
                            getActivityList(isCompletedTab)
                        }
                    } else {
                        if ((if (isCompletedTab) fliterActivityList.size else pendingActivitiesList.size) % GMKeys.PAGE_SIZE == 0) {
                            loadMoreProgressBar?.visibility = View.VISIBLE
                        } else {
                            loadMoreProgressBar?.visibility = View.GONE
                        }
                    }
                }
            }
        })
        getProgressBar("", -1)
    }

    fun getServerTime() {
        ServiceWrapper.getServerTime(object : ServiceRequestListener {
            override fun onRequestCompleted(responseObject: Any?) {
                if (responseObject is Model.ServerTime)
                    serverDateTime = responseObject.time
                updateTodayTime()
            }

            override fun onRequestFailed(responseObject: String) {
                serverDateTime = DateUtils.getTodayDate()
                updateTodayTime()
            }
        })
    }

    fun updateTodayTime() {
        serverDateTime?.let {
            toolbar?.subtitle = DateUtils.toDisplayDate(it, DateUtils.DISPLAY_MONTH_DATE)
        }
    }

    fun filterVisibility(list: ArrayList<Model.ActivityList>) {
        if (list.size != 0) {
            if (list.get(0).hatchDate != null) {
                navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.visible)
            } else {
                navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.invisible)
            }
        }

    }

    fun getActivityList(isCompleted: Boolean) {
        if (!isCompleted) {
            if (pendingActivitiesList.size == 0) {
                showProgressBar()
            }
            if (pendingActivitiesList.size % GMKeys.PAGE_SIZE == 0) {

                loadMoreProgressBar?.visibility = View.VISIBLE
                DataProvider.getPendingActivityList(pendingActivitiesList.size, object : ServiceRequestListener {
                    override fun onRequestCompleted(responseObject: Any?) {
                        if (responseObject is Model.ActivityListResponse) {
                            responseObject.activityList?.let { activityList ->
                                activityList.forEachIndexed { index, activity ->
                                    activity.audios?.forEach {
                                        it.mediaType = MediaType.Audio
                                    }
                                    activity.videos?.forEach {
                                        it.mediaType = MediaType.Video
                                    }
                                    activity.images?.forEach {
                                        it.mediaType = MediaType.Image
                                    }
                                    activity.pdfs?.forEach {
                                        it.mediaType = MediaType.Pdf
                                    }
                                }
                                pendingActivitiesList.addAll(activityList)
                                updateAdapter(pendingActivitiesList, activityList.size == 0)
                            }
                        }

                        dismissProgressBar()
                        swipeContainer?.isRefreshing = false
                    }

                    override fun onRequestFailed(responseObject: String) {
                        swipeContainer?.isRefreshing = false
                        if (IntentUtils.checkResponseObject(responseObject)) {
                            val errorMsg = IntentUtils.assignErrorLanguageForId(resources, responseObject.toInt())
                            showSnackBar(errorMsg!!)
                        } else {
                            showErrorSnackBar(responseObject)
                        }

                        dismissProgressBar()
                        swipeContainer?.isRefreshing = false
                        updateAdapter(pendingActivitiesList)
                    }
                })
            } else {
                dismissProgressBar()
                swipeContainer?.isRefreshing = false
                loadMoreProgressBar?.visibility = View.GONE
            }
        } else {
            isShedReadyActivity = false
            filterResult("", -1, 0)
        }
    }


    override fun onPermissionGranted(requestCode: Int) {
    }

    override fun onPermissionDenied(requestCode: Int) {
    }

    private fun filterResult(date: String, age: Int, flage: Int) {
        if (age != -1) {
            toolbar?.subtitle = getResourceString("age_days") + " " + getResourceString("chick_ages_format")?.let { String.format(it, age.toString()) }

        } else if (date != "") {
            toolbar?.subtitle = getResourceString("filtered_date") + DateUtils.toDisplayDate(date, DateUtils.DISPLAY_DATE)
        } else if (isShedReadyActivity) {
            toolbar?.subtitle = getResourceString("shed_ready_activity_label")
        } else {
            updateTodayTime()
        }

        if (fliterActivityList.size == 0) {
            showProgressBar()
        }
        if (fliterActivityList.size % GMKeys.PAGE_SIZE == 0) {
            loadMoreProgressBar?.visibility = View.VISIBLE
            DataProvider.getCompletedActivityList(date, age, fliterActivityList.size, flage, isShedReadyActivity, object : ServiceRequestListener {
                override fun onRequestCompleted(responseObject: Any?) {
                    if (responseObject is Model.ActivityListResponse) {
                        responseObject.activityList?.let { activityList ->
                            filterVisibility(activityList)
                            activityList.forEachIndexed { index, activity ->
                                activity.audios?.forEach {
                                    it.mediaType = MediaType.Audio
                                }
                                activity.videos?.forEach {
                                    it.mediaType = MediaType.Video
                                }
                                activity.images?.forEach {
                                    it.mediaType = MediaType.Image
                                }
                            }
                            responseObject?.activityList?.forEach {
                                if (it.isCompleted == false) {
                                    /*completedActivityList.add(it)
                                } else {*/
                                    historyActivityList.add(it)
                                }
                            }
                            fliterActivityList.addAll(activityList)
                            updateAdapter(fliterActivityList, activityList.size == 0)
                            dismissProgressBar()
                            swipeContainer?.isRefreshing = false
                            loadMoreProgressBar?.visibility = View.GONE
                        }
                    }
                }

                override fun onRequestFailed(responseObject: String) {
                    toolbar.title = getResourceString("today_activity")
                    swipeContainer?.isRefreshing = false
                    dismissProgressBar()
                    completedActivitiesList = ArrayList<Model.ActivityList>()
                    updateAdapter(completedActivitiesList)
                    loadMoreProgressBar?.visibility = View.GONE
                }
            })

        } else {
            swipeContainer?.isRefreshing = false
            loadMoreProgressBar?.visibility = View.GONE
        }
    }

    fun resetFilter() {
        fliterdate = ""
        chickCount = -1
        isShedReadyActivity = false
    }


    private fun buttonUpdate() {
        getProgressBar("", -1)
        updateTodayTime()
        loadMoreProgressBar?.visibility = View.GONE
        if (!isCompletedTab) {
            if (pendingCount == 1 && (animationCountPending == -1)) {
                var slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_right)
                slideLeft.setAnimationListener(this)
                pendingButton.startAnimation(slideLeft)
                animationCountCompleted = -1
                animationCountPending = 1
            }
            pendingButton.setBackgroundResource(R.drawable.pending_background)
            completedButton.setBackgroundResource(R.drawable.completed_background_without_solid)
            pending.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            pendingImage.setColorFilter(ContextCompat.getColor(context!!, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
            completed.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            var backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.white)
            ViewCompat.setBackgroundTintList(pendingImage, backgroundTintList)
            var backgroundTintListPending = AppCompatResources.getColorStateList(context!!, R.color.colorPrimary)
            ViewCompat.setBackgroundTintList(completedImage, backgroundTintListPending)
            activitiesNestedScrollView.scrollTo(0, 0)
            updateAdapter(ArrayList())
            pendingActivitiesList.clear()
            getActivityList(isCompletedTab)
            isShedReadyActivity = false

        } else {
            pendingButton.setBackgroundResource(R.drawable.pending_background_without_solid)
            completedButton.setBackgroundResource(R.drawable.completed_background)
            completedImage.setColorFilter(ContextCompat.getColor(context!!, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
            pending.setTextColor(ContextCompat.getColor(context!!, R.color.pending_solid))
            completed.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            var backgroundTintList = AppCompatResources.getColorStateList(context!!, R.color.white);
            ViewCompat.setBackgroundTintList(completedImage, backgroundTintList)
            var backgroundTintListCompleted = AppCompatResources.getColorStateList(context!!, R.color.pending_solid);


            if (animationCountCompleted == -1) {
                animationCountCompleted = 1
                animationCountPending = -1
                var slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_left)
                slideRight.setAnimationListener(this)
                completedButton.startAnimation(slideRight)
            }

            ViewCompat.setBackgroundTintList(pendingImage, backgroundTintListCompleted);
            activitiesNestedScrollView.scrollTo(0, 0)
            fliterActivityList.clear()
            updateAdapter(ArrayList())
            isShedReadyActivity = false
            getActivityList(isCompletedTab)


        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_fliter -> {
                if (!isClicked) {
                    isClicked = true
                    showFilterDialog()
                }
            }
            R.id.menu_notification -> {
                val bundle = Bundle()
                (activity as HomeActivity).replaceFragment(NotificationFragment.newInstance(bundle))
            }
        }
        return super.onOptionsItemSelected(item!!)

    }

    fun updateAdapter(list: ArrayList<Model.ActivityList>, isEnd: Boolean = true) {

        recyclerView?.adapter?.let {
            if (it is ActivityAdapter) {
                it.updateList(list, !isCompletedTab)
                if (list.size != 0) {
                    if (list.size % GMKeys.PAGE_SIZE == 0 && isEnd) {
                        loadMoreProgressBar?.visibility = View.VISIBLE
                    } else {
                        loadMoreProgressBar?.visibility = View.GONE
                    }
                    noDataLayout?.visibility = View.GONE
                    headerLayout?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.VISIBLE


                } else {
                    noDataLayout?.visibility = View.VISIBLE
                    noDataLayout?.bringToFront()
                    recyclerView?.visibility = View.GONE
                    headerLayout?.visibility = View.GONE
                    loadMoreProgressBar?.visibility = View.GONE

                }
            }
        }
    }


    private fun initAdapter() {
        recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        if (!isCompletedTab)
            adapter = ActivityAdapter(pendingActivitiesList, context!!, this, true, resources, this)
        else {
            navMenu?.findItem(R.id.menu_fliter)?.isVisible = resources.getBoolean(R.bool.visible)
            adapter = ActivityAdapter(completedActivitiesList, context!!, this, false, resources, this)
        }
        recyclerView?.adapter = adapter
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {

    }

    override fun onAnimationStart(animation: Animation?) {
    }
}