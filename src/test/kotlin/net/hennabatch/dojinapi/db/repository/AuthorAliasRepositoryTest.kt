package net.hennabatch.dojinapi.db.repository

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
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException
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
            //準備
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

    context("selectsByAuthorId"){
        test("データあり_authorId1"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo2', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo3', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, 2, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val aliases = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }

            //検証
            val expectedAuthor1 = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expectedAuthor2 = Author(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            val expected = AuthorAlias(1, expectedAuthor1, expectedAuthor2, localDateTime, localDateTime)
            aliases shouldHaveSize 1
            aliases shouldContain expected
        }

        test("データあり_authorId2"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo2', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo3', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, 2, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val aliases = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.selectsByAuthorId(2, 0)
            }

            //検証
            val expectedAuthor1 = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expectedAuthor2 = Author(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            // 検索対象がauthorID1に整列される
            val expected = AuthorAlias(1, expectedAuthor2, expectedAuthor1, localDateTime, localDateTime)
            aliases shouldHaveSize 1
            aliases shouldContain expected
        }

        test("データあり_複数"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo2', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo3', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, 2, '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val aliases = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }

            //検証
            val expectedAuthor1 = Author(1, "test1", "memomemo1", listOf(), localDateTime, localDateTime)
            val expectedAuthor2 = Author(2, "test2", "memomemo2", listOf(), localDateTime, localDateTime)
            val expectedAuthor3 = Author(3, "test3", "memomemo3", listOf(), localDateTime, localDateTime)
            val expected1 = AuthorAlias(1, expectedAuthor1, expectedAuthor2, localDateTime, localDateTime)
            // 検索対象がauthorID1に整列される
            val expected2 = AuthorAlias(2, expectedAuthor1, expectedAuthor3, localDateTime, localDateTime)
            aliases shouldHaveSize 2
            aliases shouldContain expected1
            aliases shouldContain expected2
        }

        test("データなし"){
            //実行
            val aliases = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            aliases.shouldBeEmpty()
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

        test("登録_該当のAuthorなし"){
            //実行
            shouldThrow<PSQLException> {
                DatabaseSingleton.dbQuery {
                    AuthorAliasRepository.insert(1,1)
                }
            }
        }
    }

    context("delete"){
        test("削除対象あり"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }
            //実行
            val result = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.delete(1)
            }

            //検証
            result.shouldBeTrue()

            shouldThrow<EntityNotFoundException> {
                DatabaseSingleton.dbQuery {
                    AuthorAliasRepository.select(1, 0)
                }
            }
        }
        test("削除対象なし"){
            //実行
            val result = DatabaseSingleton.dbQuery {
                AuthorAliasRepository.delete(1)
            }

            //検証
            result.shouldBeFalse()
        }
    }
})