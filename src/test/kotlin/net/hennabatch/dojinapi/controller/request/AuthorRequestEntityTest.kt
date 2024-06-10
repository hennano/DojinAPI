package net.hennabatch.dojinapi.controller.request

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual

class AuthorRequestEntityTest: FunSpec({

    context("validation"){
        test("正常系"){
            //準備
            val entity = AuthorRequestEntity(
                name = "なまえ",
                memo = "testmemo",
                authorAlias = listOf(1, 2)
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeTrue()
            res.reason shouldBeEqual ""
        }

        test("正常系_最小"){
            //準備
            val entity = AuthorRequestEntity(
                name = "なまえ",
                memo = "",
                authorAlias = listOf()
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeTrue()
            res.reason shouldBeEqual ""
        }

        test("異常系_nameが存在しない"){
            //準備
            val entity = AuthorRequestEntity(
                name = null,
                memo = "testmemo",
                authorAlias = listOf(1, 2)
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeFalse()
            res.reason shouldBeEqual "nameが空"
        }

        test("異常系_nameが空"){
            //準備
            val entity = AuthorRequestEntity(
                name = "",
                memo = "testmemo",
                authorAlias = listOf(1, 2)
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeFalse()
            res.reason shouldBeEqual "nameが空"
        }

        test("異常系_memoが存在しない"){
            //準備
            val entity = AuthorRequestEntity(
                name = "なまえ",
                memo = null,
                authorAlias = listOf(1, 2)
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeFalse()
            res.reason shouldBeEqual "memoがない"
        }
        test("異常系_author_aliasが存在しない"){
            //準備
            val entity = AuthorRequestEntity(
                name = "なまえ",
                memo = "testmemo",
                authorAlias = null
            )

            //実行
            val res = entity.validation()

            //検証
            res.result.shouldBeFalse()
            res.reason shouldBeEqual "authorAliasがない"
        }
    }
})