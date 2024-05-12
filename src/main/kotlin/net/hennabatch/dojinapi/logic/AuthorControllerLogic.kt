package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
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
            //authorAliasにあるIDが存在するIDか確認する
            authorAlias.forEach{
                //もし存在しなければEntityNotFoundExceptionが投げられる
                AuthorRepository.select(it)
            }
            //Authorをインサート
            val id = AuthorRepository.insert(name, memo)
            //AuthorAliasを登録
            updateAuthorAliases(id, authorAlias)
            return@dbQuery id
        }
    }

    suspend fun fetchAuthor(authorId: Int):Pair<Author, List<AuthorAlias>>{
        return dbQuery{
            val author = AuthorRepository.select(authorId)
            val authorAlias = AuthorAliasRepository.selectsByAuthorId(authorId)
            return@dbQuery author to authorAlias
        }
    }

     fun updateAuthorAliases(authorId: Int, authorAlias: List<Int>){
         //AuthorIdに紐づく既存のエイリアス一覧を取得
         val aliases = AuthorAliasRepository.selectsByAuthorId(authorId, 0)
            .associate{it.id to it.author2.id}
         //登録されていない場合は追加
         authorAlias.filter{!aliases.values.contains(it)}
            .forEach{
                AuthorAliasRepository.insert(authorId, it)
            }
         //authorAliasにないものがあれば削除
         aliases.filter { !authorAlias.contains(it.value) }
             .forEach{
                 AuthorAliasRepository.delete(it.key)
             }
    }

    suspend fun updateAuthor(id: Int, name: String, memo: String, authorAlias: List<Int>): Int{
        return dbQuery{
            //authorAliasにあるIDが存在するIDか確認する
            authorAlias.forEach{
                //もし存在しなければEntityNotFoundExceptionが投げられる
                AuthorRepository.select(it)
            }
            //Authorをインサート
            AuthorRepository.update(id, name, memo)

            //AuthorAliasを登録
            updateAuthorAliases(id, authorAlias)
            return@dbQuery id
        }

    }

}