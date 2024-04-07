package net.hennabatch.dojinapi

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.plugins.configureDatabases
import net.hennabatch.dojinapi.plugins.configureRouting
import net.hennabatch.dojinapi.plugins.configureSecurity
import net.hennabatch.dojinapi.plugins.configureSerialization

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        commandLineEnvironment(args)
    )
        .start(wait = true)
}

fun Application.module() {
    DatabaseSingleton.init(environment.config)
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureRouting()
}
