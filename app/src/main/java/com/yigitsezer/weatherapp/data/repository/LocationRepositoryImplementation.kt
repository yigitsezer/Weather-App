package com.yigitsezer.weatherapp.data.repository

import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.WeatherApiService
import com.yigitsezer.weatherapp.data.util.LocationEntityMapper

class LocationRepositoryImplementation(
    private val service: WeatherApiService,
): LocationRepository {
    override suspend fun search(latitude: Double, longitude: Double): List<Location> {
        return LocationEntityMapper.fromEntityList(service.search("${latitude},${longitude}"))
    }
}