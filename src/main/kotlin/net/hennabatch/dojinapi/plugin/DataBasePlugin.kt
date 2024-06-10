package net.hennabatch.dojinapi.plugin

import io.ktor.server.application.*
import net.hennabatch.dojinapi.db.CommonDb
import org.koin.ktor.ext.inject

fun Application.dataBasePlugin(){

    val db by inject<CommonDb>()
    db.init(environment.config)
}