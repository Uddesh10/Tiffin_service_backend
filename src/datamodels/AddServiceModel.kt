package com.uddesh.tiffinservicebackend.datamodels

data class AddServiceModel(
    val foodimage : String,
    val servicename : String,
    val price : Int,
    val description : String,
    val username : String,
    val active : Boolean
)
