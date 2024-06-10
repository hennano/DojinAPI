package net.hennabatch.dojinapi.db.repository

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.kotlinx.datetime.shouldBeAfter
import kotlinx.datetime.*
import net.hennabatch.dojinapi.db.HikariCpDb
import net.hennabatch.dojinapi.db.model.Author
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class AuthorRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"
    val db = HikariCpDb()

    beforeTest {
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

    context("select"){
        test("データあり_resolveDepth0"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val res = db.dbQuery{
                AuthorRepository.select(1, 0)
            }

            //検証
            val expected = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)

            res shouldBeEqual expected
        }

        test("データあり_resolveDepth1_circleあり"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val res = db.dbQuery{
                AuthorRepository.select(1, 1)
            }

            //検証
            res.name?.shouldBeEqual("testAuthor")
            res.memo?.shouldBeEqual("memoAuthor1")
            res.createdAt?.shouldBeEqual(localDateTime)
            res.updatedAt?.shouldBeEqual(localDateTime)
            res.joinedCircles shouldHaveSize 1
            res.joinedCircles[0].name?.shouldBeEqual("testCircle")
            res.joinedCircles[0].memo?.shouldBeEqual("memoCircle1")
            res.joinedCircles[0].members.shouldBeEmpty()
            res.joinedCircles[0].createdAt?.shouldBeEqual(localDateTime)
            res.joinedCircles[0].updatedAt?.shouldBeEqual(localDateTime)
        }

        test("データあり_resolveDepth1_circleなし"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val res = db.dbQuery{
                AuthorRepository.select(1, 1)
            }

            //検証
            res.name?.shouldBeEqual("testAuthor")
            res.memo?.shouldBeEqual("memoAuthor1")
            res.createdAt?.shouldBeEqual(localDateTime)
            res.updatedAt?.shouldBeEqual(localDateTime)
            res.joinedCircles.shouldBeEmpty()
        }

        test("データなし"){
            //実行
            shouldThrow<EntityNotFoundException> {
                db.dbQuery{
                    AuthorRepository.select(1, 0)
                }
            }
        }
    }

    context("selectAll"){
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
            val authors = db.dbQuery{
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
            val authors = db.dbQuery{
                AuthorRepository.selectAll(0)
            }

            //検証
            authors.shouldBeEmpty()
        }
    }

    context("insert"){
        test("登録"){
            //準備
            val name = "test"
            val memo = "memo"
            val now = Clock.System.now()

            //実行
            val id = db.dbQuery{
                AuthorRepository.insert(name, memo)
            }

            //検証
            id shouldBeGreaterThan 0

            val res = db.dbQuery{
                AuthorRepository.select(id, 0)
            }

            res.name?.shouldBeEqual(name)
            res.memo?.shouldBeEqual(memo)
            res.joinedCircles.shouldBeEmpty()
            res.createdAt?.shouldBeAfter(now.toLocalDateTime(TimeZone.currentSystemDefault()))
            res.updatedAt?.shouldBeAfter(now.toLocalDateTime(TimeZone.currentSystemDefault()))
        }
    }

    context("delete"){
        test("削除対象あり"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorRepository.delete(1)
            }

            result.shouldBeTrue()

            shouldThrow<EntityNotFoundException> {
                db.dbQuery {
                    AuthorRepository.select(1, 0)
                }
            }
        }

        test("削除対象なし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorRepository.delete(2)
            }

            result.shouldBeFalse()

            shouldNotThrow<EntityNotFoundException> {
                db.dbQuery {
                    AuthorRepository.select(1, 0)
                }
            }
        }
    }
})