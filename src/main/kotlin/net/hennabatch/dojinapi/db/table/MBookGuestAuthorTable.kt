package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MBookGuestAuthorTable: CompositeIdTable("m_book_guest_author")  {
    val bookId = reference("book_id", BookTable)
    val authorId = reference("author_id", AuthorTable)
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    override val primaryKey = PrimaryKey(bookId, authorId)
}