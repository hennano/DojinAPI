package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.BookMark
import net.hennabatch.dojinapi.db.table.BookMarkTable
import net.hennabatch.dojinapi.db.table.BookTable
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.EntityID

class BookMarkEntity(id: EntityID<CompositeID>): CompositeEntity(id) {
    companion object: CompositeEntityClass<BookMarkEntity>(BookMarkTable)

    val book by BookEntity via BookTable
    val page by BookMarkTable.page
    val memo by BookMarkTable.memo
    val createdAt by BookMarkTable.createdAt
    val updatedAt by BookMarkTable.updatedAt

    fun toModel(resoleDepth: Int = 1): BookMark = BookMark(
        book = book.first().toModel(resoleDepth - 1),
        page = page,
        memo = memo,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}