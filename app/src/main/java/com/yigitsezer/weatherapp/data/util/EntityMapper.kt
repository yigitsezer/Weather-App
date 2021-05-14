package com.yigitsezer.weatherapp.data.util

interface EntityMapper <Domain, Entity> {
    //fun mapToEntity(domain: Domain): Entity

    fun mapToDomain(entity: Entity): Domain
}