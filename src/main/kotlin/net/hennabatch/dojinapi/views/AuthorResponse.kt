package net.hennabatch.dojinapi.views

import io.ktor.http.*
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.views.entity.ResponseBody
import net.hennabatch.dojinapi.views.entity.ResponseEntity

class AuthorResponse {
    fun makeAuthorListFetched(authors: List<Author>): ResponseEntity{
        val body = authors
            .filter { it.name != null }
            .associate { it.id.toString() to it.name!! }
        return ResponseEntity(HttpStatusCode.OK, responseBody = ResponseBody.MapBody(body))
    }
}