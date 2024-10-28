package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime


data class Author(
    val id: Int,
    val name: String?,
    val memo: String?,
    val alias: List<Author>,
    val joinedCircles: List<Circle>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)