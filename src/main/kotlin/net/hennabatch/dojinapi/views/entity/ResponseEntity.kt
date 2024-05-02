package net.hennabatch.dojinapi.views.entity

import io.ktor.http.*
import java.io.File

data class ResponseEntity(
    val statusCode: HttpStatusCode,
    val headers: Map<String, String> = mapOf(),
    val responseBody: ResponseBody
)

sealed class ResponseBody{
    data class MapBody(val value: Map<String, Any>): ResponseBody()
    data class FileBody(val value: File): ResponseBody()
}
