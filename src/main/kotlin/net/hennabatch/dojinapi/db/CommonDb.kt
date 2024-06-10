package net.hennabatch.dojinapi.db

import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import net.hennabatch.dojinapi.common.utils.logger

interface CommonDb {

    fun init(config: ApplicationConfig)

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        runBlocking {
            logger.warn("!!!!DO TEST ONLY!!!!")
            block()
        }
}