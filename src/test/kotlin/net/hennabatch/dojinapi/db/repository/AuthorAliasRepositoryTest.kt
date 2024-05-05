package net.hennabatch.dojinapi.db.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.kotlinx.datetime.shouldBeAfter
import kotlinx.datetime.*
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class AuthorAliasRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"

    beforeTest {
        DatabaseSingleton.connect(jdbcUrl, userName, pass)
    }

    beforeEach {
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.author_alias")
            TransactionManager.current().exec("DELETE FROM djla.author")
        }
    }

    context("select"){
        test("データあり_resolveDepth0"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val res = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.select(1, 0)
            }

            //検証
            val expectedAuthor = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expected = AuthorAlias(1, expectedAuthor, expectedAuthor, localDateTime, localDateTime)

            res shouldBeEqual expected
        }

        test("データあり_resolveDepth1"){
            logger.info("データあり_resolveDepth0と同じ")
        }

        test("データなし"){
            //実行
            shouldThrow<EntityNotFoundException> {
                DatabaseSingleton.dbQuery {
                    AuthorAliasRepository.select(1, 0)
                }
            }
        }
    }

    context("insert"){
        test("登録"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }
            val now = Clock.System.now()


            //実行
            val id = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.insert(1,1)
            }

            //検証
            id shouldBeGreaterThan 0

            val res = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.select(1, 0)
            }

            //検証
            val expectedAuthor = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)

            res.author1 shouldBeEqual expectedAuthor
            res.author2 shouldBeEqual expectedAuthor
            res.createdAt?.shouldBeAfter(now.toLocalDateTime(TimeZone.currentSystemDefault()))
            res.updatedAt?.shouldBeAfter(now.toLocalDateTime(TimeZone.currentSystemDefault()))
        }
    }
})