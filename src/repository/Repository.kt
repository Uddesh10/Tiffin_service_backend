package com.uddesh.tiffinservicebackend.repository

import com.uddesh.tiffinservicebackend.database.AdminDetailsEntity
import com.uddesh.tiffinservicebackend.database.UserDetailsEntity
import com.uddesh.tiffinservicebackend.datamodels.*

interface Repository {
    // post
    fun postSignup(signupModel : SignupModel) : Boolean
    fun postLogin(loginModel : LoginModel) : UserDetailsEntity?
    fun postAdminLogin(loginModel : LoginModel) : AdminDetailsEntity?
    fun postSubscribeService(details : SubscribeServiceModel) : Boolean
    fun postAdminSignup(signupModel : AdminSignupModel) : Boolean
    fun postAddService(details : AddServiceModel) : Int
    // put
      fun putActiveService(id : Int , active : Boolean)
      fun putRenewService(id : Int , days : Int) : Boolean
      fun putUserLocation(username : String , locationModel : LocationModel) :  Boolean
      fun putAdminLocation(username : String , locationModel : LocationModel) :  Boolean
      fun putPersonalDetails(username : String , details : PersonalDetailsModel)
      fun putService(id : Int , details : AddServiceModel)
    //get
      fun getAllSubscribedService(username : String) : List<SubscribedServiceModel>
      fun getSubscribedService(id : Int) : List<SubscribedServiceDetailsModel>
      fun getAllServiceProvider(username: String) : List<ServiceProviderModel>
      fun getServiceProvider(username : String , providerUsername : String) : List<ServiceProviderDetailsModel>
      fun getService(id : Int) : List<ServiceDetailsModel>
      fun getUserDetails(username : String) : List<UserDetails>
      fun getAdminService(username : String) : List<AdminServiceModel>
      fun getAdminPersonalDetails(username : String) : List<PersonalDetailsModel>
      fun getAdminService(id : Int) : List<AddServiceModel>
    //delete
      fun deleteService(id : Int) : Boolean

      // helper function
      fun updateFoodImage(name : String , id : Int)
}