package net.hennabatch.dojinapi.logic

import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.Circle
import net.hennabatch.dojinapi.db.repository.AuthorAliasRepository
import net.hennabatch.dojinapi.db.repository.AuthorRepository
import net.hennabatch.dojinapi.db.repository.MAuthorCircleRepository

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
            mockkObject(objects = arrayOf(AuthorAliasRepository, AuthorRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any()) } returns Author(1,"test", "memo", listOf(), listOf(), null, null)
            every { AuthorAliasRepository.matrixInsert(any(), any()) } returns Unit
            every { AuthorAliasRepository.deletesRelation(any(), any())} returns 0

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(1)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.matrixInsert(1, listOf())
            }
            verify(exactly = 1) {
                AuthorAliasRepository.deletesRelation(1, listOf())
            }
        }

        test("データなし_既存を削除"){
            //準備
            val aliasAuthor = Author(2, "test2", "memomemo2", listOf(), listOf(), null, null)
            val author = Author(1, "test1", "memomemo1", listOf(aliasAuthor), listOf(), null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository, AuthorRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(1) } returns author
            every { AuthorAliasRepository.matrixInsert(any(), any()) } returns Unit
            every { AuthorAliasRepository.deletesRelation(any(), any())} returns 1

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(1)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.matrixInsert(1, listOf())
            }
            verify(exactly = 1) {
                AuthorAliasRepository.deletesRelation(1, listOf(2))
            }
        }

        test("データ1つ"){
            //準備
            mockkObject(objects = arrayOf(AuthorAliasRepository, AuthorRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any()) } returns Author(1,"test", "memo", listOf(), listOf(), null, null)
            every { AuthorAliasRepository.matrixInsert(any(), any()) } returns Unit
            every { AuthorAliasRepository.deletesRelation(any(), any())} returns 0

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(1)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.matrixInsert(1, listOf(2))
            }
            verify(exactly = 1) {
                AuthorAliasRepository.deletesRelation(1, listOf())
            }
        }

        test("データ1つ_すでにあるデータと重複"){
            //準備
            val aliasAuthor = Author(2, "test2", "memomemo2", listOf(), listOf(), null, null)
            val author = Author(1, "test1", "memomemo1", listOf(aliasAuthor), listOf(), null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository, AuthorRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any()) } returns author
            every { AuthorAliasRepository.matrixInsert(any(), any()) } returns Unit
            every { AuthorAliasRepository.deletesRelation(any(), any())} returns 0

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(1)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.matrixInsert(1, listOf())
            }
            verify(exactly = 1) {
                AuthorAliasRepository.deletesRelation(1, listOf())
            }
            confirmVerified(AuthorAliasRepository)
        }

        test("データ複数"){
            //準備
            val author2 = Author(2, "test2", "memomemo2", listOf(), listOf(), null, null)
            val author4 = Author(4, "test4", "memomemo4", listOf(), listOf(), null, null)
            val author1 = Author(1, "test1", "memomemo1", listOf(author2, author4), listOf(), null, null)
            mockkObject(objects = arrayOf(AuthorAliasRepository, AuthorRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any()) } returns author1
            every { AuthorAliasRepository.matrixInsert(any(), any()) } returns Unit
            every { AuthorAliasRepository.deletesRelation(any(), any())} returns 1

            //実行
            AuthorServiceLogic().updateAuthorAliases(1, listOf(2, 3))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(1)
            }
            verify(exactly = 1) {
                AuthorAliasRepository.matrixInsert(1, listOf(3))
            }
            verify(exactly = 1) {
                AuthorAliasRepository.deletesRelation(1, listOf(4))
            }
            confirmVerified(AuthorAliasRepository)
        }
    }

    context("updateJoinedCircles"){
        test("データなし"){
            //準備
            mockkObject(objects = arrayOf(AuthorRepository, MAuthorCircleRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any(), any()) } returns Author(1,"test", "memo", listOf(), listOf(), null, null)
            every { MAuthorCircleRepository.insert(any(), any()) } returns Unit
            every { MAuthorCircleRepository.delete(any(), any())} returns true

            //実行
            AuthorServiceLogic().updateJoinedCircles(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.insert(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.delete(any(), any())
            }
        }

        test("データなし_既存を削除"){
            mockkObject(objects = arrayOf(AuthorRepository, MAuthorCircleRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any(), any()) } returns Author(1,"test", "memo", listOf(), listOf(Circle(1, "test", "test", listOf(), listOf(), null, null)), null, null)
            every { MAuthorCircleRepository.insert(any(), any()) } returns Unit
            every { MAuthorCircleRepository.delete(any(), any())} returns true

            //実行
            AuthorServiceLogic().updateJoinedCircles(1, listOf())

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.insert(any(), any())
            }
            verify(exactly = 1) {
                MAuthorCircleRepository.delete(any(), any())
            }
        }

        test("データ1つ"){
            //準備
            mockkObject(objects = arrayOf(AuthorRepository, MAuthorCircleRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any(), any()) } returns Author(1,"test", "memo", listOf(), listOf(), null, null)
            every { MAuthorCircleRepository.insert(any(), any()) } returns Unit
            every { MAuthorCircleRepository.delete(any(), any())} returns true

            //実行
            AuthorServiceLogic().updateJoinedCircles(1, listOf(1))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(any(), any())
            }
            verify(exactly = 1) {
                MAuthorCircleRepository.insert(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.delete(any(), any())
            }
        }

        test("データ1つ_すでにあるデータと重複"){
            //準備
            mockkObject(objects = arrayOf(AuthorRepository, MAuthorCircleRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any(), any()) } returns Author(1,"test", "memo", listOf(), listOf(Circle(1, "test", "test", listOf(), listOf(), null, null)), null, null)
            every { MAuthorCircleRepository.insert(any(), any()) } returns Unit
            every { MAuthorCircleRepository.delete(any(), any())} returns true

            //実行
            AuthorServiceLogic().updateJoinedCircles(1, listOf(1))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.insert(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.delete(any(), any())
            }
        }

        test("データ複数"){
            //準備
            mockkObject(objects = arrayOf(AuthorRepository, MAuthorCircleRepository), recordPrivateCalls = true)
            every { AuthorRepository.select(any(), any()) } returns Author(1,"test1", "memo1", listOf(), listOf(
                Circle(1, "test1", "test1", listOf(), listOf(), null, null),
                Circle(2, "test2", "test2", listOf(), listOf(), null, null),
                Circle(4, "test4", "test4", listOf(), listOf(), null, null)
            ), null, null)
            every { MAuthorCircleRepository.insert(any(), any()) } returns Unit
            every { MAuthorCircleRepository.delete(any(), any())} returns true

            //実行
            AuthorServiceLogic().updateJoinedCircles(1, listOf(2, 3))

            //検証
            verify(exactly = 1) {
                AuthorRepository.select(any(), any())
            }
            verify(exactly = 0) {
                MAuthorCircleRepository.insert(1, 2)
            }
            verify(exactly = 1) {
                MAuthorCircleRepository.insert(1, 3)
            }
            verify(exactly = 1) {
                MAuthorCircleRepository.delete(1, 4)
            }
        }
    }

    context("deleteAuthor"){

    }
})