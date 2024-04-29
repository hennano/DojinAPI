package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Character
import net.hennabatch.dojinapi.db.table.CharacterTable
import net.hennabatch.dojinapi.db.table.OriginalTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CharacterEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<CharacterEntity>(CharacterTable)

    val name by CharacterTable.name
    val memo by CharacterTable.memo
    val original by OriginalEntity via OriginalTable
    val createdAt by CharacterTable.createdAt
    val updatedAt by CharacterTable.updatedAt

    fun toModel(): Character = Character(
        id = id.value,
        name = name,
        memo = memo,
        original = original.first().toModel(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}