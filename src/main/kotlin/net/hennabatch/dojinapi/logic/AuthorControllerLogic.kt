package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.repository.AuthorRepository

class AuthorControllerLogic {

    suspend fun fetchAllAuthors(): List<Author>{
        return dbQuery{
            AuthorRepository.selectAll(0)
        }
    }

}