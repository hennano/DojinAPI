package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class BookSeries (
    val id: Int,
    val name: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)