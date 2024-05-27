package net.hennabatch.dojinapi.views

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotContain
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.model.Circle

class CircleResponseTest : FunSpec({
    context("makeACircleListFetched"){
        test("正常系"){
            //準備
            val circles = listOf(
                Circle(1, "test1", null, listOf(), null, null),
                Circle(2, "test2", null, listOf(), null, null),
                Circle(3, "test3", null, listOf(), null, null)
            )

            //実行
            val res = CircleResponse().makeCircleListFetched(circles)

            //検証
            res shouldHaveSize 3
            res.shouldContain("1", JsonPrimitive("test1"))
            res.shouldContain("2", JsonPrimitive("test2"))
            res.shouldContain("3", JsonPrimitive("test3"))
        }

        test("正常系_一部nameカラ"){
            //準備
            val circles = listOf(
                Circle(1, "test1", null, listOf(), null, null),
                Circle(2, null, null, listOf(), null, null),
                Circle(3, "test3", null, listOf(), null, null)
            )

            //実行
            val res = CircleResponse().makeCircleListFetched(circles)

            //検証
            res shouldHaveSize 2
            res.shouldContain("1", JsonPrimitive("test1"))
            res.shouldNotContain("2", JsonPrimitive("test2"))
            res.shouldContain("3", JsonPrimitive("test3"))
        }

        test("正常系_データなし"){
            //準備
            val circles = listOf<Circle>()

            //準備
            val res = CircleResponse().makeCircleListFetched(circles)

            //検証
            res.shouldBeEmpty()
        }
    }
})