package net.hennabatch.dojinapi.db.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.kotlinx.datetime.shouldBeBefore
import io.kotest.matchers.shouldBe
import kotlinx.datetime.*
import net.hennabatch.dojinapi.db.HikariCpDb
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class AuthorAliasRepositoryTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"
    val db = HikariCpDb()

    beforeSpec {
        db.connect(jdbcUrl, userName, pass)
    }

    beforeEach {
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.author_alias")
            TransactionManager.current().exec("DELETE FROM djla.author")
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

            //実行
            db.dbQuery {
                AuthorAliasRepository.insert(1, 1)
            }

            //検証
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 1
            assertAuthorAlias(1, 1, resultAlias[0])
        }

        test("登録_該当のAuthorなし"){
            //実行
            shouldThrow<ExposedSQLException> {
                db.dbQuery {
                    AuthorAliasRepository.insert(1, 1)
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
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 1, '$strLocalDateTime')")
            }
            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.delete(1, 1)
            }

            //検証
            result.shouldBeTrue()
            //AuthorAliasテーブル
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 0
        }

        test("削除対象なし"){
            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.delete(1, 1)
            }

            //検証
            result.shouldBeFalse()
        }
    }

    context("deletesIncludedByAuthorId"){
        test("削除対象あり_author_id1"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesIncludedByAuthorId(1)
            }

            //検証
            result shouldBe 2
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 1
            assertAuthorAlias(2, 3, resultAlias[0])
        }

        test("削除対象あり_author_id2"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesIncludedByAuthorId(3)
            }

            //検証
            result shouldBe 2

            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 1
            assertAuthorAlias(1, 2, resultAlias[0])
        }

        test("削除対象あり_両方"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesIncludedByAuthorId(2)
            }

            //検証
            result shouldBe 2

            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 1
            assertAuthorAlias(1, 3, resultAlias[0])
        }

        test("削除対象なし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesIncludedByAuthorId(4)
            }

            //検証
            result shouldBe 0

            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 3
            assertAuthorAlias(1, 2, resultAlias[0])
            assertAuthorAlias(1, 3, resultAlias[1])
            assertAuthorAlias(2, 3, resultAlias[2])
        }
    }

    context("deletesRelation"){
        test("削除対象あり_author_id1"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesRelation(1, listOf(2))
            }

            //検証
            result shouldBe 1
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias")
            resultAlias shouldHaveSize 2
            assertAuthorAlias(1, 3, resultAlias[0])
            assertAuthorAlias(2, 3, resultAlias[1])
        }

        test("削除対象あり_author_id2"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesRelation(3, listOf(1))
            }

            //検証
            result shouldBe 1
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias")
            resultAlias shouldHaveSize 2
            assertAuthorAlias(1, 2, resultAlias[0])
            assertAuthorAlias(2, 3, resultAlias[1])
        }

        test("削除対象あり_両方"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesRelation(2, listOf(1, 3))
            }

            //検証
            result shouldBe 2
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias")
            resultAlias shouldHaveSize 1
            assertAuthorAlias(1, 3, resultAlias[0])
        }

        test("削除対象なし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (2, 'test2', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author values (3, 'test3', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 2, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (1, 3, '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.author_alias values (2, 3, '$strLocalDateTime')")
            }

            //実行
            val result = db.dbQuery {
                AuthorAliasRepository.deletesRelation(1, listOf(4))
            }

            //検証
            result shouldBe 0
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias")
            resultAlias shouldHaveSize 3
            assertAuthorAlias(1, 2, resultAlias[0])
            assertAuthorAlias(1, 3, resultAlias[1])
            assertAuthorAlias(2, 3, resultAlias[2])
        }

    }
})

private fun execRawSelectQuery(query: String): List<Map<String, Any?>>{
    return transaction {
        exec(query){ rs ->
            val resultsList = mutableListOf<Map<String, Any?>>()
            while (rs.next()){
                val row = mutableMapOf<String, Any?>()
                for( i in 1..rs.metaData.columnCount){
                    row[rs.metaData.getColumnName(i)] = rs.getObject(i)
                }
                resultsList.add(row)
            }
            resultsList
        } ?: listOf()
    }
}

private fun assertAuthorAlias(authorId1: Int, authorId2: Int, actual: Map<String, Any?>){
    Integer.parseInt(actual["author_id_1"].toString()) shouldBe authorId1
    Integer.parseInt(actual["author_id_2"].toString()) shouldBe authorId2
    //substring(0, 23)はナノ秒切り捨て用
    LocalDateTime.parse(actual["created_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}