package net.hennabatch.dojinapi.plugins

import aws.smithy.kotlin.runtime.net.url.Url
import com.auth0.jwk.JwkProviderBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import net.hennabatch.dojinapi.common.util.logger
import net.hennabatch.dojinapi.test.InitCognito
import kotlin.test.assertEquals

class AuthCognitoJwtTest: FunSpec({
    val region = "ap-northeast-1"
    val userPoolName = "DojinAPIUserPool"
    val userPoolClientName = "DojinAPIUserPoolClient"
    val endpoint = Url.parse("http://localhost:5000")
    val cognitoUserName = "localtest"
    val userEmail = "localtest@hennabatch.net"
    val userPassword = "Tes7userp@ssword"
    //2024-04-20 15:37:30.596生成
    val expiredToken = "eyJ0eXAiOiJKV1QiLCJraWQiOiJkdW1teSIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJodHRwczovL2NvZ25pdG8taWRwLmFwLW5vcnRoZWFzdC0xLmFtYXpvbmF3cy5jb20vYXAtbm9ydGhlYXN0LTFfYzQwZGI5YjQ4ODVmNGU2NjgyYjZkMmM1ZmE1ZDMxMDAiLCJzdWIiOiJlZjQ4ZThkNS01MGZkLTRkM2YtODVkNS0xNjk1OTBiMmI2MzMiLCJjbGllbnRfaWQiOiJ0ZjRzejYzeTA1ZmFxdnVkcDNhbzBvZ3RpciIsInRva2VuX3VzZSI6ImFjY2VzcyIsImF1dGhfdGltZSI6MTcxMzU5NTA1MCwiZXhwIjoxNzEzNTk4NjUwLCJ1c2VybmFtZSI6ImxvY2FsdGVzdCJ9.IiAAf9tGXljGxTRz9eX7m00us-LTibEIrPa0pno5uf691KcBUDv0cxEfP_ymEdMOpQ2-Y01CgNaUGoVMJyucXuFR4cTrXj3pTgTikhCeFjMmvEhvdHRlgHfNMEoEtazJKslCbXJSXsggOodTD_-tDylHQNlI6Rt2BjYQP3rbmuHUuG1UGuAe2Ph957UwAq74mzoFYm4CpHOdjl-ga-uElwq3cxXH-sflSeHnLcoaEHkt47Sda-jEF9kMvNB81LnCHt54nbNr0B71sLg7jGPG241JQFxbyRKLUjyAVeTx2syWHElykgXYhBn8I97L3Ie3rY9Yo7dl8FziAFd-CDnakQ"


    beforeSpec{
        InitCognito.setSeed(5000, 42)
    }
    beforeTest {
        InitCognito.getUserPools(region, endpoint)
            .forEach{
                it.id?.let { it1 -> InitCognito.deleteUserPool(region, it1, endpoint) }
            }
        InitCognito.createUserPool(region, userPoolName, endpoint)
    }

    afterTest {
        unmockkAll()
    }

    context("正常系") {
        test("正常") {
            //モック
            mockkStatic(::buildJwtProvider, ::getAudience)
            every { buildJwtProvider(any()) } returns JwkProviderBuilder(
                "http://localhost:5000/ap-northeast-1_bdd640fb06674ad19c80317fa3b1799d"
            )
            .headers(mapOf(
                "Authorization" to "AWS4-HMAC-SHA256 Credential=mock_access_key/20220524/us-east-1/cognito-idp/aws4_request, SignedHeaders=content-length;content-type;host;x-amz-date, Signature=asdf"
            ))
            .build()
            val slotCredential = slot<JWTCredential>()
            every { getAudience(
                credential = capture(slotCredential)
            ) } answers  {
                listOf(slotCredential.captured.getClaim("client_id", String::class) ?: "")
            }
            //Cognito生成
            val id = InitCognito.getUserPoolId(region, userPoolName, endpoint)
            id shouldNotBe null
            val clientId = InitCognito.createUserPoolClient(region, id!!, userPoolClientName, endpoint)
            clientId shouldNotBe null
            logger.info("issuer: $id")
            InitCognito.createNewUser(region, id, cognitoUserName, userEmail, endpoint)
            InitCognito.setPassword(region, id, cognitoUserName, userPassword, endpoint)
            val token = InitCognito.getToken(region, id, clientId!!, cognitoUserName, userPassword, endpoint)
            token shouldNotBe null
            logger.info("token: $token")
            testApplication {
                    environment {
                        config = ApplicationConfig("application_local.yaml")
                    }
                    application {
                        routing {
                            authenticate("jwt") {
                                get("/") {
                                    call.respondText("Hello World!")
                                }
                            }
                        }
                    }
                // 実行
                client.get("/"){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }.apply {
                    logger.info(bodyAsText())
                    assertEquals(HttpStatusCode.OK, status)
                    assertEquals("Hello World!", bodyAsText())
                }
            }
        }
    }
    context("異常系"){
        test("jwtトークンなし"){
            //モック
            mockkStatic(::buildJwtProvider, ::getAudience)
            every { buildJwtProvider(any()) } returns JwkProviderBuilder(
                "http://localhost:5000/ap-northeast-1_bdd640fb06674ad19c80317fa3b1799d"
            )
                .headers(mapOf(
                    "Authorization" to "AWS4-HMAC-SHA256 Credential=mock_access_key/20220524/us-east-1/cognito-idp/aws4_request, SignedHeaders=content-length;content-type;host;x-amz-date, Signature=asdf"
                ))
                .build()
            val slotCredential = slot<JWTCredential>()
            every { getAudience(
                credential = capture(slotCredential)
            ) } answers  {
                listOf(slotCredential.captured.getClaim("client_id", String::class) ?: "")
            }
            //Cognito生成
            val id = InitCognito.getUserPoolId(region, userPoolName, endpoint)
            id shouldNotBe null
            val clientId = InitCognito.createUserPoolClient(region, id!!, userPoolClientName, endpoint)
            clientId shouldNotBe null
            InitCognito.createNewUser(region, id, cognitoUserName, userEmail, endpoint)
            InitCognito.setPassword(region, id, cognitoUserName, userPassword, endpoint)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                application {
                    routing {
                        authenticate("jwt") {
                            get("/") {
                                call.respondText("Hello World!")
                            }
                        }
                    }
                }
                //実行
                client.get("/"){
                }.apply {
                    logger.info(bodyAsText())
                    assertEquals(HttpStatusCode.Unauthorized, status)
                }
            }
        }

        test("jwtトークンの期限が切れている"){
            //モック
            mockkStatic(::buildJwtProvider, ::getAudience)
            every { buildJwtProvider(any()) } returns JwkProviderBuilder(
                "http://localhost:5000/ap-northeast-1_bdd640fb06674ad19c80317fa3b1799d"
            )
                .headers(mapOf(
                    "Authorization" to "AWS4-HMAC-SHA256 Credential=mock_access_key/20220524/us-east-1/cognito-idp/aws4_request, SignedHeaders=content-length;content-type;host;x-amz-date, Signature=asdf"
                ))
                .build()
            val slotCredential = slot<JWTCredential>()
            every { getAudience(
                credential = capture(slotCredential)
            ) } answers  {
                listOf(slotCredential.captured.getClaim("client_id", String::class) ?: "")
            }
            //Cognito生成
            val id = InitCognito.getUserPoolId(region, userPoolName, endpoint)
            id shouldNotBe null
            val clientId = InitCognito.createUserPoolClient(region, id!!, userPoolClientName, endpoint)
            clientId shouldNotBe null
            InitCognito.createNewUser(region, id, cognitoUserName, userEmail, endpoint)
            InitCognito.setPassword(region, id, cognitoUserName, userPassword, endpoint)
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                application {
                    routing {
                        authenticate("jwt") {
                            get("/") {
                                call.respondText("Hello World!")
                            }
                        }
                    }
                }
                //実行
                client.get("/"){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $expiredToken")
                    }
                }.apply {
                    logger.info(bodyAsText())
                    assertEquals(HttpStatusCode.Unauthorized, status)
                }
            }
        }

        test("別のユーザープールで作成されたjwt"){
            //モック
            mockkStatic(::buildJwtProvider, ::getAudience)
            every { buildJwtProvider(any()) } returns JwkProviderBuilder(
                "http://localhost:5000/ap-northeast-1_bdd640fb06674ad19c80317fa3b1799d"
            )
                .headers(mapOf(
                    "Authorization" to "AWS4-HMAC-SHA256 Credential=mock_access_key/20220524/us-east-1/cognito-idp/aws4_request, SignedHeaders=content-length;content-type;host;x-amz-date, Signature=asdf"
                ))
                .build()
            val slotCredential = slot<JWTCredential>()
            every { getAudience(
                credential = capture(slotCredential)
            ) } answers  {
                listOf(slotCredential.captured.getClaim("client_id", String::class) ?: "")
            }
            //Cognito生成
            val id = InitCognito.getUserPoolId(region, userPoolName, endpoint)
            id shouldNotBe null
            val clientId = InitCognito.createUserPoolClient(region, id!!, userPoolClientName, endpoint)
            clientId shouldNotBe null
            InitCognito.createNewUser(region, id, cognitoUserName, userEmail, endpoint)
            InitCognito.setPassword(region, id, cognitoUserName, userPassword, endpoint)
            // 別Cognitoの生成
            val otherPoolId = InitCognito.createUserPool(region, "otherPool", endpoint)
            otherPoolId shouldNotBe null
            val otherClientId = InitCognito.createUserPoolClient(region, otherPoolId!!, userPoolClientName, endpoint)
            otherClientId shouldNotBe null
            InitCognito.createNewUser(region, otherPoolId, cognitoUserName, userEmail, endpoint)
            InitCognito.setPassword(region, otherPoolId, cognitoUserName, userPassword, endpoint)
            val token = InitCognito.getToken(region, otherPoolId, otherClientId!!, cognitoUserName, userPassword, endpoint)
            token shouldNotBe null
            testApplication {
                environment {
                    config = ApplicationConfig("application_local.yaml")
                }
                application {
                    routing {
                        authenticate("jwt") {
                            get("/") {
                                call.respondText("Hello World!")
                            }
                        }
                    }
                }
                //実行
                client.get("/"){
                    headers{
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }.apply {
                    logger.info(bodyAsText())
                    assertEquals(HttpStatusCode.Unauthorized, status)
                }
            }
        }
    }
})

