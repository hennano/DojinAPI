package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import net.hennabatch.dojinapi.Module
import org.koin.ktor.plugin.Koin

fun Application.koinPlugin(){
    install(Koin){
        modules(Module.koinModules())
    }
}