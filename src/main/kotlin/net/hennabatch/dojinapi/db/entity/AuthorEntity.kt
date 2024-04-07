package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.table.AuthorTable
import net.hennabatch.dojinapi.db.table.MAuthorCircleTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AuthorEntity(id: EntityID<Int>) : IntEntity(id){
    companion object: IntEntityClass<AuthorEntity>(AuthorTable)

    val name by AuthorTable.name
    val memo by AuthorTable.memo
    val joinedCircles by CircleEntity via MAuthorCircleTable
    val createdAt by AuthorTable.createdAt
    val updatedAt by AuthorTable.updatedAt

    fun toModel(resoleDepth: Int = 1): Author = Author(
        id = id.value,
        name = name,
        memo = memo,
        joinedCircles = if(resoleDepth > 0) joinedCircles.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}