package com.uddesh.tiffinservicebackend.datamodels

data class PersonalDetailsModel(
    val providername : String,
    val contactno : String,
    val upiid : String,
    val vegnonveg : Boolean,
    val lunch : Boolean,
    val dinner : Boolean,
    val lunchtimefrom : String,
    val lunchtimeto : String,
    val dinnertimefrom : String,
    val dinnertimeto : String,
    val logoimage : String
)
