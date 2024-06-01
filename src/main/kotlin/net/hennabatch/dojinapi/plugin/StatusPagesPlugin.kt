package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import net.hennabatch.dojinapi.statuspages.byException
import net.hennabatch.dojinapi.statuspages.byStatusCode

fun Application.statusPagePlugin(){
    install(StatusPages){
        byStatusCode()
        byException()
    }
}