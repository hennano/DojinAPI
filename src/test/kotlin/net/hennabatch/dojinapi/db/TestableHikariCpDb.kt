package net.hennabatch.dojinapi.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TestableHikariCpDb: CommonDb {

    private lateinit var db: Database

    val statementInterceptors = mutableListOf<StatementInterceptor>()

    //利用しない
    override fun init(config: ApplicationConfig): Unit = Unit

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
        newSuspendedTransaction(Dispatchers.IO, db) {
            statementInterceptors.forEach{
                TransactionManager.current().registerInterceptor(it)
            }
            block()
        }
}