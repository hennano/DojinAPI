package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import net.hennabatch.dojinapi.controller.request.RequestEntity

fun Application.requestValidationPlugin(){
    install(RequestValidation){
        validate<RequestEntity>{
            val result = it.validation()
            if(result.result){
                return@validate ValidationResult.Valid
            }
            return@validate ValidationResult.Invalid(result.reason)
        }
    }
}
