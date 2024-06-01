package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import io.ktor.server.auth.*
import net.hennabatch.dojinapi.auth.authCognito

fun Application.authenticationPlugin(){
    val config = environment.config
    install(Authentication){
        authCognito(config)
    }
}