package com.gm.controllers.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gm.R
import com.gm.WebServices.APIInterface
import com.gm.controllers.adapter.WeatherAdapter
import com.gm.models.Model
import com.gmcoreui.controllers.fragments.GMBaseFragment
import com.gmcoreui.utils.DateUtils
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.core.utils.AppPreferences
import com.gm.utilities.GMKeys
import kotlinx.android.synthetic.main.fragment_weather.*


class WeatherFragment : GMBaseFragment() {

    var weatherList= Model.WeatherDetailsList()

    var weatherDetailList=ArrayList<Model.DailyWeatherData>()

    companion object {

        fun newInstance(arg: Bundle,tabPostion:Int): WeatherFragment {
            val fragement = WeatherFragment()
            fragement.arguments = arg
            return fragement
        }

    }

    var tabLayout: TabLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherList=arguments?.get("data")as Model.WeatherDetailsList
        weatherDetailList.addAll(weatherList?.weatherlist!!)
        context?.resources?.getColor(R.color.green_shade_1)?.let { it1 -> precipitationButton?.setBackgroundColor(it1) }
        val dateFormat = SimpleDateFormat(DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)
        Collections.sort(weatherDetailList) { o1, o2 -> dateFormat.parse(o1.timestamp_local).compareTo(dateFormat.parse(o2.timestamp_local)) }
        setResourceString()
        initView()
        initAdapter(weatherDetailList,1)
        weatherDetailList.forEach {
            when(it.weather?.code?.toInt())
            {
                200->it.weather?.drawableId=R.drawable.t01d
                201->it.weather?.drawableId=R.drawable.t02d
                202->it.weather?.drawableId=R.drawable.t03d
                230->it.weather?.drawableId=R.drawable.t04d
                231->it.weather?.drawableId=R.drawable.t04d
                232->it.weather?.drawableId=R.drawable.t04d
                300->it.weather?.drawableId=R.drawable.d01d
                301->it.weather?.drawableId=R.drawable.d02d
                302->it.weather?.drawableId=R.drawable.d03d
                500->it.weather?.drawableId=R.drawable.r01d
                501->it.weather?.drawableId=R.drawable.r02d
                502->it.weather?.drawableId=R.drawable.r02d
                511->it.weather?.drawableId=R.drawable.f01d
                520->it.weather?.drawableId=R.drawable.r04d
               521->it.weather?.drawableId=R.drawable.r05d
                522->it.weather?.drawableId=R.drawable.r06d
                600->it.weather?.drawableId=R.drawable.s01d
                601->it.weather?.drawableId=R.drawable.s02d
                602->it.weather?.drawableId=R.drawable.s03d
                610->it.weather?.drawableId=R.drawable.s04d
                611->it.weather?.drawableId=R.drawable.s05d
                612->it.weather?.drawableId=R.drawable.s05d
                621->it.weather?.drawableId=R.drawable.s01d
                622->it.weather?.drawableId=R.drawable.s02d
                623->it.weather?.drawableId=R.drawable.s06d
                700->it.weather?.drawableId=R.drawable.a01d
                711->it.weather?.drawableId=R.drawable.a02d
                721->it.weather?.drawableId=R.drawable.a03d
                731->it.weather?.drawableId=R.drawable.a04d
                741->it.weather?.drawableId=R.drawable.a05d
                751->it.weather?.drawableId=R.drawable.a05d
                800->it.weather?.drawableId=R.drawable.ic_2_small
                804->it.weather?.drawableId=R.drawable.ic_4_small
                803->it.weather?.drawableId=R.drawable.ic_4_small
            }

        }
     //   initView()
    }

    override fun onPermissionGranted(requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onPermissionDenied(requestCode: Int) {
        TODO("Not yet implemented")
    }

    fun setResourceString()
    {
        windLabelTextView?.text=getResourceString("label_wind")
        humidityTextView?.text=getResourceString("label_humidity")
        precipitationLabelTextView?.text=getResourceString("label_precipitation")
        windButton?.text=getResourceString("label_wind")
        humidityButton?.text=getResourceString("weatherTemperature")
        precipitationButton?.text=getResourceString("label_precipitation")
    }
    fun initView()
    {
        weatherDetailList.forEach {
            if (DateUtils.toDisplayDateHour(it.timestamp_local).
                    equals(DateUtils.toDisplayDateHour(DateUtils.getTodayDate())))
            {
                precipitationTextView?.text=it.pop.toString().plus("%")
                dispatchedQtyTextView?.text=it.rh.toString().plus("%")
                windTextView?.text=String.format("%.2f",it.wind_spd?.times(3.6)).plus("km/h")
                cloudTextView?.text= it.temp?.let { it1 -> DateUtils.convertCelsiusToFahrenheit(it1) }

                val ti=DateUtils.toDisplayTimeWeatherAM(it.timestamp_local)
                if (ti.trim().equals("AM") || ti.trim().equals("am"))
                {
                    dateDisplayAMTextView?.text=DateUtils.toDisplayTimeWeather1(it.timestamp_local).plus(getResourceString("am"))
                }else{
                    dateDisplayAMTextView?.text=DateUtils.toDisplayTimeWeather1(it.timestamp_local).plus(getResourceString("pm"))
                }

                dateDisplayTextView?.text=DateUtils.toDisplayDateWeather(it.timestamp_local)
                if (ti.trim().equals("AM") || ti.trim().equals("am"))
                {
                    dateDisplayAMTextView.text=DateUtils.toDisplayTimeWeather1(it.timestamp_local).plus(getResourceString("am"))
                }else{
                    dateDisplayAMTextView?.text=DateUtils.toDisplayTimeWeather1(it.timestamp_local).plus(getResourceString("pm"))
                }
            }

            placeTextView?.text=AppPreferences.getInstance()?.getStringSharedPreference(GMKeys.Village, ""
                    ?: "")


        }


precipitationButton?.setOnClickListener {
    context?.resources?.getColor(R.color.white)?.let { it1 -> windButton?.setBackgroundColor(it1) }
    context?.resources?.getColor(R.color.white)?.let { it1 -> humidityButton?.setBackgroundColor(it1) }
    context?.resources?.getColor(R.color.green_shade_1)?.let { it1 -> precipitationButton?.setBackgroundColor(it1) }
    initAdapter(weatherDetailList,1)

}
        humidityButton?.setOnClickListener {
            context?.resources?.getColor(R.color.white)?.let { it1 -> windButton?.setBackgroundColor(it1) }
            context?.resources?.getColor(R.color.green_shade_1)?.let { it1 -> humidityButton?.setBackgroundColor(it1) }
            context?.resources?.getColor(R.color.white)?.let { it1 -> precipitationButton?.setBackgroundColor(it1) }
            initAdapter(weatherDetailList,2)
        }
        windButton?.setOnClickListener {
            context?.resources?.getColor(R.color.green_shade_1)?.let { it1 -> windButton?.setBackgroundColor(it1) }
            context?.resources?.getColor(R.color.white)?.let { it1 -> humidityButton?.setBackgroundColor(it1) }
            context?.resources?.getColor(R.color.white)?.let { it1 -> precipitationButton?.setBackgroundColor(it1) }
            initAdapter(weatherDetailList,3)
        }

        }

    fun initAdapter(list:ArrayList<Model.DailyWeatherData>,key:Int) {
        val weatherList=ArrayList<Model.DailyWeatherData>()
        weatherList.addAll(weatherDetailList)
        val dateFormat = SimpleDateFormat(DateUtils.SERVER_FORMAT_DATE_TIME_TRIMMED)
        Collections.sort(weatherList) { o1, o2 -> dateFormat.parse(o1.timestamp_local).compareTo(dateFormat.parse(o2.timestamp_local)) }
        weatherRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        weatherRecyclerView?.adapter = WeatherAdapter(weatherList,key,context!!,this)
    }
}

