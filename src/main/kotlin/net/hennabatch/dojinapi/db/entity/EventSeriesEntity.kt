package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.EventSeries
import net.hennabatch.dojinapi.db.table.EventSeriesTable
import net.hennabatch.dojinapi.db.table.EventTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EventSeriesEntity (id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<EventSeriesEntity>(EventSeriesTable)

    val name by EventSeriesTable.name
    val memo by EventSeriesTable.memo
    val events by EventEntity via EventTable
    val createdAt by EventSeriesTable.createdAt
    val updatedAt by EventSeriesTable.updatedAt

    fun toModel(resoleDepth: Int = 1): EventSeries = EventSeries(
        id = id.value,
        name = name,
        memo = memo,
        events = if(resoleDepth > 0) events.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}