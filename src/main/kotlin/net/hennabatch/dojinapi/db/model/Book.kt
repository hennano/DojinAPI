package net.hennabatch.dojinapi.db.model

import kotlinx.datetime.LocalDateTime

data class Book (
    val id: Int,
    val author: Author?,
    val guestAuthors: List<Author>,
    val circle: Circle?,
    val series: BookSeries?,
    val originals: List<Original>,
    val characters: List<Character>,
    val releasedEvent: Event?,
    val releasedAt: LocalDateTime?,
    val purchasedAt: LocalDateTime?,
    val name: String?,
    val memo: String?,
    val rating: Rating,
    val bookImagesPath: String?,
    val bookMarks: List<BookMark>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)