package net.hennabatch.dojinapi.views

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import net.hennabatch.dojinapi.views.entity.ResponseBody
import net.hennabatch.dojinapi.views.entity.ResponseEntity

class CommonResponse {

    suspend fun showResponse(call: ApplicationCall, response: ResponseEntity){
        response.headers.entries.forEach{
            call.response.headers.append(it.key, it.value)
        }
        when(val body = response.responseBody){
            is ResponseBody.MapBody ->{
                call.respond(HttpStatusCode.OK, body.value)
            }
            is ResponseBody.FileBody -> {
                call.respond(HttpStatusCode.OK, body.value)
            }
        }

    }

    private suspend fun showErrorResponse(call: ApplicationCall, statusCode: HttpStatusCode, errorMessage: String){
        call.respond(statusCode, mapOf( "error" to errorMessage))
    }
    suspend fun showServerErrorResponse(call: ApplicationCall){
        showErrorResponse(call, HttpStatusCode.InternalServerError, "ServerError")
    }

    suspend fun showUnauthorizedResponse(call: ApplicationCall){
        showErrorResponse(call, HttpStatusCode.Unauthorized, "Unauthorized")
    }
}