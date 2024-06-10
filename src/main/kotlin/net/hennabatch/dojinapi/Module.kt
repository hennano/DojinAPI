package net.hennabatch.dojinapi

import net.hennabatch.dojinapi.db.*
import net.hennabatch.dojinapi.logic.*
import net.hennabatch.dojinapi.views.*

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object Module {
    fun koinModules() = module {

        //db
        singleOf(::HikariCpDb) bind CommonDb::class

        //Logic
        singleOf(::AuthorServiceLogic)
        singleOf(::CircleControllerLogic)

        //Views
        singleOf(::AuthorResponse)
        singleOf(::CircleResponse)
    }
}