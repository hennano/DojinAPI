package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.CircleAlias
import net.hennabatch.dojinapi.db.table.CircleAliasTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CircleAliasEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<CircleAliasEntity>(CircleAliasTable)

    val circleId1 by CircleEntity.referencedOn(CircleAliasTable.circleId1)
    val circleId2 by CircleEntity.referencedOn(CircleAliasTable.circleId2)
    val createdAt by CircleAliasTable.createdAt
    val updatedAt by CircleAliasTable.updatedAt

    fun toModel(): CircleAlias = CircleAlias(
        id = id.value,
        circle1 = circleId1.toModel(),
        circle2 = circleId2.toModel(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}