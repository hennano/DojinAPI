package net.hennabatch.dojinapi.controller.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorRequestEntity(
    @SerialName("name") val name: String,
    @SerialName("memo") val memo: String,
    @SerialName("author_alias") val authorAlias: List<Int>
)