package net.hennabatch.dojinapi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authenticate("jwt") {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
}
