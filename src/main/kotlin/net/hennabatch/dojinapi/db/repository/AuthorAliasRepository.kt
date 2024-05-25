package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.entity.AuthorAliasEntity
import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.authorId1
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.authorId2
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or

object AuthorAliasRepository {

    fun insert(authorId1: Int, authorId2: Int): Int{
        val id = AuthorAliasTable.insert {
            it[this.authorId1] = authorId1
            it[this.authorId2] = authorId2
        } get AuthorAliasTable.id
        return id.value
    }

    fun select(id: Int, resoleDepth: Int = 1): AuthorAlias {
        return AuthorAliasEntity[id].toModel(resoleDepth)
    }

    fun selectsByAuthorId(authorId: Int, resoleDepth: Int = 1):List<AuthorAlias>{
        val aliases = AuthorAliasEntity.find{
            (authorId1 eq authorId) or
                    (authorId2 eq authorId)
        }.map { it.toModel(resoleDepth) }
        return aliases.map {
            if (it.author1.id != authorId){
                AuthorAlias(
                    id = it.id,
                    author1 = it.author2,
                    author2 = it.author1,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }else{
                it
            }
        }
    }

    fun delete(id: Int): Boolean{
        return AuthorAliasTable.deleteWhere {
            AuthorAliasTable.id eq id
        } > 0
    }

    fun deletesIncludedByAuthorId(authorId: Int):Int{
        return AuthorAliasTable.deleteWhere {
            (authorId1 eq authorId) or
                    (authorId2 eq authorId)
        }
    }
}