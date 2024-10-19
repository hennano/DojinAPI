package net.hennabatch.dojinapi.controller.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorRequestEntity(
    @SerialName("name") val name: String,
    @SerialName("memo") val memo: String?,
    @SerialName("author_alias") val authorAlias: List<Int>,
    @SerialName("joined_circles") val joinedCircles: List<Int>
): RequestEntity{
    override fun validation(): RequestValidationResult{
        if(name.isBlank()) {
            return RequestValidationResult(false, "nameが空")
        }
        if(memo == null){
            return RequestValidationResult(false, "memoがない")
        }
        return RequestValidationResult(true)
    }
}