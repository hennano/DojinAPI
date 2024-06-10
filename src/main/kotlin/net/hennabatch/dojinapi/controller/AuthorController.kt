package net.hennabatch.dojinapi.controller

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.hennabatch.dojinapi.controller.request.AuthorRequestEntity
import net.hennabatch.dojinapi.service.AuthorService
import org.koin.ktor.ext.inject

fun Route.authorController(){

    val authorService by inject<AuthorService>()

    @Resource("/author")
    class AuthorLocation()
    get<AuthorLocation> {
        call.respond(HttpStatusCode.OK, authorService.getAuthors())
    }

    post<AuthorLocation>{
        val req = call.receive<AuthorRequestEntity>()
        call.respond(HttpStatusCode.OK, authorService.postAuthor(req))
    }

    @Resource("/author/{authorId}")
    class AuthorDetailLocation(val authorId: Int)
    get<AuthorDetailLocation> { param ->
        val authorId = param.authorId
        call.respond(HttpStatusCode.OK, authorService.getAuthor(authorId))
    }

    put<AuthorDetailLocation>{ param ->
        val req = call.receive<AuthorRequestEntity>()
        val authorId = param.authorId
        call.respond(HttpStatusCode.OK, authorService.putAuthor(authorId, req))
    }

    delete<AuthorDetailLocation> { param ->
        val authorId = param.authorId
        call.respond(HttpStatusCode.OK, authorService.deleteAuthor(authorId))
    }
}



