package com.uddesh.tiffinservicebackend.datamodels

data class SubscribedServiceModel(
    val id : Int,
    val providername : String,
    val servicename : String,
    val logoimage : String,
    val active : Boolean
)
