package net.hennabatch.dojinapi.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.controller.request.AuthorRequestEntity
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.db.HikariCpDb
import net.hennabatch.dojinapi.db.TestableHikariCpDb
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.logic.AuthorServiceLogic
import net.hennabatch.dojinapi.testutils.TestFlags.disableDBAccess
import net.hennabatch.dojinapi.views.AuthorResponse
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementInterceptor
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.time.format.DateTimeFormatter

class AuthorServiceTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"
    val db = TestableHikariCpDb()
    var rollBackDetector: StatementInterceptor? = null

    beforeTest{
        db.connect(jdbcUrl, userName, pass)
    }

    beforeEach{
        startKoin {
            modules(module{
                single<CommonDb>{db}
                single<AuthorServiceLogic>{AuthorServiceLogic()}
                single<AuthorResponse>{AuthorResponse()}
            })
        }
        transaction {
            TransactionManager.current().exec("DELETE FROM djla.author_alias")
            TransactionManager.current().exec("DELETE FROM djla.author")
        }
    }

    afterEach {
        stopKoin()
        db.statementInterceptors.clear()
    }

    context("getAuthors"){
        test("正常系_データ0件").config(enabledOrReasonIf = disableDBAccess){
            //実行
            val res = runBlocking {
                AuthorService().getAuthors()
            }

            //検証
            res shouldBeEqual JsonObject(mapOf())
        }

        test("正常系_データ1件").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'test1', 'memomemo1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val res = runBlocking {
                AuthorService().getAuthors()
            }

            //検証
            res shouldBeEqual JsonObject(mapOf(
                "1" to JsonPrimitive("test1")
            ))
        }

        test("正常系_データ10件").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                for(i in 1..10){
                    TransactionManager.current().exec("INSERT INTO djla.author values ($i, 'test$i', 'memomemo$i', '$strLocalDateTime', '$strLocalDateTime')")
                }
            }

            //実行
            val res = runBlocking {
                AuthorService().getAuthors()
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

        test("異常系_AuthorServiceLogicでエラー").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { fetchAllAuthors()} throws Exception()
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
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{AuthorResponse()}
                })
            }

            //実行
            shouldThrowAny{
                runBlocking {
                    AuthorService().getAuthors()
                }
            }

            //実行確認
            verify(exactly = 1) {
                authorServiceLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorServiceLogicMock)

            //ロールバック検知
            isRollBack.shouldBeTrue()
        }

        test("異常系_AuthorResponseでエラー").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any())} throws Exception()
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
                    single<AuthorServiceLogic>{AuthorServiceLogic()}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            shouldThrowAny{
                runBlocking {
                    AuthorService().getAuthors()
                }
            }

            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(any())
            }
            confirmVerified(authorResponseMock)
            isRollBack.shouldBeTrue()
        }
    }
    /*

    context("postAuthor"){
        test("正常系"){
            //準備
            val db = spyk<CommonDb>()

            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { insertAuthor(any(), any(), any())} returns 1
            }
            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns resJson

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf()
            )
            val res = runBlocking {
                AuthorService().postAuthor(request)
            }

            //検証
            res shouldBeEqual resJson

            verify(exactly = 1) {
                authorServiceLogicMock.insertAuthor("test1", "", listOf())
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_AuthorServiceLogicでエラー"){
            //準備
            val db = spyk<CommonDb>()

            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { insertAuthor(any(), any(), any())} throws Exception()
            }
            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns resJson

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf()
            )
            shouldThrowAny{
                runBlocking {
                    AuthorService().postAuthor(request)
                }
            }

            //検証
            verify(exactly = 1) {
                authorServiceLogicMock.insertAuthor("test1", "", listOf())
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_AuthorResponseでエラー"){
            //準備
            val db = spyk<CommonDb>()

            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { insertAuthor(any(), any(), any())} returns 1
            }
            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } throws Exception()

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf()
            )
            shouldThrowAny{
                runBlocking {
                    AuthorService().postAuthor(request)
                }
            }

            //検証
            verify(exactly = 1) {
                authorServiceLogicMock.insertAuthor("test1", "", listOf())
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_AuthorResponseでエラー_dbロールバック確認").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val db = HikariCpDb()
            db.connect(jdbcUrl, userName, pass)

            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } throws Exception()

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{AuthorServiceLogic()}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf()
            )
            shouldThrowAny{
                runBlocking {
                    AuthorService().postAuthor(request)
                }
            }

            //検証
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(any(), "test1")
            }
            confirmVerified(authorResponseMock)

            logger.info("ロールバックされた旨のログが出ていないか確認する")
            //TODO プログラム上で検証できるようにする
        }
    }

    context("getAuthor"){
        test("正常系"){
            //準備
            val db = spyk<CommonDb>()

            val expectedAuthor = Author(1, "test1", null, listOf(), null, null)

            val authorRes =
                expectedAuthor to listOf<AuthorAlias>()

            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { fetchAuthor(any())} returns authorRes
            }

            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } returns resJson

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val res = runBlocking {
                AuthorService().getAuthor(1)
            }

            //検証
            res shouldBeEqual resJson

            verify(exactly = 1) {
                authorServiceLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorFetched(expectedAuthor, listOf())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_AuthorServiceLogicでエラー"){
            //準備
            val db = spyk<CommonDb>()

            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null)
            )
            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { fetchAllAuthors()} throws Exception()
            }
            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any()) } returns resJson

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }


            //実行
            shouldThrowAny{
                runBlocking {
                    AuthorService().getAuthors()
                }
            }

            verify(exactly = 1) {
                authorServiceLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorListFetched(authors)
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_AuthorResponseでエラー"){
            //準備
            val db = spyk<CommonDb>()

            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null)
            )
            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { fetchAllAuthors()} returns authors
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any()) } throws Exception()
            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            shouldThrowAny{
                runBlocking {
                    AuthorService().getAuthors()
                }
            }

            verify(exactly = 1) {
                authorServiceLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(authors)
            }
            confirmVerified(authorResponseMock)
        }
    }

    //TODO
    context("putAuthor"){
        test("正常系"){
            //準備
            val db = spyk<CommonDb>()

            val expectedAuthor = Author(1, "test1", null, listOf(), null, null)

            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { updateAuthor(any(), any(), any(), any())} returns expectedAuthor.id
            }

            val resJson = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } returns resJson

            }
            startKoin {
                modules(module{
                    single<CommonDb>{db}
                    single<AuthorServiceLogic>{authorServiceLogicMock}
                    single<AuthorResponse>{authorResponseMock}
                })
            }

            //実行
            val res = runBlocking {
                AuthorService().putAuthor(1, AuthorRequestEntity("test1", null, listOf()))
            }

            //検証
            res shouldBeEqual resJson

            verify(exactly = 1) {
                authorServiceLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorFetched(expectedAuthor, listOf())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_異常系_AuthorServiceLogicでエラー"){

        }

        test("異常系_AuthorResponseでエラー"){

        }
    }
     */
})

//トランザクションロールバックを検知
fun registerDetectionRollBack(callback: () -> Unit): StatementInterceptor{
    return object : StatementInterceptor {
        override fun afterRollback(transaction: Transaction) {
            callback()
        }
    }
}