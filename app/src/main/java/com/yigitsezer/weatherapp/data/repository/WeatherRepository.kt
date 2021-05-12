package com.yigitsezer.weatherapp.data.repository

import com.yigitsezer.weatherapp.data.domain.model.Weather

interface WeatherRepository {
    suspend fun get(woeid: Int): Weather
}