package net.hennabatch.dojinapi.logic

import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.repository.AuthorAliasRepository
import net.hennabatch.dojinapi.db.repository.AuthorRepository
import net.hennabatch.dojinapi.db.repository.CircleRepository
import net.hennabatch.dojinapi.db.repository.MAuthorCircleRepository

class AuthorServiceLogic {
    fun fetchAllAuthors(): List<Author>{
        return AuthorRepository.selectAll(0)
    }

    fun insertAuthor(name: String, memo: String, authorAlias: List<Int>, joinedCircles: List<Int>): Int{
        //authorAliasにあるIDが存在するIDか確認する
        authorAlias.forEach{
            //もし存在しなければEntityNotFoundExceptionが投げられる
            AuthorRepository.select(it)
        }
        //joinedCirclesにあるIDが存在するIDか確認する
        joinedCircles.forEach{
            //もし存在しなければEntityNotFoundExceptionが投げられる
            CircleRepository.select(it)
        }
        //Authorを登録
        val id = AuthorRepository.insert(name, memo)
        //自身を指定するAuthorAliasを登録
        AuthorAliasRepository.insert(id, id)
        //AuthorAliasを登録
        updateAuthorAliases(id, authorAlias)
        //joinedCirclesを登録
        updateJoinedCircles(id, joinedCircles)
        return id
    }

    fun fetchAuthor(authorId: Int):Author{
        val author = AuthorRepository.select(authorId)
        return author
    }

    fun updateAuthor(id: Int, name: String, memo: String, authorAlias: List<Int>, joinedCircles: List<Int>): Int{
        //Authorが存在するか確認
        //もし存在しなければEntityNotFoundExceptionが投げられる
        AuthorRepository.select(id)
        //authorAliasにあるIDが存在するIDか確認する
        authorAlias.forEach{
            //もし存在しなければEntityNotFoundExceptionが投げられる
            AuthorRepository.select(it)
        }
        //joinedCirclesにあるIDが存在するIDか確認する
        joinedCircles.forEach{
            //もし存在しなければEntityNotFoundExceptionが投げられる
            CircleRepository.select(it)
        }
        //Authorをupdate
        AuthorRepository.update(id, name, memo)
        //AuthorAliasを登録
        updateAuthorAliases(id, authorAlias)
        //joinedCirclesを登録
        updateJoinedCircles(id, joinedCircles)
        return id
    }

    fun deleteAuthor(id: Int): Boolean{
        //Authorが存在するか確認
        //もし存在しなければEntityNotFoundExceptionが投げられる
        AuthorRepository.select(id)

        //AuthorAliasを削除
        AuthorAliasRepository.deletesIncludedByAuthorId(id)
        //MAuthorCircleを削除
        MAuthorCircleRepository.deletesByAuthorId(id)
        //Authorを削除
        return AuthorRepository.delete(id)
    }

    fun updateAuthorAliases(authorId: Int, authorAlias: List<Int>){
        //AuthorIdに紐づく既存のエイリアス一覧を取得
        val aliases = AuthorRepository.select(authorId).alias.map { it.id }
        //登録されていない場合は追加
        AuthorAliasRepository.matrixInsert(authorId, authorAlias.filter{!aliases.contains(it)})
        //authorAliasにないものがあれば削除
        AuthorAliasRepository.deletesRelation(authorId, aliases.filter{!authorAlias.contains(it) && it != authorId})
    }

    fun updateJoinedCircles(authorId: Int, joinedCircles: List<Int>){
        //authorIdに紐づく既存の参加サークル一覧を取得
        val circles = AuthorRepository.select(authorId).joinedCircles.map { it.id }
        //登録されていない場合は追加
        joinedCircles.filter { !circles.contains(it)}
            .forEach{
                MAuthorCircleRepository.insert(authorId, it)
            }
        //joinedCirclesにないものがあれば削除
        circles.filter { !joinedCircles.contains(it) }
            .forEach {
                MAuthorCircleRepository.delete(authorId, it)
            }
    }
}