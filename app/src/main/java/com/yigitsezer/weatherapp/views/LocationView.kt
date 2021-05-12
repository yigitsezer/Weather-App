package com.yigitsezer.weatherapp.views

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.yigitsezer.weatherapp.R

class LocationView(context: Context?) : LinearLayout(context) {
    private var locationName: TextView

    init {
        inflate(context, R.layout.location_view, this)
        locationName = findViewById(R.id.location_name)
    }
}