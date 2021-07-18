package com.uddesh.tiffinservicebackend.repository

import com.uddesh.tiffinservicebackend.database.AdminDetailsEntity
import com.uddesh.tiffinservicebackend.database.DatabaseManager
import com.uddesh.tiffinservicebackend.database.UserDetailsEntity
import com.uddesh.tiffinservicebackend.datamodels.*

class RepositoryImplementation : Repository {

    private val database = DatabaseManager()
    override fun postSignup(signupModel: SignupModel): Boolean {
        return database.userSignup(signupModel)

    }

    override fun postLogin(loginModel: LoginModel): UserDetailsEntity? {
        return database.userLogin(loginModel)

    }

    override fun postAdminLogin(loginModel: LoginModel): AdminDetailsEntity? {
        return database.adminLogin(loginModel)
    }

    override fun postSubscribeService(details: SubscribeServiceModel): Boolean {
        return database.addSubscribedService(details)
    }

    override fun postAdminSignup(signupModel: AdminSignupModel): Boolean{
        return database.adminSignup(signupModel)
    }

    override fun postAddService(details: AddServiceModel) : Int{
        return database.addService(details)
    }

    override fun putActiveService(id: Int, active: Boolean) {
        database.updateActiveService(id , active)
    }

    override fun putRenewService(id: Int, days: Int) : Boolean {
        return database.renewService(id , days)
    }

    override fun putUserLocation(username : String ,locationModel: LocationModel): Boolean {
        return database.updateUserLocation(username , locationModel)
    }

    override fun putAdminLocation(username: String, locationModel: LocationModel): Boolean {
        return database.updateAdminLocation(username , locationModel)
    }

    override fun putPersonalDetails(username : String , details: PersonalDetailsModel) {
         database.updatePersonalDetails(username , details)
    }

    override fun putService(id: Int, details: AddServiceModel) {
        database.updateService(id , details)
    }

    override fun putServiceStatus(id: Int, active: Boolean) {
        database.updateServiceStatus(id , active)
    }

    override fun getAllSubscribedService(username: String): List<SubscribedServiceModel> {
        return database.getAllSubscribedService(username)
    }

    override fun getSubscribedService(id: Int): List<SubscribedServiceDetailsModel> {
        return database.getSubscribedService(id)
    }

    override fun getAllServiceProvider(username: String): List<ServiceProviderModel> {
        return database.getAllServiceProvider(username)
    }

    override fun getServiceProvider(username : String , providerUsername: String): List<ServiceProviderDetailsModel> {
        return database.getServiceProvider(username , providerUsername)
    }

    override fun getService(id: Int): List<ServiceDetailsModel> {
        return database.getService(id)
    }

    override fun getUserDetails(username: String): List<UserDetails> {
        return database.getUserDetails(username)
    }

    override fun getAdminService(username: String): List<AdminServiceModel> {
        return database.getAdminService(username)
    }

    override fun getAdminService(id: Int): List<AddServiceModel> {
        return database.getAdminService(id)
    }

    override fun getAdminPersonalDetails(username: String): List<PersonalDetailsModel> {
        return database.getPersonalDetails(username)
    }

    override fun deleteService(id: Int): Boolean {
        return database.deleteService(id)
    }

    override fun updateFoodImage(name: String, id: Int) {
        database.updateFoodImage(name , id)
    }
}