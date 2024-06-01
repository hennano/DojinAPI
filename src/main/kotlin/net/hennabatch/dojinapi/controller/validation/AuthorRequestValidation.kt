package net.hennabatch.dojinapi.controller.validation

import io.ktor.server.plugins.requestvalidation.*
import net.hennabatch.dojinapi.controller.entity.AuthorRequestEntity

fun RequestValidationConfig.authorRequestValidation(){
    validate<AuthorRequestEntity>{
        if(it.name.isNullOrBlank()) {
            return@validate ValidationResult.Invalid("")
        }
        if(it.memo == null){
            return@validate ValidationResult.Invalid("")
        }
        if(it.authorAlias == null){
            return@validate ValidationResult.Invalid("")
        }
        return@validate ValidationResult.Valid
    }
}