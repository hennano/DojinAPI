package net.hennabatch.dojinapi.logic

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.db.repository.AuthorAliasRepository

class AuthorControllerLogicTest : FunSpec({

    afterEach {
        unmockkAll()
    }

    context("fetchAllAuthors"){
        //とくになし
    }

    context("insertAuthor"){
        //とくになし
    }

    context("fetchAuthorDetail"){
        //とくになし
    }

    context("insertAuthorAliases"){
        test("データ1つ"){
            //準備
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf()
            every { AuthorAliasRepository.insert(any(), any()) } returns 1

            //実行
            AuthorControllerLogic().insertAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.insert(1, 2)
            }
        }

        test("データ1つ_重複"){
            //準備
            val author1 = Author(1, "test1", "memomemo1", listOf(), null, null)
            val author2 = Author(2, "test2", "memomemo2", listOf(), null, null)
            val expected = AuthorAlias(1, author1, author2, null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf(
                expected
            )
            every { AuthorAliasRepository.insert(any(), any()) } returns 1

            //実行
            AuthorControllerLogic().insertAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.insert(any(), any())
            }
            confirmVerified(AuthorAliasRepository)
        }

        test("データ複数"){
            //準備
            val author1 = Author(1, "test1", "memomemo1", listOf(), null, null)
            val author2 = Author(2, "test2", "memomemo2", listOf(), null, null)
            val expected = AuthorAlias(1, author1, author2, null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf(
                expected
            )
            every { AuthorAliasRepository.insert(any(), any()) } returns 1

            //実行
            AuthorControllerLogic().insertAuthorAliases(1, listOf(2, 3))

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.insert(1, 2)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.insert(1, 3)
            }
        }
    }
})