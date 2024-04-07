package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.BookSeries
import net.hennabatch.dojinapi.db.table.BookSeriesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookSeriesEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<BookSeriesEntity>(BookSeriesTable)

    val name by BookSeriesTable.name
    val createdAt by BookSeriesTable.createdAt
    val updatedAt by BookSeriesTable.updatedAt

    fun toModel(): BookSeries = BookSeries(
        id = id.value,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}