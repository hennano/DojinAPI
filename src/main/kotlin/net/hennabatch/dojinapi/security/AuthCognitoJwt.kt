package net.hennabatch.dojinapi.security

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthCognitoSecurity() {
    val jwtAudience = environment.config.property("dojinapi.jwt.audience").getString()
    val jwtIssuer  = environment.config.property("dojinapi.jwt.issuer").getString()
    install(Authentication){
        jwt(name = "jwt") {
            val jwtProvider = buildJwtProvider(jwtIssuer)
            verifier(jwtProvider, jwtIssuer)
            validate { credential ->
                if (getAudience(credential).contains(jwtAudience)){
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

fun buildJwtProvider(jwtIssuer: String): JwkProvider = JwkProviderBuilder(jwtIssuer).build()

fun getAudience(credential: JWTCredential): List<String> = credential.payload.audience