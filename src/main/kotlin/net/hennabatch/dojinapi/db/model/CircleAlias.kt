package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.clientDefault
import net.hennabatch.dojinapi.db.table.AuthorTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

data class CircleAlias (
    val id: Int,
    val circle1: Circle,
    val circle2: Circle,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)