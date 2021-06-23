package com.uddesh.tiffinservicebackend.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.double
import org.ktorm.schema.varchar


object AdminDetails : Table<AdminDetailsEntity>("AdminDetails") {

    val username = varchar("username").primaryKey().bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val mobileno = varchar("mobileno").bindTo { it.mobileno }
    val location = varchar("location").bindTo { it.location }
    val longitude = double("longitude").bindTo { it.longitude }
    val latitude = double("latitude").bindTo { it.latitude }
    val providername = varchar("providername").bindTo { it.providername }
    val contactno = varchar("contactno").bindTo { it.contactno }
    val upiid = varchar("upiid").bindTo { it.upiid }
    val vegnonveg = boolean("vegnonveg").bindTo { it.vegnonveg }
    val lunch = boolean("lunch").bindTo { it.lunch }
    val dinner = boolean("dinner").bindTo { it.dinner }
    val lunchtimefrom = varchar("lunchtimefrom").bindTo { it.lunchtimefrom }
    val lunchtimeto = varchar("lunchtimeto").bindTo { it.lunchtimeto }
    val dinnertimefrom = varchar("dinnertimefrom").bindTo { it.dinnertimefrom }
    val dinnertimeto = varchar("dinnertimeto").bindTo { it.dinnertimeto }
    val logoimage = varchar("logoimage").bindTo { it.logoimage }
}

interface AdminDetailsEntity : Entity<AdminDetailsEntity>{

    companion object :  Entity.Factory<AdminDetailsEntity>()

    val username : String
    val password : String
    val mobileno : String
    val location : String
    val longitude : Double
    val latitude : Double
    val providername : String
    val contactno : String
    val upiid : String
    val vegnonveg : Boolean
    val lunch : Boolean
    val dinner : Boolean
    val lunchtimefrom : String
    val lunchtimeto : String
    val dinnertimefrom : String
    val dinnertimeto : String
    val logoimage : String

}