package net.hennabatch.dojinapi.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*

val errors = HttpStatusCode.allStatusCodes.filter { it.value >= 400 }.toTypedArray()

fun StatusPagesConfig.byStatusCode(){

    status(*errors){ call, status ->
        showErrorResponse(call, status)
    }
}