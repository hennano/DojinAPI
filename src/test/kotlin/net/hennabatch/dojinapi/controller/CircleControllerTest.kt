package net.hennabatch.dojinapi.controller

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.hennabatch.dojinapi.db.DatabaseSingleton
import net.hennabatch.dojinapi.db.model.Circle
import net.hennabatch.dojinapi.logic.CircleControllerLogic
import net.hennabatch.dojinapi.views.CircleResponse
import net.hennabatch.dojinapi.Module
import net.hennabatch.dojinapi.common.utils.logger
import net.hennabatch.dojinapi.configRouting
import org.koin.dsl.module

class CircleControllerTest: FunSpec({

    beforeEach {
        //DBは使わないのでモックして無効化する
        mockkObject(objects = arrayOf(DatabaseSingleton), recordPrivateCalls = true)
        every { DatabaseSingleton.init(any()) } just Runs
        //デフォルトのルートを無効化する
        mockkStatic(Application::configRouting.declaringKotlinFile)
        every { (Application::configRouting)(any()) } just Runs
    }

    afterEach {
        unmockkAll()
    }

    context("getCircleList"){
        test("正常系"){
            //準備
            val circles = listOf(
                Circle(1, "test1", null, listOf(), null, null),
                Circle(2, "test2", null, listOf(), null, null),
                Circle(3, "test3", null, listOf(), null, null)
            )
            val circleControllerLogicMock = mockk<CircleControllerLogic>{
                coEvery { fetchCircles() } returns circles
            }
            val res = JsonObject(mapOf(
                "1" to JsonPrimitive("test1"),
                "2" to JsonPrimitive("test2"),
                "3" to JsonPrimitive("test3"),
            ))
            val circleResponseMock = mockk<CircleResponse>{
                every { makeCircleListFetched(any()) } returns res
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every { Module.koinModules() } returns module {
                single<CircleControllerLogic> { circleControllerLogicMock }
                single<CircleResponse> { circleResponseMock }
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    circleController()
                }
                // 実行(リクエスト)
                client.get("/circle"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{\"1\":\"test1\",\"2\":\"test2\",\"3\":\"test3\"}"
                }
                //検証
                coVerify(exactly = 1){
                    circleControllerLogicMock.fetchCircles()
                }
                confirmVerified(circleControllerLogicMock)
                verify(exactly = 1){
                    circleResponseMock.makeCircleListFetched(any())
                }
                confirmVerified(circleResponseMock)
            }
        }

        test("正常系_データなし"){
            //準備
            val circles = listOf<Circle>()
            val circleControllerLogicMock = mockk<CircleControllerLogic>{
                coEvery { fetchCircles() } returns circles
            }
            val res = JsonObject(mapOf())
            val circleResponseMock = mockk<CircleResponse>{
                every { makeCircleListFetched(any()) } returns res
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every { Module.koinModules() } returns module {
                single<CircleControllerLogic> { circleControllerLogicMock }
                single<CircleResponse> { circleResponseMock }
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    circleController()
                }
                // 実行(リクエスト)
                client.get("/circle"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.OK
                    bodyAsText() shouldBeEqual "{}"
                }
                //検証
                coVerify(exactly = 1){
                    circleControllerLogicMock.fetchCircles()
                }
                confirmVerified(circleControllerLogicMock)
                verify(exactly = 1){
                    circleResponseMock.makeCircleListFetched(any())
                }
                confirmVerified(circleResponseMock)
            }
        }

        test("異常系_circleControllerLogicでエラー"){
            //準備
            val circleControllerLogicMock = mockk<CircleControllerLogic>{
                coEvery { fetchCircles() } throws Exception()
            }
            val circleResponseMock = mockk<CircleResponse>{}
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every { Module.koinModules() } returns module {
                single<CircleControllerLogic> { circleControllerLogicMock }
                single<CircleResponse> { circleResponseMock }
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    circleController()
                }
                // 実行(リクエスト)
                client.get("/circle"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"Internal Server Error\"}"
                }
                //検証
                coVerify(exactly = 1){
                    circleControllerLogicMock.fetchCircles()
                }
                confirmVerified(circleControllerLogicMock)
                verify(exactly = 0){
                    circleResponseMock.makeCircleListFetched(any())
                }
                confirmVerified(circleResponseMock)
            }
        }

        test("異常系_CircleResponseでエラー"){
            //準備
            val circles = listOf(
                Circle(1, "test1", null, listOf(), null, null),
                Circle(2, "test2", null, listOf(), null, null),
                Circle(3, "test3", null, listOf(), null, null)
            )
            val circleControllerLogicMock = mockk<CircleControllerLogic>{
                coEvery { fetchCircles() } returns circles
            }
            val circleResponseMock = mockk<CircleResponse>{
                every { makeCircleListFetched(any()) } throws Exception()
            }
            //モジュールの差し替え
            mockkObject(objects = arrayOf(Module), recordPrivateCalls = true)
            every { Module.koinModules() } returns module {
                single<CircleControllerLogic> { circleControllerLogicMock }
                single<CircleResponse> { circleResponseMock }
            }

            //実行(起動)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                //検証対象のRouteをロード
                routing {
                    circleController()
                }
                // 実行(リクエスト)
                client.get("/circle"){
                }.apply {
                    //検証(リクエスト)
                    logger.info(bodyAsText())
                    status shouldBeEqual HttpStatusCode.InternalServerError
                    bodyAsText() shouldBeEqual "{\"error\":\"Internal Server Error\"}"
                }
                //検証
                coVerify(exactly = 1){
                    circleControllerLogicMock.fetchCircles()
                }
                confirmVerified(circleControllerLogicMock)
                verify(exactly = 1){
                    circleResponseMock.makeCircleListFetched(any())
                }
                confirmVerified(circleResponseMock)
            }
        }
    }
})