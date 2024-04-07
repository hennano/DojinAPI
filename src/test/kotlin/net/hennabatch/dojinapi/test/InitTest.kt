package net.hennabatch.dojinapi.test

import io.kotest.core.spec.style.FunSpec
import aws.smithy.kotlin.runtime.net.url.Url

class InitTest: FunSpec({
    context("リソース作成"){
        test("Cognito"){
            val region = "ap-northeast-1"
            val userPoolName = "DojinAPIUserPool"
            val endpoint = Url.parse("http://localhost:5000")
            val userName = "localtest"
            val userEmail = "localtest@hennabatch.net"
            val userPassword = "Tes7userp@ssword"

            val userPoolId = InitCognito.createUserPool(
                userPoolRegion = region,
                userPoolName = userPoolName,
                endpointUrl = endpoint
            )

            InitCognito.createNewUser(
                userPoolRegion = region,
                userPoolId = userPoolId!!,
                name = userName,
                email = userEmail,
                endpointUrl = endpoint
            )

            InitCognito.setPassword(
                userPoolRegion = region,
                userPoolId = userPoolId,
                name = userName,
                password = userPassword,
                endpointUrl = endpoint
            )
        }

        test("rds"){

        }
    }
})