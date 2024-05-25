package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.entity.AuthorEntity
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.table.AuthorTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

object AuthorRepository {

    fun select(id: Int, resoleDepth: Int = 1): Author{
        return AuthorEntity[id].toModel(resoleDepth)
    }

    fun selectAll(resoleDepth: Int = 1):List<Author>{
        return AuthorEntity.all().with(AuthorEntity::joinedCircles).map { it.toModel(resoleDepth) }
    }

    fun insert(name: String, memo: String):Int{
        val id = AuthorTable.insert {
            it[this.name] = name
            it[this.memo] = memo
        } get AuthorTable.id
        return id.value
    }

    fun update(id: Int, name: String, memo: String): Int{
        AuthorTable.update({
            AuthorTable.id eq id
        }){
            it[this.name] = name
            it[this.memo] = memo
        }
        return id
    }

    fun delete(id: Int): Boolean{
        return AuthorTable.deleteWhere {
            AuthorTable.id eq id
        } > 0
    }
}