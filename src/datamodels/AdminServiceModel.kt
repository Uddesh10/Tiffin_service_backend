package com.uddesh.tiffinservicebackend.datamodels

data class AdminServiceModel(
    val id  : Int,
    val servicename : String,
    val active : Boolean,
    val subscribed : Int
)
