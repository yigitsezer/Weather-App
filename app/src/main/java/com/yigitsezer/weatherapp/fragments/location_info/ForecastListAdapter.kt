package com.yigitsezer.weatherapp.fragments.location_info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yigitsezer.weatherapp.data.domain.model.Forecast
import com.yigitsezer.weatherapp.R

class ForecastListAdapter(private var list: List<Forecast>, private var dayList: List<String>):
    RecyclerView.Adapter<ForecastListAdapter.ForecastViewHolder>() {
    class ForecastViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var dayName: TextView = view.findViewById(R.id.day_name)
        var humidity: TextView = view.findViewById(R.id.humidity)
        var weatherState: ImageView = view.findViewById(R.id.weather_state)
        var maxMinTemp: TextView = view.findViewById(R.id.max_min_temp)

        fun setWeatherStateAbbr(abbr: String) {
            weatherState.setImageResource(
                when (abbr) {
                    "sn" -> R.drawable.sn
                    "sl" -> R.drawable.sl
                    "h" -> R.drawable.h
                    "t" -> R.drawable.t
                    "hr" -> R.drawable.hr
                    "lr" -> R.drawable.lr
                    "s" -> R.drawable.s
                    "hc" -> R.drawable.hc
                    "lc" -> R.drawable.lc
                    "c" -> R.drawable.c
                    else -> R.drawable.c
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_view, parent, false)

        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = list[position]
        holder.dayName.text = dayList[position]
        holder.humidity.text = forecast.humidity.toString()
        holder.setWeatherStateAbbr(forecast.weatherStateAbbr.toString())
        holder.maxMinTemp.text = "${forecast.maxTemp?.toInt()}°/${forecast.minTemp?.toInt()}° C"
    }

    override fun getItemCount(): Int {
        return list.size
    }

}