package net.hennabatch.dojinapi.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.Module
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.logic.AuthorControllerLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.koin.dsl.module

class AuthorControllerTest: FunSpec({

    afterTest {
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
})