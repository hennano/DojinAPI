package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class Rating(
    val id: Int,
    val name: String?,
    val memo: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
