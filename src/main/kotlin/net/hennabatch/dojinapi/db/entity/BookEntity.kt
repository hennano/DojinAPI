package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.*
import net.hennabatch.dojinapi.db.table.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<BookEntity>(BookTable)

    val author by AuthorEntity via AuthorTable
    val circle by CircleEntity via CircleTable
    val series by BookSeriesEntity via BookSeriesTable
    val originals by OriginalEntity via MBookOriginalTable
    val characters by CharacterEntity via MBookCharactersTable
    val releasedEvent by EventEntity via EventTable
    val releasedAt by BookTable.releasedAt
    val purchasedAt by BookTable.purchasedAt
    val name by BookTable.name
    val memo by BookTable.memo
    val bookImagesPath by BookTable.bookImagesPath
    val createdAt by BookTable.createdAt
    val updatedAt by BookTable.updatedAt

    fun toModel(): Book = Book(
        id = id.value,
        author = author.first().toModel(),
        circle = circle.first().toModel(),
        series = series.first().toModel(),
        originals = originals.map { it.toModel() }.toList(),
        characters = characters.map { it.toModel() }.toList(),
        releasedEvent = releasedEvent.first().toModel(),
        releasedAt = releasedAt,
        purchasedAt = purchasedAt,
        name = name,
        memo = memo,
        bookImagesPath = bookImagesPath,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}