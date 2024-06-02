package net.hennabatch.dojinapi.controller.request

data class RequestValidationResult(
    val result: Boolean,
    val reason: String = ""
)