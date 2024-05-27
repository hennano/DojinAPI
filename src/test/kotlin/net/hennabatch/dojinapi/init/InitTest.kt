package net.hennabatch.dojinapi.init

import io.kotest.core.spec.style.FunSpec
import aws.smithy.kotlin.runtime.net.url.Url
import io.kotest.matchers.shouldNotBe
import net.hennabatch.dojinapi.db.DatabaseSingleton

class InitTest: FunSpec({
    val region = "ap-northeast-1"
    val userPoolName = "DojinAPIUserPool"
    val endpoint = Url.parse("http://localhost:5000")
    val cognitoUserName = "localtest"
    val userEmail = "localtest@hennabatch.net"
    val userPassword = "Tes7userp@ssword"

    val jdbcUrl = "jdbc:postgresql://localhost:5432/DOJINLIB?currentSchema=djla"
    val userName = "user"
    val pass = "localuserpass"

    context("リソース作成"){
        test("Cognito"){

            InitCognito.setSeed(5000, 42)

            val userPoolId = InitCognito.createUserPool(
                userPoolRegion = region,
                userPoolName = userPoolName,
                endpointUrl = endpoint
            )
            println("userpoolId: $userPoolId")

            InitCognito.createNewUser(
                userPoolRegion = region,
                userPoolId = userPoolId!!,
                name = cognitoUserName,
                email = userEmail,
                endpointUrl = endpoint
            )

            InitCognito.setPassword(
                userPoolRegion = region,
                userPoolId = userPoolId,
                name = cognitoUserName,
                password = userPassword,
                endpointUrl = endpoint
            )
        }

        test("rds"){
            DatabaseSingleton.connect(jdbcUrl, userName, pass)
            InitDB.createAllTable()
        }
    }

    context("リソース削除"){
        test("Cognito"){
            val userPoolId = InitCognito.getUserPoolId(
                userPoolRegion = region,
                userPoolName = userPoolName,
                endpointUrl = endpoint
            )

            userPoolId shouldNotBe null

            InitCognito.deleteUserPool(
                userPoolRegion = region,
                userPoolId = userPoolId!!,
                endpointUrl = endpoint
            )
        }
        test("rds"){
            DatabaseSingleton.connect(jdbcUrl, userName, pass)
            InitDB.dropAllTable()
        }
    }
})