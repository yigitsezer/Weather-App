package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Location
import com.yigitsezer.weatherapp.data.network.model.LocationNetworkEntity

object LocationEntityMapper: EntityMapper<Location, LocationNetworkEntity> {
    override fun mapToEntity(domain: Location): LocationNetworkEntity {
        return LocationNetworkEntity(
            distance = 0,
            title = domain.name,
            locationType = "City",
            woeid = domain.woeid,
            lattLong = domain.lattLong
        )
    }

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

    fun toEntityList(data: List<Location>): List<LocationNetworkEntity> {
        return data.map { mapToEntity(it) }
    }
}