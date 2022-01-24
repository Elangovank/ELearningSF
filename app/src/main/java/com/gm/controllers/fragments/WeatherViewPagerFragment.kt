package com.gm.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelStore
import com.core.utils.AppPreferences
import com.gm.R
import com.gm.WebServices.APIClient
import com.gm.WebServices.APIInterface
import com.gm.WebServices.DataProvider
import com.gm.WebServices.DataProvider.application
import com.gm.WebServices.DataProvider.threadBlock
import com.gm.controllers.adapter.MyAdapter
import com.gm.models.Model
import com.gm.utilities.GMKeys
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.fragment_weather_viewpager.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class WeatherViewPagerFragment : GMBaseFragment() {
    var apiInterface: APIInterface? = null
    var varList=Model.WeatherDetails()
    var weatherList = Model.WeatherData()
    var listData = ArrayList<Model.WeatherData>()
    var dataList = ArrayList<String>()
    var tempList=ArrayList<Model.WeatherDetailsList>()


    companion object {
        fun newInstance(arg: Bundle): WeatherViewPagerFragment {
            val fragement = WeatherViewPagerFragment()
            fragement.arguments = arg
            return fragement
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_viewpager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataList.clear()
        weatherList?.data?.forEach {
            if (!dataList.contains(DateUtils.toDisplayDateWeather(it.timestamp_local))) {
                dataList.add(DateUtils.toDisplayDateWeather(it.timestamp_local))
                tabLayout?.addTab(tabLayout!!.newTab().setText(DateUtils.toDisplayDateWeather(it.timestamp_local)))
            }
        }

        initToolbar(getResourceString("title_weather"), false)
        apiInterface = APIClient.client!!.create(APIInterface::class.java)
        dateDisplayTextView?.text = DateUtils.toDisplayDateWeather(DateUtils.getTodayDate())
        val ti = DateUtils.toDisplayTimeWeatherAM(DateUtils.getTodayDate())
        if (ti.trim().equals("AM")) {
            dateDisplayAMTextView?.text = " ".plus(getResourceString("am"))
        } else {
            dateDisplayAMTextView?.text = " ".plus(getResourceString("pm"))
        }
        //dateDisplayTimeTextView?.text = DateUtils.toDisplayTimeWeatherReport(DateUtils.getTodayDate())
        //  toGetWeather()

        toGetWeather(DateUtils.getTommarrowDateInput().get(0), DateUtils.getTommarrowDateInput().get(1))

        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

    }


    override fun getViewModelStore(): ViewModelStore {
        return super.getViewModelStore()
    }


    fun initTab(list: ArrayList<Model.DailyWeatherData>) {
        dataList.clear()
        weatherList?.data?.forEach {
            if (!dataList.contains(DateUtils.toDisplayDateWeather(it.timestamp_local))) {
                dataList.add(DateUtils.toDisplayDateWeather(it.timestamp_local))
                tabLayout?.addTab(tabLayout!!.newTab().setText(DateUtils.toDisplayDateWeather(it.timestamp_local)))
            }
        }
        tabLayout?.tabGravity = TabLayout.GRAVITY_FILL

        }

    fun toGetWeather(startDate: String? = null, endDate: String? = null) {
        showProgressBar()
        val lat = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.LATITUDE, ""
                ?: "")
        val log = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.LONGITUDE, ""
                ?: "")
        val postal = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.postalCode, ""
                ?: "")
        val weatherAPIKey = AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.WEATHER_API_KEY, "")
        val call: Call<Model.WeatherData?>? = apiInterface!!.doGetUserList(
                postal,
                weatherAPIKey)
        call?.enqueue(object : Callback<Model.WeatherData?> {
            override fun onResponse(call: Call<Model.WeatherData?>?, response: Response<Model.WeatherData?>) {
                weatherList = Model.WeatherData()
                response.body()?.let {
                    weatherList = it
                    DataProvider.threadBlock {
                        DataProvider.application?.database?.getWeatherListDao()?.deleteAll()
                        DataProvider.application?.database?.getWeatherListDao()?.insert(it)

                    }
                }
                listData.add(weatherList)
                initTab(arrayListOf())
                initView()
                initViewAdapter(varList)
                dismissProgressBar()
            }

            override fun onFailure(call: Call<Model.WeatherData?>, t: Throwable) {
                threadBlock {
                    val list = application?.database?.getWeatherListDao()?.getall()
                    var responseObject: Model.WeatherData? = null
                    list?.let {
                        weatherList = list as Model.WeatherData
                        initTab(arrayListOf())
                        initView()
                        initViewAdapter(varList)
                        dismissProgressBar()
                    }
                }
                dismissProgressBar()
            }
        })
    }

    fun initView() {
        placeTextView?.text = weatherList?.city_name
    }


    fun initViewAdapter(weatherList1: Model.WeatherDetails) {
        tempList.clear()
        dataList.forEachIndexed { index, s ->
            var a=    weatherList?.data?.filter { s.equals(DateUtils.toDisplayDateWeather(it.timestamp_local)) &&it.timestamp_local?.compareTo(DateUtils.getTodayDate("yyyy-MM-dd'T'HH:mm:ss"))!=-1}
            var weather=Model.WeatherDetailsList()
            weather.weatherlist= a as ArrayList<Model.DailyWeatherData>?
       //     if (weather.weatherlist?.get(po))
            tempList.add(weather)
        }
        varList?.list=tempList
        val adapter = MyAdapter(context!!, (context as AppCompatActivity).supportFragmentManager, tabLayout!!.tabCount, tempList)
        viewPager?.adapter = adapter
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }

}




