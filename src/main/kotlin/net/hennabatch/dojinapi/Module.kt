package net.hennabatch.dojinapi

import net.hennabatch.dojinapi.logic.*
import net.hennabatch.dojinapi.views.*

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object Module {
    fun koinModules() = module {
        //Logic
        singleOf(::AuthorControllerLogic)
        singleOf(::CircleControllerLogic)

        //Views
        singleOf(::AuthorResponse)
        singleOf(::CircleResponse)
    }
}