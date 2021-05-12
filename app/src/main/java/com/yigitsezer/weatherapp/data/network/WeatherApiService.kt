package com.yigitsezer.weatherapp.data.network

import com.yigitsezer.weatherapp.data.network.model.LocationNetworkEntity
import com.yigitsezer.weatherapp.data.network.model.WeatherNetworkEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiService {

    @GET("location/search")
    suspend fun search(
        @Query("lattlong") lattLong: String
    ): List<LocationNetworkEntity>

    @GET("location/{woeid}")
    suspend fun get(
        @Path("woeid") woeid: Int
    ): WeatherNetworkEntity
}