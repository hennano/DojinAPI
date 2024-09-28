package net.hennabatch.dojinapi.service

import aws.smithy.kotlin.runtime.io.closeIfCloseable
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.controller.request.AuthorRequestEntity
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.db.HikariCpDb
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.logic.AuthorServiceLogic
import net.hennabatch.dojinapi.testutils.TestFlags.disableDBAccess
import net.hennabatch.dojinapi.views.AuthorResponse
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class AuthorServiceTest: FunSpec({

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"

    afterEach {
        stopKoin()
        unmockkAll()
    }

    context("getAuthors"){
        test("正常系"){
            //準備
            val db = spyk<CommonDb>()

            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null)
            )
            val authorServiceLogicMock = mockk<AuthorServiceLogic>{
                every { fetchAllAuthors()} returns authors
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
            val res = runBlocking {
                 AuthorService().getAuthors()
            }

            //検証
            res shouldBeEqual resJson

            verify(exactly = 1) {
                authorServiceLogicMock.fetchAllAuthors()
            }
            confirmVerified(authorServiceLogicMock)
            verify(exactly = 1) {
                authorResponseMock.makeAuthorListFetched(authors)
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
})