package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.model.LocationNetworkEntity

object LocationEntityMapper: EntityMapper<Location, LocationNetworkEntity> {
    override fun mapToDomain(entity: LocationNetworkEntity): Location {
        return Location(
            distance = entity.distance,
            name = entity.title,
            woeid = entity.woeid,
            lattLong = entity.lattLong
        )
    }

    fun fromEntityList(entities: List<LocationNetworkEntity>): List<Location> {
        return entities.map { mapToDomain(it) }
    }
}