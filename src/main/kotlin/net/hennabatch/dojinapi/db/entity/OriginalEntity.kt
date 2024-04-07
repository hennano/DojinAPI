package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Original
import net.hennabatch.dojinapi.db.table.OriginalTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OriginalEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<OriginalEntity>(OriginalTable)

    val name by OriginalTable.name
    val memo by OriginalTable.memo
    val parentOriginalId by OriginalEntity via OriginalTable
    val createdAt by OriginalTable.createdAt
    val updatedAt by OriginalTable.updatedAt

    fun toModel(resoleDepth: Int = 1):Original = Original(
        id = id.value,
        name = name,
        memo = memo,
        parentOriginal = if(resoleDepth > 0) parentOriginalId.first().toModel(resoleDepth - 1) else null,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}