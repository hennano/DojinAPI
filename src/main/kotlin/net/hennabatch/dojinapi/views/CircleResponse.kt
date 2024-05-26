package net.hennabatch.dojinapi.views

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Circle

class CircleResponse {
    fun makeCircleListFetched(circles: List<Circle>): JsonObject {
        val mapBody = circles
            .filter { it.name != null }
            .associate { it.toString() to JsonPrimitive(it.name!!) }
        return JsonObject(mapBody)
    }
}