package net.hennabatch.dojinapi.service

import kotlinx.serialization.json.JsonObject
import net.hennabatch.dojinapi.controller.request.AuthorRequestEntity
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.logic.AuthorServiceLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthorService: KoinComponent {

    val db by inject<CommonDb>()

    val authorServiceLogic by inject<AuthorServiceLogic>()
    val authorResponse by inject<AuthorResponse>()

    suspend fun getAuthors(): JsonObject{
        return db.dbQuery{
            val authors = authorServiceLogic.fetchAllAuthors()
            return@dbQuery authorResponse.makeAuthorListFetched(authors)
        }
    }

    suspend fun postAuthor(requestEntity: AuthorRequestEntity): JsonObject{
        return db.dbQuery {
            val id = authorServiceLogic.insertAuthor(name = requestEntity.name!!, memo = requestEntity.memo!!, authorAlias = requestEntity.authorAlias!!)
            return@dbQuery authorResponse.makeAuthorCreated(id, requestEntity.name)
        }
    }

    suspend fun getAuthor(authorId: Int):JsonObject{
        return db.dbQuery {
            val authorDetail = authorServiceLogic.fetchAuthor(authorId)
            return@dbQuery authorResponse.makeAuthorFetched(authorDetail.first, authorDetail.second)
        }
    }

    suspend fun putAuthor(authorId: Int, requestEntity: AuthorRequestEntity):JsonObject{
        return db.dbQuery {
            val id = authorServiceLogic.updateAuthor(id = authorId, name = requestEntity.name, memo = requestEntity.memo!!, authorAlias = requestEntity.authorAlias)
            return@dbQuery authorResponse.makeAuthorUpdated(id, requestEntity.name)
        }
    }

    suspend fun deleteAuthor(authorId: Int):JsonObject{
        return db.dbQuery {
            val isSucceededInDeletion = authorServiceLogic.deleteAuthor(authorId)
            return@dbQuery authorResponse.makeAuthorDeleted(isSucceededInDeletion)
        }
    }
}