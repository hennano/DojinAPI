package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class Original (
    val id: Int,
    val name: String?,
    val memo: String?,
    val parentOriginal: Original?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)