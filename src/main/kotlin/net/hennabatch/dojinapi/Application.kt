package net.hennabatch.dojinapi

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.resources.*
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.plugins.configureRouting
import net.hennabatch.dojinapi.security.configureAuthCognitoSecurity
import net.hennabatch.dojinapi.views.errorResponse
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        commandLineEnvironment(args)
    )
        .start(wait = true)
}

fun Application.module() {
    //プラグインインストール
    install(Resources)
    install(RequestValidation)
    install(ContentNegotiation) {
        json()
    }
    install(Koin){
        modules(Module.koinModules())
    }

    //初期化
    DatabaseSingleton.init(environment.config)
    //configureSerialization()
    //configureDatabases()
    errorResponse()
    configureAuthCognitoSecurity()
    configureRouting()
}
