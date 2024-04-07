package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.hennabatch.dojinapi.db.model.Original
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object BookTable: IntIdTable("book")  {
    val authorId = reference("author_id", AuthorTable).nullable()
    val circleId = reference("circle_id", CircleTable).nullable()
    val series = reference("series_id", BookSeriesTable).nullable()
    val original = reference("original_id", OriginalTable).nullable()
    val releasedEvent = reference("released_event_id", EventTable).nullable()
    val releasedAt = datetime("released_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
    val purchasedAt =datetime("purchased_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
    val name = varchar("name", 255).nullable()
    val memo = text("memo").nullable()
    val bookImagesPath = varchar("book_images_path", 255).nullable()
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
    val updatedAt = datetime("updated_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
}