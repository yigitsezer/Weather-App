package com.yigitsezer.weatherapp.views

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yigitsezer.weatherapp.R

class ForecastView(context: Context?) : LinearLayout(context) {
    private var dayName: TextView
    private var humidity: TextView
    private var weatherState: ImageView
    private var maxMinTemp: TextView

    init {
        inflate(context, R.layout.forecast_view, this)
        dayName = findViewById(R.id.location_name)
        humidity = findViewById(R.id.humidity)
        weatherState = findViewById(R.id.weather_state)
        maxMinTemp = findViewById(R.id.max_min_temp)
    }
}