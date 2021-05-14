package com.yigitsezer.weatherapp.data.util

import com.yigitsezer.weatherapp.data.domain.model.Parent
import com.yigitsezer.weatherapp.data.network.model.ParentNetworkEntity

object ParentEntityMapper: EntityMapper<Parent, ParentNetworkEntity> {
    override fun mapToDomain(entity: ParentNetworkEntity): Parent {
        return Parent(
            title = entity.title,
            locationType = entity.locationType,
            woeid = entity.woeid,
            lattLong = entity.lattLong
        )
    }

}