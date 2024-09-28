package net.hennabatch.dojinapi.e2e

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import net.hennabatch.dojinapi.common.utils.logger

class `01_Author`: FunSpec({
    context("Author操作"){
        test("取得から作成、削除まで"){
            //認証トークン取得

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                // 新規登録1
                client.post("/author"){
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"test1\",\"memo\": \"\",\"author_alias\": []}")
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\"}"
                }

                //新規登録2
            }
        }
    }
})