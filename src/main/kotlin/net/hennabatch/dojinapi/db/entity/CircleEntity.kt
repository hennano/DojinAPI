package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Circle
import net.hennabatch.dojinapi.db.table.CircleTable
import net.hennabatch.dojinapi.db.table.MAuthorCircleTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CircleEntity (id: EntityID<Int>) : IntEntity(id){
    companion object: IntEntityClass<CircleEntity>(CircleTable)

    val name by CircleTable.name
    val memo by CircleTable.memo
    val members by AuthorEntity via MAuthorCircleTable
    val createdAt by CircleTable.createdAt
    val updatedAt by CircleTable.updatedAt

    fun toModel(resoleDepth: Int = 1): Circle = Circle(
        id = id.value,
        name = name,
        memo = memo,
        members = if(resoleDepth > 0) members.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}