package com.yigitsezer.weatherapp.data.repository

import com.yigitsezer.weatherapp.data.domain.model.Location

interface LocationRepository {
    suspend fun search(latitude: Double, longitude: Double): List<Location>
}