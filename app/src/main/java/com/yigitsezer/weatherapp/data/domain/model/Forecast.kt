package com.yigitsezer.weatherapp.data.domain.model

class Forecast (
    var id: Long? = null,
    var weatherStateName: String? = null,
    var weatherStateAbbr: String? = null,
    var windDirectionCompass: String? = null,
    var createdAt: String? = null,
    var applicableDate: String? = null,
    var minTemp: Double? = null,
    var maxTemp: Double? = null,
    var theTemp: Double? = null,
    var windSpeed: Double? = null,
    var windDirection: Double? = null,
    var airPressure: Double? = null,
    var humidity: Int? = null,
    var visibility: Double? = null,
    var predictability: Int? = null
)