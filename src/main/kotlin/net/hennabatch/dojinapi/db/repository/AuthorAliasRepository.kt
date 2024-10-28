package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.authorId1
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.authorId2
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

object AuthorAliasRepository {

    fun insert(authorId1: Int, authorId2: Int){
        AuthorAliasTable.insert {
            it[this.authorId1] = authorId1
            it[this.authorId2] = authorId2
        }
    }

    fun matrixInsert(authorId: Int, matrixAuthorIds: List<Int>){
        val insertList = matrixAuthorIds.map {
            listOf(
                authorId to it,
                it to authorId
            )
        }.flatten()
        AuthorAliasTable.batchInsert(insertList){
            this[authorId1] = it.first
            this[authorId2] = it.second
        }
    }

    fun delete(authorId1: Int, authorId2: Int): Boolean{
        return AuthorAliasTable.deleteWhere {
            (AuthorAliasTable.authorId1 eq authorId1 ) and (AuthorAliasTable.authorId2 eq authorId2)
        } > 0
    }

    fun deletesIncludedByAuthorId(authorId: Int):Int{
        return AuthorAliasTable.deleteWhere {
            (authorId1 eq authorId) or
                    (authorId2 eq authorId)
        }
    }

    fun deletesRelation(authorId: Int, deleteTargetIds: List<Int>):Int{
        return AuthorAliasTable.deleteWhere {
            (( authorId1 eq authorId ) and ( authorId2 inList deleteTargetIds)) or
                    (( authorId1 inList deleteTargetIds) and ( authorId2 eq authorId ))
        }
    }
}