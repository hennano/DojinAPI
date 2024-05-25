package net.hennabatch.dojinapi.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.Module
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.entity.AuthorEntity
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.table.AuthorTable
import net.hennabatch.dojinapi.logic.AuthorControllerLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.dsl.module
import java.time.format.DateTimeFormatter

class AuthorControllerTest: FunSpec({

    beforeEach{
        //DBは使わないのでモックして無効化する
        mockkObject(objects = arrayOf(DatabaseSingleton), recordPrivateCalls = true)
        every { DatabaseSingleton.init(any())} just Runs
    }

    afterEach {
        unmockkAll()
    }

    context("getAuthorList"){
        test("正常系"){
            //準備
            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null),
                Author(2, "test2", null, listOf(), null, null),
                Author(3, "test3", null, listOf(), null, null)
            )
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAllAuthors()} returns authors
            }
            val res = JsonObject(mapOf(
                    "1" to JsonPrimitive("test1"),
                    "2" to JsonPrimitive("test2"),
                    "3" to JsonPrimitive("test3")
                )
            )
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any()) } returns res

            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\",\"2\":\"test2\",\"3\":\"test3\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(any())
            }
            confirmVerified(authorResponseMock)
        }

        test("正常系_データなし"){
            //準備
            val authors = listOf<Author>()
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAllAuthors()} returns authors
            }
            val res = JsonObject(mapOf())
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any()) } returns res

            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_authorControllerLogicでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAllAuthors()} throws Exception()
            }
            val authorResponseMock = mockk<AuthorResponse>{}
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorListFetched(any())
            }
            confirmVerified(authorResponseMock)
        }

        test("AuthorResponseでエラー"){
            //準備
            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null),
                Author(2, "test2", null, listOf(), null, null),
                Author(3, "test3", null, listOf(), null, null)
            )
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAllAuthors()} returns authors
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorListFetched(any()) } throws Exception()

            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(any())
            }
            confirmVerified(authorResponseMock)
        }
    }

    context("createAuthor"){
        test("正常系_最小"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.insertAuthor("test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }

        test("正常系_すべて"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"memomemo1\",\"author_alias\": [1, 2]}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.insertAuthor("test1", "memomemo1", listOf(1, 2))
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_リクエストボディなし"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    //setBody("{\"name\": \"test1\",\"memo\": \"memomemo1\",\"author_alias\": [1, 2]}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_Content-Typeがapplication/json以外"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("{\"name\": \"test1\",\"memo\": \"memomemo1\",\"author_alias\": [1, 2]}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_nameが項目ごとなし"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"memo\": \"memomemo1\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_memoが項目ごとなし"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"author_alias\": [1, 2]}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_author_aliasが項目ごとなし"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"memomemo1\"}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_author_aliasに存在しないAuthorIdが指定されている"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} throws  EntityNotFoundException(EntityID(1, AuthorTable), AuthorEntity)
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": [1]}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_nameが空文字"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"\",\"memo\": \"memomemo1\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_insertAuthorでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} throws Exception()
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.insertAuthor(any(), any(), any())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_makeAuthorCreatedでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { insertAuthor(any(), any(), any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorCreated(any(), any()) } throws Exception()
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.insertAuthor("test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorCreated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }
    }

    context("getAuthor"){
        test("正常系_最小"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            val author = Author(1, "test1", "", listOf(), localDateTime, localDateTime)
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAuthor(any())} returns Pair(author, listOf())
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } returns JsonObject(mapOf(
                    "id" to JsonPrimitive("1"),
                    "name" to JsonPrimitive("test1"),
                    "memo" to JsonPrimitive(""),
                    "joined_circles" to JsonObject(mapOf()),
                    "author_alias" to JsonObject(mapOf()),
                    "created_at" to JsonPrimitive(strLocalDateTime),
                    "updated_at" to JsonPrimitive(strLocalDateTime)
                ))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"id\":\"1\",\"name\":\"test1\",\"memo\":\"\",\"joined_circles\":{},\"author_alias\":{},\"created_at\":\"2024-05-02T16:20:30\",\"updated_at\":\"2024-05-02T16:20:30\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorFetched(author, listOf())
            }
            confirmVerified(authorResponseMock)
        }

        test("正常系_すべて"){
            logger.info("getAuthor::正常系_最小と、makeAuthorFetched::データあり_すべてで実施")
        }

        test("異常系_author_idが項目ごと存在しない"){
            logger.info("getAuthorList::正常系で実施")
        }

        test("異常系_author_idが数値でない"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val author = Author(1, "test1", "", listOf(), localDateTime, localDateTime)
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAuthor(any())} returns Pair(author, listOf())
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } returns JsonObject(mapOf())
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author/a"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
            //検証
            coVerify(exactly = 0) {
                authorControllerLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorFetched(author, listOf())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_author_idが存在しない値"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAuthor(any())} throws EntityNotFoundException(EntityID(1, AuthorTable), AuthorEntity)
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } returns JsonObject(mapOf())
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorFetched(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_fetchAuthorでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAuthor(any())} throws Exception()
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } returns JsonObject(mapOf())
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorFetched(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_makeAuthorFetchedでエラー"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val author = Author(1, "test1", "", listOf(), localDateTime, localDateTime)
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { fetchAuthor(any())} returns Pair(author, listOf())
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorFetched(any(), any()) } throws Exception()
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.fetchAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorFetched(author, listOf())
            }
            confirmVerified(authorResponseMock)
        }
    }

    context("updateAuthor"){
        test("正常系_最小"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { updateAuthor(any(),any(),any(),any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.updateAuthor(1, "test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorUpdated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }

        test("正常系_すべて"){
            logger.info("updateAuthor::正常系_最小と、makeAuthorUpdated::データあり_すべてで実施")
        }

        test("異常系_author_idが項目ごと存在しない"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
        }

        test("異常系_author_idが数値でない"){
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/a"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_author_idが存在しない値"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { updateAuthor(any(),any(),any(),any())} throws EntityNotFoundException(EntityID(1, AuthorTable), AuthorEntity)
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } returns JsonObject(mapOf())
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
        }

        test("異常系_リクエストボディなし"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    //setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_Content-Typeがapplication/json以外"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_nameが項目ごとなし"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("{\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_memoが項目ごとなし"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("{\"name\": \"test1\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_author_aliasが項目ごとなし"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("{\"name\": \"test1\",\"memo\": \"\"}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_updateAuthorでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { updateAuthor(any(),any(),any(),any())} throws Exception()
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.updateAuthor(1, "test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorUpdated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_author_aliasに存在しないAuthorIdが指定されている"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { updateAuthor(any(),any(),any(),any())} throws EntityNotFoundException(EntityID(1, AuthorTable), AuthorEntity)
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } returns JsonObject(mapOf("1" to JsonPrimitive("test1")))
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.updateAuthor(1, "test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 0) {
                authorResponseMock.makeAuthorUpdated(any(), any())
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_nameが空文字"){
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_makeAuthorUpdatedでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { updateAuthor(any(),any(),any(),any())} returns 1
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorUpdated(any(), any()) } throws Exception()
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.updateAuthor(1, "test1", "", listOf())
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorUpdated(1, "test1")
            }
            confirmVerified(authorResponseMock)
        }
    }

    context("deleteAuthor"){
        test("正常系"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { deleteAuthor(any())} returns true
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorDeleted(any()) } returns JsonObject(mapOf())
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.deleteAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorDeleted(true)
            }
            confirmVerified(authorResponseMock)
        }

        test("異常系_author_idが項目ごと存在しない"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
        }

        test("異常系_author_idが数値でない"){
            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/a"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.BadRequest
                    bodyAsText() shouldBeEqual "{\"error\":\"BadRequest\"}"
                }
            }
        }

        test("異常系_author_idが存在しない値"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { deleteAuthor(any())} throws EntityNotFoundException(EntityID(1, AuthorTable), AuthorEntity)
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.NotFound
                    bodyAsText() shouldBeEqual "{\"error\":\"NotFound\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.deleteAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
        }

        test("異常系_deleteAuthorでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { deleteAuthor(any())} throws Exception()
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.deleteAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
        }

        test("異常系_makeAuthorDeletedでエラー"){
            //準備
            val authorControllerLogicMock = mockk<AuthorControllerLogic>{
                coEvery { deleteAuthor(any())} returns true
            }
            val authorResponseMock = mockk<AuthorResponse>{
                every { makeAuthorDeleted(any()) } throws Exception()
            }

            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorControllerLogic>{authorControllerLogicMock}
                single<AuthorResponse>{authorResponseMock}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 実行(リクエスト)
                client.delete("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"ServerError\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorControllerLogicMock.deleteAuthor(1)
            }
            confirmVerified(authorControllerLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorDeleted(true)
            }
            confirmVerified(authorResponseMock)
        }
    }
})