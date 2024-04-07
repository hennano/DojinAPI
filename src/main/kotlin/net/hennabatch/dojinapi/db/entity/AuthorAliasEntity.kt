package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AuthorAliasEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<AuthorAliasEntity>(AuthorAliasTable)

    val authorId1 by AuthorEntity.referencedOn(AuthorAliasTable.authorId1)
    val authorId2 by AuthorEntity.referencedOn(AuthorAliasTable.authorId2)
    val createdAt by AuthorAliasTable.createdAt
    val updatedAt by AuthorAliasTable.updatedAt

    fun toModel():AuthorAlias = AuthorAlias(
        id = id.value,
        author1 = authorId1.toModel(),
        author2 = authorId2.toModel(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}