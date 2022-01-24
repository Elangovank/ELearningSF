package com.gm.controllers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gm.R
import com.gm.controllers.fragments.WeatherFragment
import com.gm.models.Model
import com.gmcoreui.utils.DateUtils
import kotlinx.android.synthetic.main.item_weather.view.*

class WeatherAdapter(private var weatherList: ArrayList<Model.DailyWeatherData>, private var keyValue: Int, private var context: Context, var fragment: WeatherFragment)

    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false))
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = weatherList.get(position)
        if (keyValue == 1) {
            holder.itemView.celciusTextView.text = String.format("%.2f", item.pop).plus("%")
                    ?: 0.0.toString()
        } else if (keyValue == 2) {
            holder.itemView.celciusTextView.text = item.temp?.let { DateUtils.convertCelsiusToFahrenheit(it) }
        } else if (keyValue == 3) {
            holder.itemView.celciusTextView.text = String.format("%.2f", item.wind_spd?.times(3.6)).plus("km/h")
                    ?: 0.0.toString()
        } else {

        }

        weatherList[position].weather?.drawableId?.let { holder.itemView.weatherImageView.setImageResource(it) }
        if (DateUtils.comparedToTime(DateUtils.toDisplayTimeWeather(item.timestamp_local))) {
            holder.itemView.timeTextView.text = "Now"
        } else {
            if (item.timestamp_local != null) {
                holder.itemView.timeTextView.visibility = View.VISIBLE
                var ti = DateUtils.toDisplayTimeMaterialAM(item.timestamp_local)
                if (ti.trim().equals("AM") || ti.trim().equals("am")) {
                    holder.itemView.timeTextView.text = DateUtils.toDisplayTimeMaterial(item.timestamp_local).plus(" ").plus(fragment.getResourceString("am"))
                } else {
                    holder.itemView.timeTextView.text = DateUtils.toDisplayTimeMaterial(item.timestamp_local).plus(" ").plus(fragment.getResourceString("pm"))
                }
            } else {
                holder.itemView.timeTextView.visibility = View.GONE
            }
        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.celciusTextView
            itemView.timeTextView
            itemView.weatherImageView
        }
    }


}