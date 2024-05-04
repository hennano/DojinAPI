package net.hennabatch.dojinapi.views

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotContain
import io.ktor.http.*
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Author
import net.hennabatch.dojinapi.views.entity.ResponseBody

class AuthorResponseTest : FunSpec({
    context("正常系") {
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
            res.statusCode shouldBeEqual HttpStatusCode.OK
            res.headers.shouldBeEmpty()
            when(val body = res.responseBody){
                is ResponseBody.JsonBody ->{
                    body.value shouldHaveSize 3
                    body.value.shouldContain("1", JsonPrimitive("test1"))
                    body.value.shouldContain("2", JsonPrimitive("test2"))
                    body.value.shouldContain("3", JsonPrimitive("test3"))
                }
                else ->{
                    fail("ResponseBody.MapBody以外が指定されている")
                }
            }
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
            res.statusCode shouldBeEqual HttpStatusCode.OK
            res.headers.shouldBeEmpty()
            when(val body = res.responseBody){
                is ResponseBody.JsonBody ->{
                    body.value shouldHaveSize 2
                    body.value.shouldContain("1", JsonPrimitive("test1"))
                    body.value.shouldNotContain("2", JsonPrimitive("test2"))
                    body.value.shouldContain("3", JsonPrimitive("test3"))
                }
                else ->{
                    fail("ResponseBody.MapBody以外が指定されている")
                }
            }
        }

        test("データなし"){
            //準備
            val authors: List<Author> = listOf()

            //実行
            val res = AuthorResponse().makeAuthorListFetched(authors)

            //検証
            res.statusCode shouldBeEqual HttpStatusCode.OK
            res.headers.shouldBeEmpty()
            when(val body = res.responseBody){
                is ResponseBody.JsonBody ->{
                    body.value.shouldBeEmpty()
                }
                else ->{
                    fail("ResponseBody.MapBody以外が指定されている")
                }
            }
        }
    }
})