package com.uddesh.tiffinservicebackend.datamodels

data class ServiceDetailsModel(
    val id : Int,
    val foodimage : String,
    val servicename : String,
    val price : Int,
    val description : String,
    val upiid : String
)