package net.hennabatch.dojinapi

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.controller.authorController
import net.hennabatch.dojinapi.controller.circleController

fun Application.configRouting() {
    routing {
        authenticate("jwt") {
            authorController()
            circleController()
        }
    }
}
