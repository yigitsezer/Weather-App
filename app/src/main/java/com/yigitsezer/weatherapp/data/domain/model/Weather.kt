package com.yigitsezer.weatherapp.data.domain.model

class Weather (
    var consolidatedWeather:List<Forecast>? = null,
    var time: String? = null,
    var sunRise: String? = null,
    var sunSet: String? = null,
    var timezoneName: String? = null,
    var parent: Parent? = null,
    var title: String? = null,
    var locationType: String? = null,
    var woeid: Int? = null,
    var lattLong: String? = null,
    var timezone: String? = null
)