package com.yigitsezer.weatherapp.data.network.model

import com.google.gson.annotations.SerializedName

class LocationNetworkEntity(

    @SerializedName(value = "distance")
    var distance: Int? = null,

    @SerializedName(value = "title")
    var title: String? = null,

    @SerializedName(value = "location_type")
    var locationType: String? = null,

    @SerializedName(value = "woeid")
    var woeid: Int,

    @SerializedName(value = "latt_long")
    var lattLong: String? = null
)