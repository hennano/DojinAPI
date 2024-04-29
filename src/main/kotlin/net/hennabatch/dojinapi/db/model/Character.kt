package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class Character (
    val id: Int,
    val name: String?,
    val memo: String?,
    val original: Original?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)