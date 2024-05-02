package net.hennabatch.dojinapi.common.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.logger: Logger
    get() = LoggerFactory.getLogger(this.javaClass)