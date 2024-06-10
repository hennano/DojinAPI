package net.hennabatch.dojinapi.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import net.hennabatch.dojinapi.db.CommonDb
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.logic.AuthorServiceLogic
import net.hennabatch.dojinapi.views.AuthorResponse
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class AuthorServiceTest: FunSpec({

    afterEach {
        unmockkAll()
    }

    context("getAuthor"){
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
    }
})