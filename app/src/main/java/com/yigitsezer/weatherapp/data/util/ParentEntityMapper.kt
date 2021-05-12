package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Parent
import com.yigitsezer.weatherapp.data.network.model.ParentNetworkEntity

object ParentEntityMapper: EntityMapper<Parent, ParentNetworkEntity> {
    override fun mapToEntity(domain: Parent): ParentNetworkEntity {
        TODO("Not yet implemented, POST requests do not exist for this app")
    }

    override fun mapToDomain(entity: ParentNetworkEntity): Parent {
        return Parent(
            title = entity.title,
            locationType = entity.locationType,
            woeid = entity.woeid,
            lattLong = entity.lattLong
        )
    }

}