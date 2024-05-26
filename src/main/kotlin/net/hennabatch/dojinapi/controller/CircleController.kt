package net.hennabatch.dojinapi.controller

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.logic.CircleControllerLogic
import net.hennabatch.dojinapi.views.CircleResponse
import org.koin.ktor.ext.inject

fun Route.circleController() {

    val circleControllerLogic by inject<CircleControllerLogic>()
    val circleResponse by inject<CircleResponse>()

    @Resource("/circle")
    class CircleLocation()
    get<CircleLocation>{
        val circles = circleControllerLogic.fetchCircles()
        val res = circleResponse.makeCircleListFetched(circles)
        call.respond(HttpStatusCode.OK, res)
    }
}