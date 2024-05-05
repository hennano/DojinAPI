package net.hennabatch.dojinapi.views

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*


fun Application.errorResponse(){
    install(StatusPages){
        exception<Throwable> {call, cause ->
            when(cause){
                is RequestValidationException ->{
                    //バリデーションに失敗
                    showErrorResponse(call, HttpStatusCode.BadRequest, "BadRequest")
                }
                else ->{
                    //サーバーエラー
                    call.application.environment.log.error(cause)
                    showErrorResponse(call, HttpStatusCode.InternalServerError, "ServerError")
                }
            }
        }
    }
}

private suspend fun showErrorResponse(call: ApplicationCall, statusCode: HttpStatusCode, errorMessage: String){
    call.respond(statusCode, mapOf("error" to errorMessage))
}