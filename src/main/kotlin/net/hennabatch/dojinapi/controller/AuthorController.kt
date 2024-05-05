package net.hennabatch.dojinapi.controller

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.logic.AuthorControllerLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.koin.ktor.ext.inject

fun Route.authorController(){

    val authorControllerLogic by inject<AuthorControllerLogic>()
    val authorResponse by inject<AuthorResponse>()

    @Resource("/author")
    class AuthorLocation()
    get<AuthorLocation> {
        val authors = authorControllerLogic.fetchAllAuthors()
        val res = authorResponse.makeAuthorListFetched(authors)
        call.respond(HttpStatusCode.OK, res)
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