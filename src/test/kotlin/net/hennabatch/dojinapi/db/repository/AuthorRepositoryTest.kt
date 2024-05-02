package net.hennabatch.dojinapi.db.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.datetime.*
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Author
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class AuthorRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"

    beforeEach{
        DatabaseSingleton.connect(jdbcUrl, userName, pass)
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.author")
        }
    }

    context("selectAll") {
        test("データあり"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo2', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo3', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val authors = dbQuery{
                AuthorRepository.selectAll(0)
            }

            //検証
            val expected1 = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expected2 = Author(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            val expected3 = Author(3, "test3", "memomemo3", listOf(), localDateTime, localDateTime)

            authors shouldHaveSize 3
            authors shouldContain expected1
            authors shouldContain expected2
            authors shouldContain expected3
        }

        test("データなし"){
            //実行
            val authors = dbQuery{
                AuthorRepository.selectAll(0)
            }

            //検証
            authors.shouldBeEmpty()
        }
    }
})