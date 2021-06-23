package com.uddesh.tiffinservicebackend.database

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SubscribedUser : Table<SubscribedUserEntity>("SubscribedUser") {

    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val seriviceid = int("serviceid").bindTo { it.serviceid }
    val daysremaining = int("daysremaining").bindTo { it.daysremaining }
    val active = boolean("active").bindTo { it.active }
}

interface SubscribedUserEntity : Entity<SubscribedUserEntity>{

    companion object :  Entity.Factory<SubscribedUserEntity>()
    val id : Int
    val username : String
    val serviceid : Int
    val daysremaining : Int
    val active : Boolean
}