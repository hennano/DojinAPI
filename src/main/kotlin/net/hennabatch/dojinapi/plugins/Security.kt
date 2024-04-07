package net.hennabatch.dojinapi.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtAudience = environment.config.property("dojinapi.jwt.audience").getString()
    val jwtDomain = environment.config.property("dojinapi.jwt.domain").getString()
    val jwtRealm = environment.config.property("dojinapi.jwt.realm").getString()
    val jwtSecret = environment.config.property("dojinapi.jwt.secret").getString()
    install(Authentication){
        jwt(name = "jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
