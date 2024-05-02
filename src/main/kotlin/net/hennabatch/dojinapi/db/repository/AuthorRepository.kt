package net.hennabatch.dojinapi.db.repository

import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.entity.AuthorEntity
import net.hennabatch.dojinapi.db.entity.CircleEntity
import net.hennabatch.dojinapi.db.model.Author
import org.jetbrains.exposed.dao.with

object AuthorRepository {

    fun selectAll(resoleDepth: Int):List<Author>{
        return AuthorEntity.all().with(AuthorEntity::joinedCircles).map { it.toModel(resoleDepth) }
    }
}