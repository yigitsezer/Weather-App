package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Forecast
import com.yigitsezer.weatherapp.data.network.model.ForecastNetworkEntity

object ForecastEntityMapper: EntityMapper<Forecast, ForecastNetworkEntity> {
    override fun mapToEntity(domain: Forecast): ForecastNetworkEntity {
        TODO("Not yet implemented, POST requests do not exist for this app")
    }

    override fun mapToDomain(entity: ForecastNetworkEntity): Forecast {
        return Forecast(
            id = entity.id,
            weatherStateName = entity.weatherStateName,
            weatherStateAbbr = entity.weatherStateAbbr,
            windDirectionCompass = entity.windDirectionCompass,
            createdAt = entity.createdAt,
            applicableDate = entity.applicableDate,
            minTemp = entity.minTemp,
            maxTemp = entity.maxTemp,
            theTemp = entity.theTemp,
            windSpeed = entity.windSpeed,
            windDirection = entity.windDirection,
            airPressure = entity.airPressure,
            humidity = entity.humidity,
            visibility = entity.visibility,
            predictability = entity.predictability
        )
    }
}