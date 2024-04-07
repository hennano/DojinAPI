package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

class Circle (
    val id: Int,
    val name: String?,
    val memo: String?,
    val members: List<Author>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)