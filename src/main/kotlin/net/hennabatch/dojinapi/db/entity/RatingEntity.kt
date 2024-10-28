package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Rating
import net.hennabatch.dojinapi.db.table.RatingTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RatingEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<RatingEntity>(RatingTable)

    val name by RatingTable.name
    val memo by RatingTable.memo
    val createdAt by RatingTable.createdAt
    val updatedAt by RatingTable.updatedAt

    //他のものと合わせるためにresoleDepthを入れているが使ってない
    fun toModel(resoleDepth: Int = 1): Rating = Rating(
        id = id.value,
        name = name,
        memo = memo,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}