package net.hennabatch.dojinapi

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.hennabatch.dojinapi.plugin.*

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        environment = commandLineEnvironment(args),
    )
        .start(wait = true)
}

fun Application.module() {
    //DI読み込み
    koinPlugin()
    //プラグイン読み込み
    resourcePlugin()
    contentNegotiationPlugin()
    requestValidationPlugin()
    dataBasePlugin()
    statusPagePlugin()
    authenticationPlugin()
    //ルーティング
    configRouting()
}
