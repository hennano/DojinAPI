package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.Event
import net.hennabatch.dojinapi.db.table.EventSeriesTable
import net.hennabatch.dojinapi.db.table.EventTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EventEntity (id: EntityID<Int>) : IntEntity(id)  {
    companion object: IntEntityClass<EventEntity>(EventTable)

    val name by EventTable.name
    val memo by EventTable.memo
    val heldAt by EventTable.heldAt
    val location by EventTable.location
    val eventSeriesId by EventSeriesEntity via EventSeriesTable
    val createdAt by EventTable.createdAt
    val updatedAt by EventTable.updatedAt

    fun toModel(): Event = Event(
        id = id.value,
        name = name,
        memo = memo,
        heldAt = heldAt,
        location = location,
        eventSeries = eventSeriesId.first().toModel(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}