package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class BookMark(
    val book: Book,
    val page: Int,
    val memo: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
