package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CircleAliasTable: CompositeIdTable("circle_alias") {
    val circleId1 = reference("circle_id_1", CircleTable)
    val circleId2 = reference("circle_id_2", CircleTable)
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    override val primaryKey = PrimaryKey(circleId1, circleId2)
}