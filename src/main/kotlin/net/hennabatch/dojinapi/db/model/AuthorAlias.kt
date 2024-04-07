package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class AuthorAlias (
    val id: Int,
    val author1: Author,
    val author2: Author,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)