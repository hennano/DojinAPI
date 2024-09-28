package net.hennabatch.dojinapi.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.util.logging.*
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException

fun StatusPagesConfig.byException(){
    exception<Throwable> {call, cause ->
        when(cause){
            is RequestValidationException ->{
                //バリデーションに失敗
                showErrorResponse(call, HttpStatusCode.BadRequest)
            }
            is SerializationException ->{
                //json変換に失敗
                showErrorResponse(call, HttpStatusCode.BadRequest)
            }
            is BadRequestException ->{
                //json変換に失敗
                showErrorResponse(call, HttpStatusCode.BadRequest)
            }

            is EntityNotFoundException ->{
                //見つからない
                showErrorResponse(call, HttpStatusCode.NotFound)
            }
            else ->{
                //サーバーエラー
                call.application.environment.log.error(cause)
                showErrorResponse(call, HttpStatusCode.InternalServerError)
            }
        }
    }
}