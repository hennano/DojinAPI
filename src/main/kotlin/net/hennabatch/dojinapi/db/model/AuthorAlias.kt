package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.hennabatch.dojinapi.db.table.AuthorAliasTable
import net.hennabatch.dojinapi.db.table.AuthorAliasTable.clientDefault
import net.hennabatch.dojinapi.db.table.AuthorTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

data class AuthorAlias (
    val id: Int,
    val author1: Author,
    val author2: Author,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)