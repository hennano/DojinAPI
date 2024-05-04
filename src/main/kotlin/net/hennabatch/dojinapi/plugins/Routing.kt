package net.hennabatch.dojinapi.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.controller.authorController

fun Application.configureRouting() {

    routing {
        authorController()
    }
}
