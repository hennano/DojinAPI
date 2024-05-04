package net.hennabatch.dojinapi

import net.hennabatch.dojinapi.logic.AuthorControllerLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import net.hennabatch.dojinapi.views.CommonResponse
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object Module {
    fun koinModules() = module {
        //Logic
        singleOf(::AuthorControllerLogic)

        //Views
        singleOf(::AuthorResponse)
        singleOf(::CommonResponse)
    }
}