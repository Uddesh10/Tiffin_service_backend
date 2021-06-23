package com.uddesh.tiffinservicebackend.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.varchar


object UserDetails : Table<UserDetailsEntity>("UserDetails") {

    val username = varchar("username").primaryKey().bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val fullname = varchar("fullname").bindTo { it.fullname }
    val mobileno = varchar("mobileno").bindTo { it.mobileno }
    val location = varchar("location").bindTo { it.location }
    val longitude = double("longitude").bindTo { it.longitude }
    val latitude = double("latitude").bindTo { it.latitude }


}

interface UserDetailsEntity : Entity<UserDetailsEntity> {

    companion object :  Entity.Factory<UserDetailsEntity>()

    val username : String
    val password : String
    val fullname : String
    val mobileno : String
    val location : String
    val longitude : Double
    val latitude : Double

}