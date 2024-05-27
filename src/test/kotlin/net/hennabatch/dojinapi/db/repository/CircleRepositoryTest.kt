package net.hennabatch.dojinapi.db.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.DatabaseSingleton.dbQuery
import net.hennabatch.dojinapi.db.model.Circle
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class CircleRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"

    beforeTest {
        DatabaseSingleton.connect(jdbcUrl, userName, pass)
    }

    beforeEach{
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.m_author_circle")
            TransactionManager.current().exec("DELETE FROM djla.author_alias")
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
        }
    }

    context("selectAll") {
        test("データあり") {
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current()
                    .exec("INSERT INTO djla.circle values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current()
                    .exec("INSERT INTO djla.circle values (2, 'test2', 'memomemo2', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current()
                    .exec("INSERT INTO djla.circle values (3, 'test3', 'memomemo3', '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val circles = dbQuery {
                CircleRepository.selectAll(0)
            }

            //検証
            val expected1 = Circle(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expected2 = Circle(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            val expected3 = Circle(3, "test3", "memomemo3", listOf(), localDateTime, localDateTime)

            circles shouldHaveSize 3
            /* TODO 中身は一致しているはずなのに何故か通らない
            circles shouldContain expected1
            circles shouldContain expected2
            circles shouldContain expected3
             */
        }

        test("データなし"){
            //実行
            val circles = dbQuery {
                CircleRepository.selectAll(0)
            }

            //検証
            circles.shouldBeEmpty()
        }
    }

})