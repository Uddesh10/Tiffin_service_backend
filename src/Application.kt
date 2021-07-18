package com.uddesh.tiffinservicebackend

import com.google.gson.Gson
import com.uddesh.tiffinservicebackend.authentication.JWTAdminConfig
import com.uddesh.tiffinservicebackend.authentication.JWTUserConfig
import com.uddesh.tiffinservicebackend.datamodels.*
import com.uddesh.tiffinservicebackend.repository.RepositoryImplementation
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    val jwtAdminConfig = JWTAdminConfig(System.getenv("JWT_SECRET"))
    val jwtUserConfig = JWTUserConfig(System.getenv("JWT_SECRET"))

    install(Authentication) {
        jwt("auth-admin") {
            jwtAdminConfig.configureKtorFeature(this)
        }
        jwt("auth-user") {
            jwtUserConfig.configureKtorFeature(this)
        }
    }

    routing {
        val gson = Gson()
        val repository = RepositoryImplementation()

        authenticate("auth-admin") {
            put("/adminlocation") {
                try {
                    val locationModel = call.receive<LocationModel>()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        repository.putAdminLocation(username, locationModel)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            put("/personaldetails") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    val multipartData = call.receiveMultipart()
                    var data: String
                    var fileName = ""
                    var personalDetailsModel: PersonalDetailsModel
                    if (principal == username) {
                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    data = part.value
                                    personalDetailsModel = gson.fromJson(data, PersonalDetailsModel::class.java)
                                    repository.putPersonalDetails(username, personalDetailsModel)
                                    fileName = personalDetailsModel.logoimage
                                }
                                is PartData.FileItem -> {
                                    val fileBytes = part.streamProvider().readBytes()
                                    File("logoimages/$fileName").writeBytes(fileBytes)
                                }
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    println(e)
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("/addservice") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    val multipartData = call.receiveMultipart()
                    var addServiceModel: AddServiceModel
                    var fileName = ""
                    var data: String
                    if (principal == username) {
                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    data = part.value
                                    addServiceModel = gson.fromJson(data, AddServiceModel::class.java)
                                    val id = repository.postAddService(addServiceModel)
                                    fileName = "${addServiceModel.username}$id.png"
                                    repository.updateFoodImage(fileName, id)
                                }
                                is PartData.FileItem -> {
                                    val fileBytes = part.streamProvider().readBytes()
                                    File("foodimages/$fileName").writeBytes(fileBytes)
                                }
                            }
                        }
                        call.respond(HttpStatusCode.Created)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            put("/updateservice") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    val multipartData = call.receiveMultipart()
                    val id = call.parameters["id"]!!.toInt()
                    var updateServiceModel: AddServiceModel
                    var fileName = ""
                    var data: String
                    if (principal == username) {
                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    data = part.value
                                    updateServiceModel = gson.fromJson(data, AddServiceModel::class.java)
                                    repository.putService(id, updateServiceModel)
                                    fileName = "$username$id.png"
                                }
                                is PartData.FileItem -> {
                                    val fileBytes = part.streamProvider().readBytes()
                                    File("foodimages/$fileName").writeBytes(fileBytes)
                                }
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    println(e)
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            put("/updatestatus"){
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    val id: Int = call.parameters["id"]!!.toInt()
                    val active = call.parameters["active"]!!.toBoolean()
                    if (principal == username) {
                        repository.putServiceStatus(id, active)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            delete("/deleteservice/{id}") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        val fileName = "$username$id.png"
                        val file = File("foodimages/$fileName")
                        file.delete()
                        val success = repository.deleteService(id)
                        if(success)
                            call.respond(HttpStatusCode.OK)
                        else
                            call.respond(HttpStatusCode.NotModified)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/userdetails") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getUserDetails(username))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/getadminservice") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getAdminService(username))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/getpersonaldetails"){
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getAdminPersonalDetails(username))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/servicedetails"){
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTAdminConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getAdminService(id))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

        }

        authenticate("auth-user") {
            put("/userlocation") {
                try {
                    val locationModel = call.receive<LocationModel>()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        repository.putUserLocation(username, locationModel)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            post("/addsubscription") {
                try {
                    val subscribeServiceModel = call.receive<SubscribeServiceModel>()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        repository.postSubscribeService(subscribeServiceModel)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            put("/updateactive") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    val id: Int = call.parameters["id"]!!.toInt()
                    val active = call.parameters["active"]!!.toBoolean()
                    if (principal == username) {
                        repository.putActiveService(id, active)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            put("/renewservice") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val days = call.parameters["days"]!!.toInt()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        val done = repository.putRenewService(id, days)
                        if (done) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotAcceptable)
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/subscribedservice") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getAllSubscribedService(username))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/subscribedservicedetails") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getSubscribedService(id))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/getallserviceprovider") {
                try {
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getAllServiceProvider(username))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/getserviceprovider") {
                try {
                    val username = call.parameters["username"]!!
                    val providerUsername = call.parameters["providerUsername"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getServiceProvider(username, providerUsername))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/getservice") {
                try {
                    val id = call.parameters["id"]!!.toInt()
                    val username = call.parameters["username"]!!
                    val principal = call.authentication.principal<JWTUserConfig.JwtUser>()!!.userName
                    if (principal == username) {
                        call.respond(HttpStatusCode.OK, repository.getService(id))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }catch (e : Exception){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }


        post("/usersignup") {
            try {
                val signupModel = call.receive<SignupModel>()
                val result = repository.postSignup(signupModel)
                if (result) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.NotAcceptable)
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/userlogin") {
            try {
                val loginModel = call.receive<LoginModel>()
                val user = repository.postLogin(loginModel)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val token = jwtUserConfig.generateToken(JWTUserConfig.JwtUser(user.username))
                    val mobileno = user.mobileno
                    call.respond(HttpStatusCode.OK, LoggedInDataModel(token, mobileno))
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/adminlogin") {
            try {
                val loginModel = call.receive<LoginModel>()
                val user = repository.postAdminLogin(loginModel)
                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val token = jwtAdminConfig.generateToken(JWTAdminConfig.JwtUser(user.username))
                    val mobileno = user.mobileno
                    call.respond(HttpStatusCode.OK, LoggedInDataModel(token , mobileno))
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        post("/adminsignup") {
            try {
                val signupModel = call.receive<AdminSignupModel>()
                val result = repository.postAdminSignup(signupModel)
                if (result) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.NotAcceptable)
                }
            }catch (e : Exception){
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        get("/getlogo"){
            val fileName = call.parameters["fileName"]
            val file = File("logoimages/$fileName")
            call.respondFile(file)
        }
        get("/getfoodimage"){
            val fileName = call.parameters["fileName"]
            val file = File("foodimages/$fileName")
            call.respondFile(file)
        }

    }
}
