package net.hennabatch.dojinapi.controller.validation

import io.ktor.server.plugins.requestvalidation.*
import net.hennabatch.dojinapi.controller.entity.AuthorRequestEntity

fun RequestValidationConfig.authorRequestValidation(){
    validate<AuthorRequestEntity>{
        if(it.name.isBlank()){
            ValidationResult.Invalid("")
        }else{
            ValidationResult.Valid
        }
    }
}