package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import net.hennabatch.dojinapi.db.DatabaseSingleton

fun Application.dataBasePlugin(){
    DatabaseSingleton.init(environment.config)
}