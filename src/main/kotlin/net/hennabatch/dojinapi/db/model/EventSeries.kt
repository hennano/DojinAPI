package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class EventSeries(
    val id: Int,
    val name: String?,
    val memo: String?,
    val events: List<Event>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)