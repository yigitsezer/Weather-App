package com.yigitsezer.weatherapp.data.repository

import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.util.WeatherEntityMapper

class WeatherRepositoryImplementation(
    private val service: WeatherApiService
): WeatherRepository {
    override suspend fun get(woeid: Int): Weather {
        return WeatherEntityMapper.mapToDomain(service.get(woeid))
    }

}