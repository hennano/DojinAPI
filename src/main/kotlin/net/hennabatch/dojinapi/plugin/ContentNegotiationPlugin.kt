package net.hennabatch.dojinapi.plugin

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.contentNegotiationPlugin(){
    install(ContentNegotiation) {
        json()
    }
}