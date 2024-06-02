package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import net.hennabatch.dojinapi.controller.request.authorRequestValidation

fun Application.requestValidationPlugin(){
    install(RequestValidation){
        authorRequestValidation()
    }
}
