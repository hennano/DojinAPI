package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import org.jetbrains.exposed.sql.insert

object AuthorAliasRepository {

    fun insert(authorId1: Int, authorId2: Int): Int{
        val id = AuthorAliasTable.insert {
            it[this.authorId1] = authorId1
            it[this.authorId2] = authorId2
        } get AuthorAliasTable.id
        return id.value
    }
}