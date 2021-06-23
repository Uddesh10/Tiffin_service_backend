package com.uddesh.tiffinservicebackend.datamodels

data class SubscribedServiceDetailsModel(
    val id : Int,
    val providername : String,
    val servicename : String,
    val servicedescription : String,
    val daysremaining : Int,
    val logoimage : String,
    val foodimage : String,
    val active : Boolean,
    val upiid : String
)
