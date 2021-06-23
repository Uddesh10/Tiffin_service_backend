package com.uddesh.tiffinservicebackend.datamodels

data class UserDetails(
    val fullname : String,
    val servicename : String,
    val contactno : String,
    val longitude : Double,
    val latitude : Double,
    val location : String
)
