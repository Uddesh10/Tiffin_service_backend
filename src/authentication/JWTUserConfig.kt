package com.uddesh.tiffinservicebackend.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.auth.*
import io.ktor.auth.jwt.*

class JWTUserConfig(jwtSecret : String) {
    companion object Constants {
        // jwt config
        private val jwtIssuer = System.getenv("JWT_ISSUER")
        private val jwtRealm = System.getenv("JWT_REALM")
        private val jwtAudience = System.getenv("JWT_AUDIENCE_USER")
        // claims
        private val CLAIM_USERNAME = System.getenv("CLAIM")
    }

    private val jwtAlgorithm = Algorithm.HMAC512(jwtSecret)
    private val jwtVerifier: JWTVerifier = JWT
        .require(jwtAlgorithm)
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .build()

    /**
     * Generate a token for a authenticated user
     */
    fun generateToken(user: JwtUser): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(jwtIssuer)
        .withAudience(jwtAudience)
        .withClaim(CLAIM_USERNAME, user.userName)
        .sign(jwtAlgorithm)

    /**
     * Configure the jwt ktor authentication feature
     */
    fun configureKtorFeature(config: JWTAuthenticationProvider.Configuration) = with(config) {
        verifier(jwtVerifier)
        realm = jwtRealm
        validate {
            val userName = it.payload.getClaim(CLAIM_USERNAME).asString()

            if (userName != null) {
                JwtUser(userName)
            } else {
                null
            }
        }
    }

    /**
     * POKO, that contains information of an authenticated user that is authenticated via jwt
     */
    data class JwtUser(val userName: String): Principal
}