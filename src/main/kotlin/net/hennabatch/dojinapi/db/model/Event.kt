package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class Event (
    val id: Int,
    val name: String?,
    val memo: String?,
    val heldAt: LocalDateTime?,
    val location: String?,
    val eventSeries: EventSeries?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)