package com.yigitsezer.weatherapp.data.network.model

import com.google.gson.annotations.SerializedName

class WeatherNetworkEntity (

    @SerializedName(value = "consolidated_weather")
    var consolidatedWeather: List<ForecastNetworkEntity>,

    @SerializedName(value = "time")
    var time: String? = null,

    @SerializedName(value = "sun_rise")
    var sunRise: String? = null,

    @SerializedName(value = "sun_set")
    var sunSet: String? = null,

    @SerializedName(value = "timezone_name")
    var timezoneName: String? = null,

    @SerializedName(value = "parent")
    var parent: ParentNetworkEntity,

    @SerializedName(value = "title")
    var title: String? = null,

    @SerializedName(value = "location_type")
    var locationType: String? = null,

    @SerializedName(value = "woeid")
    var woeid: Int? = null,

    @SerializedName(value = "latt_long")
    var lattLong: String? = null,

    @SerializedName(value = "timezone")
    var timezone: String? = null
)