package net.hennabatch.dojinapi.controller

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.service.CircleService
import org.koin.ktor.ext.inject

fun Route.circleController() {

    val circleService by inject<CircleService>()

    @Resource("/circle")
    class CircleLocation()
    get<CircleLocation>{
        call.respond(HttpStatusCode.OK, circleService.getCircles())
    }
}