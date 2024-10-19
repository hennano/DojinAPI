package net.hennabatch.dojinapi.service


import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.kotlinx.datetime.shouldBeBefore
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import net.hennabatch.dojinapi.controller.request.AuthorRequestEntity
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.db.TestableHikariCpDb
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
    var rollBackDetector: StatementInterceptor?

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
            TransactionManager.current().exec("DELETE FROM djla.m_author_circle")
            TransactionManager.current().exec("DELETE FROM djla.author")
            TransactionManager.current().exec("DELETE FROM djla.circle")
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

    context("postAuthor"){
        test("正常系_最小").config(enabledOrReasonIf = disableDBAccess){
            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf(),
                joinedCircles = listOf()
            )
            val res = runBlocking {
                AuthorService().postAuthor(request)
            }

            //検証
            res.values.first().jsonPrimitive.content shouldBeEqual "test1"
            val id = res.keys.first().toString()

            //DB検証
            val result = execRawSelectQuery("SELECT * from djla.author") // わざと全件取得し、1個だけできていることを確認する
            result shouldHaveSize 1
            assertAuthor(Integer.parseInt(id), "test1", "", result[0])
        }

        test("正常系_すべて").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            //実行
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "hello",
                authorAlias = listOf(1),
                joinedCircles = listOf(1)
            )
            val res = runBlocking {
                AuthorService().postAuthor(request)
            }

            //検証
            res.values.first().jsonPrimitive.content shouldBeEqual "test1"
            val id = res.keys.first().toString()
            id shouldNotBeEqual 1 //既存のものではないことを確認

            //DB検証
            //Authorテーブル
            val resultAuthor = execRawSelectQuery("SELECT * from djla.author WHERE id = $id")
            resultAuthor shouldHaveSize 1
            assertAuthor(Integer.parseInt(id), "test1", "hello", resultAuthor[0])
            //AuthorAliasテーブル
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias") // わざと全件取得し、1個だけできていることを確認する
            resultAlias shouldHaveSize 1
            assertAuthorAlias(Integer.parseInt(id), 1, resultAlias[0])
            //MAuthorCircleテーブル
            val resultMAuthorCircle = execRawSelectQuery("SELECT * from djla.m_author_circle") // わざと全件取得し、1個だけできていることを確認する
            resultMAuthorCircle shouldHaveSize 1
            assertMAuthorCircle(Integer.parseInt(id), 1, resultMAuthorCircle[0])
        }

        test("異常系_AuthorServiceLogicでエラー").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every {insertAuthor(any(), any(), any(), any())} throws Exception()
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
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "",
                authorAlias = listOf(),
                joinedCircles = listOf()
            )
            shouldThrowAny{
                runBlocking {
                    AuthorService().postAuthor(request)
                }
            }

            //実行確認
            verify(exactly = 1) {
                authorServiceLogicMock.insertAuthor(any(), any(), any(), any())
            }
            confirmVerified(authorServiceLogicMock)

            //ロールバック検知
            isRollBack.shouldBeTrue()
        }

        test("異常系_AuthorResponseでエラー").config(enabledOrReasonIf = disableDBAccess){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            transaction {
                TransactionManager.current().exec("INSERT INTO djla.author values (1, 'testAuthor', 'memoAuthor1', '$strLocalDateTime', '$strLocalDateTime')")
                TransactionManager.current().exec("INSERT INTO djla.circle values (1, 'testCircle', 'memoCircle1', '$strLocalDateTime', '$strLocalDateTime')")
            }

            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any())} throws Exception()
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
            val request = AuthorRequestEntity(
                name = "test1",
                memo = "hello",
                authorAlias = listOf(1),
                joinedCircles = listOf(1)
            )

            shouldThrowAny{
                runBlocking {
                    AuthorService().postAuthor(request)
                }
            }

            //実行確認
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)

            //ロールバック検知
            isRollBack.shouldBeTrue()

            //DB検証
            //Authorテーブル
            val resultAuthor = execRawSelectQuery("SELECT * from djla.author")
            resultAuthor shouldHaveSize 1
            //AuthorAliasテーブル
            val resultAlias = execRawSelectQuery("SELECT * from djla.author_alias")
            resultAlias.shouldBeEmpty()
            //MAuthorCircleテーブル
            val resultMAuthorCircle = execRawSelectQuery("SELECT * from djla.m_author_circle")
            resultMAuthorCircle.shouldBeEmpty()
        }
    }
    /*
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


private fun assertAuthor(id: Int, name: String, memo: String, actual: Map<String, Any?>){
    Integer.parseInt(actual["id"].toString()) shouldBe id
    actual["name"] shouldBe name
    actual["memo"] shouldBe memo
    //substring(0, 23)はナノ秒切り捨て用
    LocalDateTime.parse(actual["created_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    LocalDateTime.parse(actual["updated_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

private fun assertAuthorAlias(authorId1: Int, authorId2: Int, actual: Map<String, Any?>){
    Integer.parseInt(actual["author_id_1"].toString()) shouldBe authorId1
    Integer.parseInt(actual["author_id_2"].toString()) shouldBe authorId2
    //substring(0, 23)はナノ秒切り捨て用
    LocalDateTime.parse(actual["created_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    LocalDateTime.parse(actual["updated_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

private fun assertMAuthorCircle(authorId: Int, circleId: Int, actual: Map<String, Any?>){
    Integer.parseInt(actual["author_id"].toString()) shouldBe authorId
    Integer.parseInt(actual["circle_id"].toString()) shouldBe circleId
    //substring(0, 23)はナノ秒切り捨て用
    LocalDateTime.parse(actual["created_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    LocalDateTime.parse(actual["updated_at"].toString().replace(" ", "T")) shouldBeBefore Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}