package com.uddesh.tiffinservicebackend.database

import com.uddesh.tiffinservicebackend.datamodels.*
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import kotlin.math.*

class DatabaseManager {
    private val hostname = "127.0.0.1:5432"
    private val databaseName = "TiffinServiceDB"
    private val username = "postgres"
    private val password = System.getenv("DB_PASSWORD")

    private val database: Database

    init {
        val jdbcURL = "jdbc:postgresql://$hostname/$databaseName?user=$username&password=$password&ssl=false"
        database = Database.connect(jdbcURL)
    }

    fun userSignup(model: SignupModel): Boolean {
        try {
            database.insert(UserDetails) {
                set(UserDetails.username, model.username)
                set(UserDetails.fullname, model.fullname)
                set(UserDetails.password, model.password)
                set(UserDetails.mobileno, model.mobileno)
                set(UserDetails.location, " ")
                set(UserDetails.latitude, 0.0)
                set(UserDetails.longitude, 0.0)
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun userLogin(model: LoginModel): UserDetailsEntity? {
        return database.sequenceOf(UserDetails)
            .firstOrNull { (it.username eq model.username) and (it.password eq model.password) }
    }

    fun adminLogin(model: LoginModel): AdminDetailsEntity? {
        return try {
            database.sequenceOf(AdminDetails)
                .firstOrNull { (it.username eq model.username) and (it.password eq model.password) }
        } catch (e: Exception) {
            null
        }
    }

    fun updateUserLocation(username: String, model: LocationModel): Boolean {
        val result = database.update(UserDetails) {
            set(UserDetails.location, model.location)
            set(UserDetails.latitude, model.latitude)
            set(UserDetails.longitude, model.longitude)
            where {
                it.username eq username
            }
        }
        return result > 0
    }

    fun updateAdminLocation(username: String, model: LocationModel): Boolean {
        val result = database.update(AdminDetails) {
            set(UserDetails.location, model.location)
            set(UserDetails.latitude, model.latitude)
            set(UserDetails.longitude, model.longitude)
            where {
                it.username eq username
            }
        }
        return result > 0
    }

    fun adminSignup(model: AdminSignupModel): Boolean {
        try {
            database.insert(AdminDetails) {
                set(AdminDetails.username, model.username)
                set(AdminDetails.password, model.password)
                set(AdminDetails.mobileno, model.mobileno)
                set(AdminDetails.location, " ")
                set(AdminDetails.latitude, 0.0)
                set(AdminDetails.longitude, 0.0)
                set(AdminDetails.providername, " ")
                set(AdminDetails.contactno, " ")
                set(AdminDetails.upiid, " ")
                set(AdminDetails.vegnonveg, false)
                set(AdminDetails.lunch, false)
                set(AdminDetails.dinner, false)
                set(AdminDetails.lunchtimefrom, " ")
                set(AdminDetails.lunchtimeto, " ")
                set(AdminDetails.dinnertimefrom, " ")
                set(AdminDetails.dinnertimeto, " ")
                set(AdminDetails.logoimage, " ")
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun updatePersonalDetails(username: String, model: PersonalDetailsModel): Boolean {
        val result = database.update(AdminDetails) {
            set(AdminDetails.providername, model.providername)
            set(AdminDetails.contactno, model.contactno)
            set(AdminDetails.upiid, model.upiid)
            set(AdminDetails.vegnonveg, model.vegnonveg)
            set(AdminDetails.lunch, model.lunch)
            set(AdminDetails.dinner, model.dinner)
            set(AdminDetails.lunchtimefrom, model.lunchtimefrom)
            set(AdminDetails.lunchtimeto, model.lunchtimeto)
            set(AdminDetails.dinnertimefrom, model.dinnertimefrom)
            set(AdminDetails.dinnertimeto, model.dinnertimeto)
            set(AdminDetails.logoimage, model.logoimage)
            where {
                it.username eq username
            }
        }
        return result > 0
    }

    fun addService(model: AddServiceModel): Int {
        return database.insertAndGenerateKey(ServiceInfo) {
            set(ServiceInfo.foodimage, " ")
            set(ServiceInfo.active, model.active)
            set(ServiceInfo.name, model.servicename)
            set(ServiceInfo.username, model.username)
            set(ServiceInfo.description, model.description)
            set(ServiceInfo.price, model.price)
        } as Int
    }

    fun updateFoodImage(name: String, id: Int) {
        database.update(ServiceInfo) {
            set(ServiceInfo.foodimage, name)
            where {
                it.serviceid eq id
            }
        }
    }

    fun addSubscribedService(model: SubscribeServiceModel): Boolean {
        try {
            database.insertAndGenerateKey(SubscribedUser) {
                set(SubscribedUser.username, model.username)
                set(SubscribedUser.seriviceid, model.serviceid)
                set(SubscribedUser.daysremaining, model.daysremaining)
                set(SubscribedUser.active, model.active)
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun updateActiveService(id: Int, value: Boolean) {
        database.update(SubscribedUser) {
            set(SubscribedUser.active, value)
            where {
                it.id eq id
            }
        }
    }

    fun updateServiceStatus(id : Int , value : Boolean){
        database.update(ServiceInfo){
            set(ServiceInfo.active , value)
            where {
                it.serviceid eq id
            }
        }
    }

    fun renewService(id: Int, days: Int): Boolean {
        var query = false
        database.from(SubscribedUser)
            .innerJoin(ServiceInfo, on = ServiceInfo.serviceid eq SubscribedUser.seriviceid)
            .select(ServiceInfo.active)
            .where {
                SubscribedUser.id eq id
            }
            .map { row ->
                query = row[ServiceInfo.active]!!
            }
        if (query) {
            database.update(SubscribedUser) {
                set(SubscribedUser.daysremaining, days)
                where {
                    it.id eq id
                }
            }
            return query
        }
        return query
    }

    fun updateService(id: Int, model: AddServiceModel) {
        database.update(ServiceInfo) {
            set(ServiceInfo.foodimage, model.foodimage)
            set(ServiceInfo.active, model.active)
            set(ServiceInfo.name, model.servicename)
            set(ServiceInfo.username, model.username)
            set(ServiceInfo.description, model.description)
            set(ServiceInfo.price, model.price)
            where {
                it.serviceid eq id
            }
        }
    }

    fun deleteService(id: Int): Boolean {
        val result = database.sequenceOf(SubscribedUser)
            .filter { it.seriviceid eq id }
            .count()
        if (result == 0) {
            database.delete(ServiceInfo) {
                it.serviceid eq id
            }
            return true
        }
        return false
    }

    fun getAllSubscribedService(username: String): List<SubscribedServiceModel> {
        return database.from(UserDetails)
            .innerJoin(SubscribedUser, on = UserDetails.username eq SubscribedUser.username)
            .innerJoin(ServiceInfo, on = ServiceInfo.serviceid eq SubscribedUser.seriviceid)
            .innerJoin(AdminDetails, on = AdminDetails.username eq ServiceInfo.username)
            .select(
                SubscribedUser.id,
                AdminDetails.providername,
                ServiceInfo.name,
                AdminDetails.logoimage,
                SubscribedUser.active
            )
            .where { UserDetails.username eq username }
            .map { row ->
                SubscribedServiceModel(
                    id = row[SubscribedUser.id]!!,
                    providername = row[AdminDetails.providername]!!,
                    servicename = row[ServiceInfo.name]!!,
                    logoimage = row[AdminDetails.logoimage]!!,
                    active = row[SubscribedUser.active]!!
                )
            }
    }

    fun getSubscribedService(id: Int): List<SubscribedServiceDetailsModel> {
        return database.from(SubscribedUser)
            .innerJoin(ServiceInfo, on = SubscribedUser.seriviceid eq ServiceInfo.serviceid)
            .innerJoin(AdminDetails, on = ServiceInfo.username eq AdminDetails.username)
            .select(
                SubscribedUser.id,
                AdminDetails.providername,
                ServiceInfo.name,
                ServiceInfo.description,
                SubscribedUser.daysremaining,
                AdminDetails.logoimage,
                ServiceInfo.foodimage,
                SubscribedUser.active,
                AdminDetails.upiid
            )
            .where {
                SubscribedUser.id eq id
            }
            .map { row ->
                SubscribedServiceDetailsModel(
                    id = row[SubscribedUser.id]!!,
                    providername = row[AdminDetails.providername]!!,
                    servicename = row[ServiceInfo.name]!!,
                    servicedescription = row[ServiceInfo.description]!!,
                    daysremaining = row[SubscribedUser.daysremaining]!!,
                    logoimage = row[AdminDetails.logoimage]!!,
                    foodimage = row[ServiceInfo.foodimage]!!,
                    active = row[SubscribedUser.active]!!,
                    upiid = row[AdminDetails.upiid]!!
                )
            }
    }

    fun getAdminService(username: String): List<AdminServiceModel> {
        return database.from(ServiceInfo)
            .select(ServiceInfo.serviceid, ServiceInfo.name, ServiceInfo.active)
            .where {
                ServiceInfo.username eq username
            }
            .map { row ->
                AdminServiceModel(
                    id = row[ServiceInfo.serviceid]!!,
                    servicename = row[ServiceInfo.name]!!,
                    active = row[ServiceInfo.active]!!,
                    subscribed = getSubscriberCount(row[ServiceInfo.serviceid]!!)
                )
            }
    }

    private fun getSubscriberCount(id: Int): Int {
        return database.sequenceOf(SubscribedUser)
            .filter {
                SubscribedUser.seriviceid eq id
            }
            .count()

    }

    fun getUserDetails(username: String): List<com.uddesh.tiffinservicebackend.datamodels.UserDetails> {
        return database.from(ServiceInfo)
            .innerJoin(SubscribedUser, on = SubscribedUser.seriviceid eq ServiceInfo.serviceid)
            .innerJoin(UserDetails, on = SubscribedUser.username eq UserDetails.username)
            .select(
                UserDetails.fullname,
                ServiceInfo.name,
                UserDetails.mobileno,
                UserDetails.location,
                UserDetails.longitude,
                UserDetails.latitude
            )
            .where {
                ServiceInfo.username eq username
            }
            .map { row ->
                UserDetails(
                    fullname = row[UserDetails.fullname]!!,
                    servicename = row[ServiceInfo.name]!!,
                    contactno = row[UserDetails.mobileno]!!,
                    location = row[UserDetails.location]!!,
                    latitude = row[UserDetails.latitude]!!,
                    longitude = row[UserDetails.longitude]!!
                )
            }
    }

    fun getAllServiceProvider(username: String): List<ServiceProviderModel> {
        val result = database.from(AdminDetails)
            .select(
                AdminDetails.username,
                AdminDetails.providername,
                AdminDetails.vegnonveg,
                AdminDetails.logoimage,
                AdminDetails.lunch,
                AdminDetails.dinner,
                AdminDetails.longitude,
                AdminDetails.latitude
            )
            .map { row ->

                ServiceProviderModel(
                    username = row[AdminDetails.username]!!,
                    providername = row[AdminDetails.providername]!!,
                    vegnonveg = row[AdminDetails.vegnonveg]!!,
                    logoimage = row[AdminDetails.logoimage]!!,
                    availability = availability(row[AdminDetails.lunch]!!, row[AdminDetails.dinner]!!),
                    distance = distance(username, row[AdminDetails.longitude]!!, row[AdminDetails.latitude]!!),
                    startingprice = startingPrice(row[AdminDetails.username]!!)
                )

            }
        return result.sortedBy { it.distance }
    }

    private fun availability(lunch: Boolean, dinner: Boolean): String {
        val result: String
        if (lunch && dinner) {
            result = "lunch , dinner"
        } else if (lunch) {
            result = "lunch"
        } else {
            result = "dinner"
        }
        return result
    }

    private fun distance(username: String, longitude: Double, latitude: Double): Int {
        val query = database.sequenceOf(UserDetails)
            .firstOrNull { it.username eq username }

        var userLongitude = query!!.longitude
        var userLatitude = query.latitude
        val adminLongitude = Math.toRadians(longitude)
        val adminLatitude = Math.toRadians(latitude)
        userLatitude = Math.toRadians(userLatitude)
        userLongitude = Math.toRadians(userLongitude)
        val dlat = userLatitude - adminLatitude
        val dlong = userLongitude - adminLongitude
        val a = sin(dlat / 2).pow(2.0) + cos(userLatitude) * cos(adminLatitude) * sin(dlong / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))
        val r = 6371
        return (c * r).toInt()
    }

    private fun startingPrice(username: String): Int {
        var result = 0
        database.from(ServiceInfo)
            .select(min(ServiceInfo.price))
            .where {
                ServiceInfo.username eq username
            }
            .map { row ->
                result = row.getInt(1)
            }
        return result

    }

    fun getServiceProvider(username: String, adminUsername: String): List<ServiceProviderDetailsModel> {
        return database.from(AdminDetails)
            .select()
            .where {
                AdminDetails.username eq adminUsername
            }
            .map { row ->

                ServiceProviderDetailsModel(
                    providername = row[AdminDetails.providername]!!,
                    vegnonveg = row[AdminDetails.vegnonveg]!!,
                    logoimage = row[AdminDetails.logoimage]!!,
                    availability = availability(row[AdminDetails.lunch]!!, row[AdminDetails.dinner]!!),
                    distance = distance(username, row[AdminDetails.longitude]!!, row[AdminDetails.latitude]!!),
                    lunchtimings = "Lunch : ${row[AdminDetails.lunchtimefrom]!!} - ${row[AdminDetails.lunchtimeto]!!}",
                    dinnertimings = "Dinner : ${row[AdminDetails.dinnertimefrom]!!} - ${row[AdminDetails.dinnertimeto]!!}",
                    contactno = row[AdminDetails.contactno]!!,
                    services = getServices(row[AdminDetails.username]!!)
                )

            }
    }

    private fun getServices(username: String): List<ServiceModel> {
        return database.from(ServiceInfo)
            .select(
                ServiceInfo.serviceid,
                ServiceInfo.foodimage,
                ServiceInfo.name,
                ServiceInfo.price
            )
            .where { (ServiceInfo.active eq true) and (ServiceInfo.username eq username) }
            .map { row ->

                ServiceModel(
                    id = row[ServiceInfo.serviceid]!!,
                    foodimage = row[ServiceInfo.foodimage]!!,
                    servicename = row[ServiceInfo.name]!!,
                    price = row[ServiceInfo.price]!!
                )
            }
    }

    fun getService(id: Int): List<ServiceDetailsModel> {
        return database.from(ServiceInfo)
            .innerJoin(AdminDetails, on = AdminDetails.username eq ServiceInfo.username)
            .select(
                ServiceInfo.serviceid,
                ServiceInfo.foodimage,
                ServiceInfo.name,
                ServiceInfo.price,
                ServiceInfo.description,
                AdminDetails.upiid
            )
            .where {
                ServiceInfo.serviceid eq id
            }
            .map { row ->

                ServiceDetailsModel(
                    id = row[ServiceInfo.serviceid]!!,
                    foodimage = row[ServiceInfo.foodimage]!!,
                    servicename = row[ServiceInfo.name]!!,
                    price = row[ServiceInfo.price]!!,
                    description = row[ServiceInfo.description]!!,
                    upiid = row[AdminDetails.upiid]!!
                )

            }
    }

    fun getPersonalDetails(username: String): List<PersonalDetailsModel> {
        return database.from(AdminDetails)
            .select(
                AdminDetails.providername,
                AdminDetails.contactno,
                AdminDetails.upiid,
                AdminDetails.vegnonveg,
                AdminDetails.lunch,
                AdminDetails.dinner,
                AdminDetails.lunchtimefrom,
                AdminDetails.lunchtimeto,
                AdminDetails.dinnertimefrom,
                AdminDetails.dinnertimeto,
                AdminDetails.logoimage
            )
            .where {
                AdminDetails.username eq username
            }
            .map { row ->
                PersonalDetailsModel(
                    providername = row[AdminDetails.providername]!!,
                    contactno = row[AdminDetails.contactno]!!,
                    upiid = row[AdminDetails.upiid]!!,
                    vegnonveg = row[AdminDetails.vegnonveg]!!,
                    lunch = row[AdminDetails.lunch]!!,
                    dinner = row[AdminDetails.dinner]!!,
                    lunchtimefrom = row[AdminDetails.lunchtimefrom]!!,
                    lunchtimeto = row[AdminDetails.lunchtimeto]!!,
                    dinnertimefrom = row[AdminDetails.dinnertimefrom]!!,
                    dinnertimeto = row[AdminDetails.dinnertimeto]!!,
                    logoimage = row[AdminDetails.logoimage]!!
                )
            }
    }

    fun getAdminService(id: Int): List<AddServiceModel>{
        return database.from(ServiceInfo)
            .select()
            .where {
                ServiceInfo.serviceid eq id
            }
            .map {row->
                AddServiceModel(
                    foodimage = row[ServiceInfo.foodimage]!!,
                    servicename = row[ServiceInfo.name]!!,
                    price = row[ServiceInfo.price]!!,
                    description = row[ServiceInfo.description]!!,
                    username = row[ServiceInfo.username]!!,
                    active = row[ServiceInfo.active]!!
                )
            }
    }
}