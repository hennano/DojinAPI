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
    }

    context("fetchAuthorDetail"){
        //とくになし
    }

    context("updateAuthorAliases"){
        test("データなし"){
            //準備
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf()
            every { AuthorAliasRepository.insert(any(), any()) } returns 1
            every { AuthorAliasRepository.delete(any())} returns true

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.insert(any(), any())
            }
            verify(exactly = 0) {
                AuthorAliasRepository.delete(any())
            }
        }

        test("データなし_既存を削除"){
            //準備
            val author1 = Author(1, "test1", "memomemo1", listOf(), null, null)
            val author2 = Author(2, "test2", "memomemo2", listOf(), null, null)
            val expected = AuthorAlias(1, author1, author2, null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf(
                expected
            )
            every { AuthorAliasRepository.insert(any(), any()) } returns 1
            every { AuthorAliasRepository.delete(any())} returns true

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.insert(any(), any())
            }
            verify(exactly = 1) {
                AuthorAliasRepository.delete(1)
            }
        }

        test("データ1つ"){
            //準備
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf()
            every { AuthorAliasRepository.insert(any(), any()) } returns 1
            every { AuthorAliasRepository.delete(any())} returns true

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.insert(1, 2)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.delete(1)
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
            every { AuthorAliasRepository.delete(any())} returns true

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorAliasRepository.selectsByAuthorId(1, 0)
            }
            verify(exactly = 0) {
                AuthorAliasRepository.insert(any(), any())
            }
            verify(exactly = 0) {
                AuthorAliasRepository.delete(any())
            }
            confirmVerified(AuthorAliasRepository)
        }

        test("データ複数"){
            //準備
            val author1 = Author(1, "test1", "memomemo1", listOf(), null, null)
            val author2 = Author(2, "test2", "memomemo2", listOf(), null, null)
            val author4 = Author(4, "test2", "memomemo2", listOf(), null, null)
            val expected1 = AuthorAlias(1, author1, author2, null, null)
            val expected2 = AuthorAlias(2, author1, author4, null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository), recordPrivateCalls = true)
            every { AuthorAliasRepository.selectsByAuthorId(any(), any()) } returns listOf(
                expected1,
                expected2
            )
            every { AuthorAliasRepository.insert(any(), any()) } returns 1
            every { AuthorAliasRepository.delete(any())} returns true

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2, 3))

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
            verify(exactly = 1) {
                AuthorAliasRepository.delete(2)
            }
            confirmVerified(AuthorAliasRepository)
        }
    }

    context("deleteAuthor"){

    }
})