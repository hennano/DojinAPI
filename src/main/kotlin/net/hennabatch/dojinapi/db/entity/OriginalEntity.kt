package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Original
import net.hennabatch.dojinapi.db.table.OriginalClosureTable
import net.hennabatch.dojinapi.db.table.OriginalTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OriginalEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<OriginalEntity>(OriginalTable)

    val name by OriginalTable.name
    val memo by OriginalTable.memo
    val parents by OriginalEntity.via(OriginalClosureTable.childId, OriginalClosureTable.parentId)
    val children by OriginalEntity.via(OriginalClosureTable.parentId, OriginalClosureTable.childId)
    val createdAt by OriginalTable.createdAt
    val updatedAt by OriginalTable.updatedAt

    fun toModel(resoleDepth: Int = 1):Original = Original(
        id = id.value,
        name = name,
        memo = memo,
        parents = if(resoleDepth > 0) parents.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        children = if(resoleDepth > 0) children.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}