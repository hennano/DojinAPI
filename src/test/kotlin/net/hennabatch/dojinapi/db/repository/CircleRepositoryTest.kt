package net.hennabatch.dojinapi.db.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import net.hennabatch.dojinapi.db.HikariCpDb
import net.hennabatch.dojinapi.db.model.Circle
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class CircleRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"
    val db = HikariCpDb()

    beforeSpec {
        db.connect(jdbcUrl, userName, pass)
    }

    beforeEach{
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.m_author_circle")
            TransactionManager.current().exec("DELETE FROM djla.author_alias")
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
        }
    }

    context("select") {
        test("データあり_データあり_resolveDepth0"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                CircleRepository.select(1, 0)
            }

            //検証
            val expected = Circle(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)

            res shouldBeEqual expected
        }

        test("データあり_resolveDepth1_authorあり"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                CircleRepository.select(1, 1)
            }

            //検証
            res.name?.shouldBeEqual("testCircle")
            res.memo?.shouldBeEqual("memoCircle1")
            res.createdAt?.shouldBeEqual(localDateTime)
            res.updatedAt?.shouldBeEqual(localDateTime)
            res.members shouldHaveSize 1
            res.members[0].name?.shouldBeEqual("testAuthor")
            res.members[0].memo?.shouldBeEqual("memoAuthor1")
            res.members[0].joinedCircles.shouldBeEmpty()
            res.members[0].createdAt?.shouldBeEqual(localDateTime)
            res.members[0].updatedAt?.shouldBeEqual(localDateTime)
        }

        test("データあり_resolveDepth1_authorなし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                CircleRepository.select(1, 0)
            }

            //検証
            val expected = Circle(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            res shouldBeEqual expected
        }

        test("データなし"){
            shouldThrow<EntityNotFoundException> {
                db.dbQuery { CircleRepository.select(1, 0) }
            }
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
            val circles = db.dbQuery {
                CircleRepository.selectAll(0)
            }

            //検証
            val expected1 = Circle(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expected2 = Circle(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            val expected3 = Circle(3, "test3", "memomemo3", listOf(), localDateTime, localDateTime)

            circles shouldHaveSize 3
            circles shouldContain expected1
            circles shouldContain expected2
            circles shouldContain expected3
        }

        test("データなし"){
            //実行
            val circles = db.dbQuery {
                CircleRepository.selectAll(0)
            }

            //検証
            circles.shouldBeEmpty()
        }
    }

})