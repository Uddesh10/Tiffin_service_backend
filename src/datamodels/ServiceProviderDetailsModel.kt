package com.uddesh.tiffinservicebackend.datamodels

data class ServiceProviderDetailsModel(
    val providername : String,
    val vegnonveg : Boolean,
    val logoimage : String,
    val availability : String,
    val distance : Int,
    val lunchtimings : String,
    val dinnertimings : String,
    val contactno : String,
    val services : List<ServiceModel>
)