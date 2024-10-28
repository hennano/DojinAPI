package net.hennabatch.dojinapi.views

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author
import java.time.format.DateTimeFormatter

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

    fun makeAuthorFetched(author: Author): JsonObject{
        return JsonObject(mapOf(
            "id" to JsonPrimitive(author.id),
            "name" to JsonPrimitive(author.name),
            "memo" to JsonPrimitive(author.memo),
            "joined_circles" to JsonObject(
                author.joinedCircles.associate{ it.id.toString() to JsonPrimitive(it.name)}
            ),
            "author_alias" to JsonObject(
                author.alias.associate{ it.id.toString() to JsonPrimitive(it.name)}
            ),
            "created_at" to JsonPrimitive(author.createdAt?.toJavaLocalDateTime()?.format(DateTimeFormatter.ISO_DATE_TIME)),
            "updated_at" to JsonPrimitive(author.updatedAt?.toJavaLocalDateTime()?.format(DateTimeFormatter.ISO_DATE_TIME)),
        ))
    }

    fun makeAuthorUpdated(id: Int, name:String): JsonObject{
        return JsonObject(mapOf(id.toString() to JsonPrimitive(name)))
    }

    fun makeAuthorDeleted(isSucceededInDeletion: Boolean): JsonObject{
        return JsonObject(mapOf())
    }
}