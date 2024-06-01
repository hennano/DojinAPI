package net.hennabatch.dojinapi.statuspages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun showErrorResponse(call: ApplicationCall, statusCode: HttpStatusCode){
    call.respond(statusCode, mapOf("error" to statusCode.description))
}