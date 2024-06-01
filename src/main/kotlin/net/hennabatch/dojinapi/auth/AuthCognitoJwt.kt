package net.hennabatch.dojinapi.auth

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*

const val AUTH_COGNITO_NAME = "jwt"

fun AuthenticationConfig.authCognito(config: ApplicationConfig){

    val jwtAudience = config.property("dojinapi.jwt.audience").getString()
    val jwtIssuer  = config.property("dojinapi.jwt.issuer").getString()

    jwt(name = AUTH_COGNITO_NAME) {
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

fun buildJwtProvider(jwtIssuer: String): JwkProvider = JwkProviderBuilder(jwtIssuer).build()

fun getAudience(credential: JWTCredential): List<String> = credential.payload.audience