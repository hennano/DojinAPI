package net.hennabatch.dojinapi.views

import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.views.entity.ResponseBody
import net.hennabatch.dojinapi.views.entity.ResponseEntity

class AuthorResponse {
    fun makeAuthorListFetched(authors: List<Author>): ResponseEntity{
        val mapBody = authors
            .filter { it.name != null }
            .associate { it.id.toString() to JsonPrimitive(it.name!!)}
        val jsonBody = JsonObject(mapBody)
        return ResponseEntity(HttpStatusCode.OK, responseBody = ResponseBody.JsonBody(jsonBody))
    }
}