package net.hennabatch.dojinapi.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class HikariCpDb: CommonDb {

    private lateinit var db: Database

    override fun init(config: ApplicationConfig){
        val jdbcUrl = config.property("dojinapi.db.url").getString()
        val userName = config.property("dojinapi.db.userName").getString()
        val password = config.property("dojinapi.db.password").getString()
        connect(jdbcUrl, userName, password)
    }

    fun connect(jdbcUrl: String, userName: String, password: String){
        db = Database.connect(
            createHikariDataSource(
                url = jdbcUrl,
                userName = userName,
                pass = password
            )
        )
    }

    private fun createHikariDataSource(
        url: String,
        userName: String,
        pass: String
    ) = HikariDataSource(
        HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = url
            username = userName
            password = pass
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
    )

    override suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, db) { block() }
}