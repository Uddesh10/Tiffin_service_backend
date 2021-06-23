package com.uddesh.tiffinservicebackend.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object ServiceInfo : Table<ServiceInfoEntity>("ServiceInfo") {

    val serviceid = int("serviceid").primaryKey().bindTo { it.serviceid }
    val name = varchar("name").bindTo { it.name }
    val description = varchar("description").bindTo { it.description }
    val price = int("price").bindTo { it.price }
    val foodimage = varchar("foodimage").bindTo { it.foodimage }
    val username = varchar("username").bindTo { it.username }
    val active = boolean("active").bindTo { it.active }
}

interface ServiceInfoEntity : Entity<ServiceInfoEntity>{

    companion object :  Entity.Factory<ServiceInfoEntity>()

    val serviceid : Int
    val name : String
    val description : String
    val price : Int
    val foodimage : String
    val username : String
    val active : Boolean
}