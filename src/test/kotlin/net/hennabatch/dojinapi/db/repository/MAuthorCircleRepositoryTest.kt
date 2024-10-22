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

class MAuthorCircleRepositoryTest: FunSpec({
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
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
        }
    }

    context("insert") {
        test("登録"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            db.dbQuery {
                MAuthorCircleRepository.insert(1, 1)
            }

            //検証
            val result = execRawSelectQuery("SELECT * FROM djla.m_author_circle") // わざと全件取得し、1個だけできていることを確認する
            result shouldHaveSize 1
            assertMAuthorCircle(1, 1, result[0])
        }

        test("登録_該当のAuthorなし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            shouldThrow<ExposedSQLException> {
                db.dbQuery {
                    MAuthorCircleRepository.insert(1, 1)
                }
            }
        }

        test("登録_該当のCircleなし"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            shouldThrow<ExposedSQLException> {
                db.dbQuery {
                    MAuthorCircleRepository.insert(1, 1)
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
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                MAuthorCircleRepository.delete(1, 1)
            }

            //検証
            res.shouldBeTrue()
        }

        test("削除対象なし_AuthorIdが違う"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                MAuthorCircleRepository.delete(2, 1)
            }

            //検証
            res.shouldBeFalse()
        }

        test("削除対象なし_CircleIdが違う"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                MAuthorCircleRepository.delete(1, 2)
            }

            //検証
            res.shouldBeFalse()
        }

        test("削除対象なし_AuthorIdとCircleIdが違う"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.m_author_circle values (1, 1, '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = db.dbQuery{
                MAuthorCircleRepository.delete(2, 2)
            }

            //検証
            res.shouldBeFalse()
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

private fun assertMAuthorCircle(authorId: Int, circleId: Int, actual: Map<String, Any?>){
    Integer.parseInt(actual["author_id"].toString()) shouldBe authorId
    Integer.parseInt(actual["circle_id"].toString()) shouldBe circleId
    //substring(0, 23)はナノ秒切り捨て用
    LocalDateTime.parse(actual["created_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault())
    LocalDateTime.parse(actual["updated_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault())

}