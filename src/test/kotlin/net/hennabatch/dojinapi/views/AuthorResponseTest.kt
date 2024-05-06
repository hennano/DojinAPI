package net.hennabatch.dojinapi.views

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotContain
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.db.model.AuthorAlias
import net.hennabatch.dojinapi.db.model.Circle
import java.time.format.DateTimeFormatter

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

    context("makeAuthorFetched"){
        test("データあり_最小"){
            //準備
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            val author = Author(1, "author1", "", listOf(), localDateTime, localDateTime)

            //実行
            val res = AuthorResponse().makeAuthorFetched(author, listOf())

            //検証
            res.shouldContain("id", JsonPrimitive(1))
            res.shouldContain("name", JsonPrimitive("author1"))
            res.shouldContain("memo", JsonPrimitive(""))
            res.shouldContain("joined_circles", JsonObject(mapOf()))
            res.shouldContain("author_alias", JsonObject(mapOf()))
            res.shouldContain("created_at", JsonPrimitive(strLocalDateTime))
            res.shouldContain("updated_at", JsonPrimitive(strLocalDateTime))
        }

        test("データあり_すべて"){
            val localDateTime = LocalDateTime(2024, 5, 2, 16, 20, 30)
            val strLocalDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
            val circle = Circle(1, "circle1", "", listOf(), null, null)
            val author1 = Author(1, "author1", "memomemo", listOf(circle), localDateTime, localDateTime)
            val author2 = Author(2, "author2", "", listOf(), null, null)
            val authorAlias = AuthorAlias(1, author1, author2, null, null)

            //実行
            val res = AuthorResponse().makeAuthorFetched(author1, listOf(authorAlias))

            //検証
            res.shouldContain("id", JsonPrimitive(1))
            res.shouldContain("name", JsonPrimitive("author1"))
            res.shouldContain("memo", JsonPrimitive("memomemo"))
            res.shouldContain("joined_circles", JsonObject(mapOf("1" to JsonPrimitive("circle1"))))
            res.shouldContain("author_alias", JsonObject(mapOf("2" to JsonPrimitive("author2"))))
            res.shouldContain("created_at", JsonPrimitive(strLocalDateTime))
            res.shouldContain("updated_at", JsonPrimitive(strLocalDateTime))
        }
    }
})