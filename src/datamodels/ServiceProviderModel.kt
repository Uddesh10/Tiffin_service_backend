package com.uddesh.tiffinservicebackend.datamodels

data class ServiceProviderModel(
    val username : String,
    val providername : String,
    val vegnonveg : Boolean,
    val logoimage : String,
    val availability : String,
    val startingprice : Int,
    val distance : Int
)
