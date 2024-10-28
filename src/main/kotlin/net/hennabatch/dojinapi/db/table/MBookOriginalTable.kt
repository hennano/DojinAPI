package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MBookOriginalTable: CompositeIdTable("m_book_original") {
    val bookId = reference("book_id", BookTable)
    val originalId = reference("original_id", OriginalTable)
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    override val primaryKey = PrimaryKey(bookId, originalId)
}