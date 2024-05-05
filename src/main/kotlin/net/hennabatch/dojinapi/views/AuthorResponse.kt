package net.hennabatch.dojinapi.views

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author

class AuthorResponse {
    fun makeAuthorListFetched(authors: List<Author>): JsonObject{
        val mapBody = authors
            .filter { it.name != null }
            .associate { it.id.toString() to JsonPrimitive(it.name!!)}
        return JsonObject(mapBody)
    }

    fun makeAuthorCreated(id: Int, name:String): JsonObject{
        return JsonObject(mapOf(id.toString() to JsonPrimitive(name)))
    }
}