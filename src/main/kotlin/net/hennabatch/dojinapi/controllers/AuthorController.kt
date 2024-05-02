package net.hennabatch.dojinapi.controllers

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import net.hennabatch.dojinapi.logic.AuthorControllerLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import net.hennabatch.dojinapi.views.CommonResponse

fun Route.authorController(){

    val authorControllerLogic = AuthorControllerLogic()
    val authorResponse = AuthorResponse()
    val commonResponse = CommonResponse()


    @Resource("/author")
    class AuthorLocation()
    get<AuthorLocation> {
        try{
            val authors = authorControllerLogic.fetchAllAuthors()
            val response = authorResponse.makeAuthorListFetched(authors)
            commonResponse.showResponse(call, response)
        }catch (e: Exception){
            call.application.environment.log.error(e)
            commonResponse.showServerErrorResponse(call)
        }
    }

    /*
    post<AuthorLocation>{

    }

    @Resource("/author/{authorId}")
    class AuthorDetailLocation(val authorId: Int)
    get<AuthorDetailLocation> { param ->
        val authorId = param.authorId
    }

    put<AuthorDetailLocation>{ param ->
        val authorId = param.authorId
    }

    delete<AuthorDetailLocation> { param ->
        val authorId = param.authorId
    }
    */
}