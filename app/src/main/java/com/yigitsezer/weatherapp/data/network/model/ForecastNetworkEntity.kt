package com.yigitsezer.weatherapp.data.network.model

import com.google.gson.annotations.SerializedName

class ForecastNetworkEntity (

    @SerializedName(value = "id")
    var id: Long? = null,

    @SerializedName(value = "weather_state_name")
    var weatherStateName: String? = null,

    @SerializedName(value = "weather_state_abbr")
    var weatherStateAbbr: String? = null,

    @SerializedName(value = "wind_direction_compass")
    var windDirectionCompass: String? = null,

    @SerializedName(value = "created")
    var createdAt: String? = null,

    @SerializedName(value = "applicable_date")
    var applicableDate: String? = null,

    @SerializedName(value = "min_temp")
    var minTemp: Double? = null,

    @SerializedName(value = "max_temp")
    var maxTemp: Double? = null,

    @SerializedName(value = "the_temp")
    var theTemp: Double? = null,

    @SerializedName(value = "wind_speed")
    var windSpeed: Double? = null,

    @SerializedName(value = "wind_direction")
    var windDirection: Double? = null,

    @SerializedName(value = "air_pressure")
    var airPressure: Double? = null,

    @SerializedName(value = "humidity")
    var humidity: Int? = null,

    @SerializedName(value = "visibility")
    var visibility: Double? = null,

    @SerializedName(value = "predictability")
    var predictability: Int? = null
)