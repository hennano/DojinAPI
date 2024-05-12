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
import net.hennabatch.dojinapi.controller.entity.AuthorRequestEntity
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

    post<AuthorLocation>{
        val req = call.receive<AuthorRequestEntity>()
        val id = authorControllerLogic.insertAuthor(name = req.name, memo = req.memo, authorAlias = req.authorAlias)
        val res = authorResponse.makeAuthorCreated(id, req.name)
        call.respond(HttpStatusCode.OK, res)
    }

    @Resource("/author/{authorId}")
    class AuthorDetailLocation(val authorId: Int)
    get<AuthorDetailLocation> { param ->
        val authorId = param.authorId
        val authorDetail = authorControllerLogic.fetchAuthor(authorId)
        val res = authorResponse.makeAuthorFetched(authorDetail.first, authorDetail.second)
        call.respond(HttpStatusCode.OK, res)
    }

    put<AuthorDetailLocation>{ param ->
        val req = call.receive<AuthorRequestEntity>()
        val authorId = param.authorId
        val id = authorControllerLogic.updateAuthor(id = authorId, name = req.name, memo = req.memo, authorAlias = req.authorAlias)
        val res = authorResponse.makeAuthorUpdated(id, req.name)
        call.respond(HttpStatusCode.OK, res)
    }
    /*

delete<AuthorDetailLocation> { param ->
    val authorId = param.authorId
}
*/
}