package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class CircleAlias (
    val id: Int,
    val circle1: Circle,
    val circle2: Circle,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)