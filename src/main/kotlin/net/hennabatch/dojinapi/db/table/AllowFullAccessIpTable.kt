package net.hennabatch.dojinapi.db.table

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.hennabatch.dojinapi.db.columntype.InetColumnType
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import java.net.InetAddress

object AllowFullAccessIpTable: IntIdTable("allow_full_access_ip"){
    val ip = inet("ip")
    val createdAt = datetime("created_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()
    val updatedAt = datetime("updated_at").clientDefault { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }.nullable()

    private fun inet(name: String) = registerColumn<InetAddress>(name, InetColumnType())
}