package net.hennabatch.dojinapi.service

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.db.TestableHikariCpDb
import net.hennabatch.dojinapi.logic. CircleServiceLogic
import net.hennabatch.dojinapi.testutils.TestFlags.enableDBAccess
import net.hennabatch.dojinapi.views.CircleResponse
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.time.format.DateTimeFormatter

class CircleServiceTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"
    val db = TestableHikariCpDb()
    var rollBackDetector: StatementInterceptor?

    beforeSpec{
        db.connect(jdbcUrl, userName, pass)
    }

    afterSpec{
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.circle_alias")
            TransactionManager.current().exec("DELETE FROM djla.m_author_circle")
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
        }
    }

    beforeEach{
        startKoin {
            modules(module{
                single<CommonDb>{db}
                single<CircleServiceLogic>{  CircleServiceLogic() }
                single<CircleResponse>{ CircleResponse() }
            })
        }
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.circle_alias")
            TransactionManager.current().exec("DELETE FROM djla.m_author_circle")
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
        }
    }

    afterEach {
        stopKoin()
        db.statementInterceptors.clear()
    }

    context("getCircles"){
        test("正常系_データ0件").config(enabledOrReasonIf = enableDBAccess){
            //実行
            val res = runBlocking {
                 CircleService().getCircles()
            }

            //検証
            res shouldBeEqual JsonObject(mapOf())
        }

        test("正常系_データ1件").config(enabledOrReasonIf = enableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = runBlocking {
                 CircleService().getCircles()
            }

            //検証
            res shouldBeEqual JsonObject(mapOf(
                "1" to JsonPrimitive("test1")
            ))
        }

        test("正常系_データ10件").config(enabledOrReasonIf = enableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                for(i in 1..10){
                    TransactionManager.current().exec("INSERT INTO djla.circle values ($i, 'test$i', 'memomemo$i', '$strLocalDateTime', '$strLocalDateTime')")
                }
            }

            //実行
            val res = runBlocking {
                 CircleService().getCircles()
            }

            //検証
            res shouldBeEqual JsonObject(mapOf(
                "1" to JsonPrimitive("test1"),
                "2" to JsonPrimitive("test2"),
                "3" to JsonPrimitive("test3"),
                "4" to JsonPrimitive("test4"),
                "5" to JsonPrimitive("test5"),
                "6" to JsonPrimitive("test6"),
                "7" to JsonPrimitive("test7"),
                "8" to JsonPrimitive("test8"),
                "9" to JsonPrimitive("test9"),
                "10" to JsonPrimitive("test10")
            ))
        }

        test("異常系_CircleServiceLogicでエラー").config(enabledOrReasonIf = enableDBAccess){
            //準備
            val circleServiceLogicMock = mockk<CircleServiceLogic>{
                every { fetchCircles()} throws Exception()
            }

            var isRollBack = false
            rollBackDetector = registerDetectionRollBack {
                isRollBack = true
            }
            db.statementInterceptors.add(rollBackDetector!!)

            stopKoin()
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<CircleServiceLogic>{circleServiceLogicMock}
                    single<CircleResponse>{CircleResponse()}
                })
            }

            //実行
            shouldThrowAny{
                runBlocking {
                     CircleService().getCircles()
                }
            }

            //実行確認
            verify(exactly = 1) {
                 circleServiceLogicMock.fetchCircles()
            }
            confirmVerified(circleServiceLogicMock)

            //ロールバック検知
            isRollBack.shouldBeTrue()
        }

        test("異常系_AuthorResponseでエラー").config(enabledOrReasonIf = enableDBAccess){
            //準備
            val circleResponseMock = mockk<CircleResponse>{
                every { makeCircleListFetched(any())} throws Exception()
            }
            var isRollBack = false
            rollBackDetector = registerDetectionRollBack {
                isRollBack = true
            }
            db.statementInterceptors.add(rollBackDetector!!)

            stopKoin()
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<CircleServiceLogic>{CircleServiceLogic()}
                    single<CircleResponse>{circleResponseMock}
                })
            }

            //実行
            shouldThrowAny{
                runBlocking {
                     CircleService().getCircles()
                }
            }

            verify(exactly = 1) {
                circleResponseMock.makeCircleListFetched(any())
            }
            confirmVerified(circleResponseMock)
            isRollBack.shouldBeTrue()
        }
    }
})

//トランザクションロールバックを検知
private fun registerDetectionRollBack(callback: () -> Unit): StatementInterceptor{
    return object : StatementInterceptor {
        override fun afterRollback(transaction: Transaction) {
            callback()
        }
    }
}

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
