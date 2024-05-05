package net.hennabatch.dojinapi.views

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotContain
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author

class AuthorResponseTest : FunSpec({
    context("makeAuthorListFetched") {
        test("データあり") {
            //準備
            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null),
                Author(2, "test2", null, listOf(), null, null),
                Author(3, "test3", null, listOf(), null, null)
            )

            //実行
            val res = AuthorResponse().makeAuthorListFetched(authors)

            //検証
            res shouldHaveSize 3
            res.shouldContain("1", JsonPrimitive("test1"))
            res.shouldContain("2", JsonPrimitive("test2"))
            res.shouldContain("3", JsonPrimitive("test3"))
        }
        test("データあり_一部nameカラ") {
            //準備
            val authors = listOf(
                Author(1, "test1", null, listOf(), null, null),
                Author(2, null, null, listOf(), null, null),
                Author(3, "test3", null, listOf(), null, null)
            )

            //実行
            val res = AuthorResponse().makeAuthorListFetched(authors)

            //検証
            res shouldHaveSize 2
            res.shouldContain("1", JsonPrimitive("test1"))
            res.shouldNotContain("2", JsonPrimitive("test2"))
            res.shouldContain("3", JsonPrimitive("test3"))
        }

        test("データなし"){
            //準備
            val authors: List<Author> = listOf()

            //実行
            val res = AuthorResponse().makeAuthorListFetched(authors)

            //検証
            res.shouldBeEmpty()
        }
    }
})