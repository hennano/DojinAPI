package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime
import java.net.InetAddress

data class AllowFullAccessIp (
    val id: Int,
    val ip: InetAddress,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)