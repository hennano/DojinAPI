package net.hennabatch.dojinapi.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.Module
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.db.entity.AuthorEntity
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.table.AuthorTable
import net.hennabatch.dojinapi.logic.AuthorServiceLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.dsl.module
import java.time.format.DateTimeFormatter
import net.hennabatch.dojinapi.configRouting
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.service.AuthorService
import org.jetbrains.exposed.sql.Except

class AuthorControllerTest: FunSpec({

    beforeEach {
        mockkStatic(Application::configRouting.declaringKotlinFile)
        every { (Application::configRouting)(any()) } just Runs
    }

    afterEach {
        unmockkAll()
    }

    context("getAuthorList"){
        test("正常系"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { getAuthors() } returns JsonObject(mapOf("testKey" to JsonPrimitive("testValue")))
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"testKey\":\"testValue\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.getAuthors()
            }
        }

        test("異常系_AuthorServiceでエラー"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { getAuthors() } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.get("/author"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.getAuthors()
            }
        }
    }

    context("createAuthor"){
        test("正常系"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { postAuthor(any()) } returns JsonObject(mapOf("testKey" to JsonPrimitive("testValue")))
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"testKey\":\"testValue\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.postAuthor(any())
            }
        }

        test("異常系_AuthorServiceでエラー"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { postAuthor(any()) } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.postAuthor(any())
            }
        }
    }

    context("getAuthor"){
        test("正常系"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { getAuthor(any()) } returns JsonObject(mapOf("testKey" to JsonPrimitive("testValue")))
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"testKey\":\"testValue\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.getAuthor(1)
            }
        }

        test("異常系_AuthorServiceでエラー"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { getAuthor(any()) } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.get("/author/1"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.getAuthor(1)
            }
        }
    }

    context("updateAuthor"){
        test("正常系"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { putAuthor(any(), any()) } returns JsonObject(mapOf("testKey" to JsonPrimitive("testValue")))
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"testKey\":\"testValue\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.putAuthor(1, any())
            }
        }

        test("異常系_AuthorServiceでエラー"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { putAuthor(any(), any()) } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.put("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.putAuthor(1, any())
            }
        }
    }

    context("deleteAuthor"){
        test("正常系"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { deleteAuthor(any()) } returns JsonObject(mapOf("testKey" to JsonPrimitive("testValue")))
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.delete("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"testKey\":\"testValue\"}"
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.deleteAuthor(1)
            }
        }

        test("異常系_AuthorServiceでエラー"){
            //準備
            val authorServiceMock = mockk<AuthorService>{
                coEvery { deleteAuthor(any()) } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every {Module.koinModules()} returns module {
                single<AuthorService>{authorServiceMock}
                single<CommonDb>{spyk<CommonDb>()}
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    authorController()
                }

                // 実行(リクエスト)
                client.delete("/author/1"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"testName\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                }
            }
            //検証
            coVerify(exactly = 1) {
                authorServiceMock.deleteAuthor(1)
            }
        }
    }
})
