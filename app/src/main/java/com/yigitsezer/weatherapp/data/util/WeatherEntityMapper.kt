package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Weather
import com.yigitsezer.weatherapp.data.network.model.WeatherNetworkEntity

object WeatherEntityMapper: EntityMapper<Weather, WeatherNetworkEntity> {
    override fun mapToEntity(domain: Weather): WeatherNetworkEntity {
        TODO("Not yet implemented, POST requests do not exist for this app")
    }

    override fun mapToDomain(entity: WeatherNetworkEntity): Weather {
        return Weather(
            consolidatedWeather = entity.consolidatedWeather.map { ForecastEntityMapper.mapToDomain(it) },
            time = entity.time,
            sunRise = entity.sunRise,
            sunSet = entity.sunSet,
            timezoneName = entity.timezoneName,
            parent = ParentEntityMapper.mapToDomain(entity.parent),
            title = entity.title,
            locationType = entity.locationType,
            woeid = entity.woeid,
            lattLong = entity.lattLong,
            timezone = entity.timezone,
        )
    }


}