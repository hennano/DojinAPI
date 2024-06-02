package net.hennabatch.dojinapi.controller.request

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorRequestEntity(
    @SerialName("name") val name: String?,
    @SerialName("memo") val memo: String?,
    @SerialName("author_alias") val authorAlias: List<Int>?
)

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