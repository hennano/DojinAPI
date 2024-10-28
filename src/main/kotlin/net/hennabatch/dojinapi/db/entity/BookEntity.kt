package net.hennabatch.dojinapi.db.entity

import net.hennabatch.dojinapi.db.model.*
import net.hennabatch.dojinapi.db.table.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BookEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<BookEntity>(BookTable)

    val author by AuthorEntity via AuthorTable
    val guestAuthors by AuthorEntity via MBookGuestAuthorTable
    val circle by CircleEntity via CircleTable
    val series by BookSeriesEntity via BookSeriesTable
    val originals by OriginalEntity via MBookOriginalTable
    val characters by CharacterEntity via MBookCharacterTable
    val releasedEvent by EventEntity via EventTable
    val releasedAt by BookTable.releasedAt
    val purchasedAt by BookTable.purchasedAt
    val name by BookTable.name
    val memo by BookTable.memo
    val rating by RatingEntity via RatingTable
    val bookImagesPath by BookTable.bookImagesPath
    val bookMarks by BookMarkEntity via BookMarkTable
    val createdAt by BookTable.createdAt
    val updatedAt by BookTable.updatedAt

    fun toModel(resoleDepth: Int = 1): Book = Book(
        id = id.value,
        author = author.first().toModel(resoleDepth - 1),
        guestAuthors = if(resoleDepth > 0) guestAuthors.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        circle = circle.first().toModel(resoleDepth - 1),
        series = series.first().toModel(resoleDepth - 1) ,
        originals = if(resoleDepth > 0) originals.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        characters = if(resoleDepth > 0) characters.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        releasedEvent = releasedEvent.first().toModel(resoleDepth - 1),
        releasedAt = releasedAt,
        purchasedAt = purchasedAt,
        name = name,
        memo = memo,
        rating = rating.first().toModel(resoleDepth - 1),
        bookImagesPath = bookImagesPath,
        bookMarks = if(resoleDepth > 0) bookMarks.map { it.toModel(resoleDepth - 1) }.toList() else listOf(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}