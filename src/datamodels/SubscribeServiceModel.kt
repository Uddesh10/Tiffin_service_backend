package com.uddesh.tiffinservicebackend.datamodels

data class  SubscribeServiceModel(
    val username : String,
    val serviceid : Int,
    val daysremaining : Int,
    val active : Boolean
)
