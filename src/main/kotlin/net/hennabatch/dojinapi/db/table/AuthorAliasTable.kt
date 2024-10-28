package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object AuthorAliasTable: CompositeIdTable("author_alias") {
    val authorId1 = reference("author_id_1", AuthorTable)
    val authorId2 = reference("author_id_2", AuthorTable)
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    override val primaryKey = PrimaryKey(authorId1, authorId2)
}