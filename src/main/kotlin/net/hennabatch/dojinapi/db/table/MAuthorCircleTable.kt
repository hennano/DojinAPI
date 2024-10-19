package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MAuthorCircleTable: CompositeIdTable("m_author_circle") {
    val authorId = reference("author_id", AuthorTable)
    val circleId = reference("circle_id", CircleTable)
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
    val updatedAt = datetime("updated_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    override val primaryKey = PrimaryKey(authorId, circleId)
}