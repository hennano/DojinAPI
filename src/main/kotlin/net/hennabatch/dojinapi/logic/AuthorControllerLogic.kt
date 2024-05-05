package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.repository.AuthorAliasRepository
import net.hennabatch.dojinapi.db.repository.AuthorRepository

class AuthorControllerLogic {
    suspend fun fetchAllAuthors(): List<Author>{
        return dbQuery{
            AuthorRepository.selectAll(0)
        }
    }

    suspend fun insertAuthor(name: String, memo: String, authorAlias: List<Int>): Int{
        return dbQuery {
            //Authorをインサート
            val id = AuthorRepository.insert(name, memo)

            //AuthorAliasを登録
            insertAuthorAliases(id, authorAlias)
            return@dbQuery id
        }
    }

     fun insertAuthorAliases(authorId: Int, authorAlias: List<Int>){
        //AuthorAliasを登録
        val aliases = AuthorAliasRepository.selectsByAuthorId(authorId, 0)
            .map{it.author2.id}
        authorAlias.filter{! aliases.contains(it)}
            .forEach{
                AuthorAliasRepository.insert(authorId, it)
            }
    }

}