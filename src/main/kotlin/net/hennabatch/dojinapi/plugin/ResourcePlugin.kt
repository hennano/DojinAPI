package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import io.ktor.server.resources.*

fun Application.resourcePlugin(){
    install(Resources)
}