package net.hennabatch.dojinapi.test

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.*
import aws.smithy.kotlin.runtime.net.url.Url

class InitCognito {

    companion object{
        suspend fun createUserPool(userPoolRegion: String, userPoolName: String, endpointUrl: Url): String?{
            val request = CreateUserPoolRequest {
                this.poolName = userPoolName
                policies = UserPoolPolicyType {
                    passwordPolicy = PasswordPolicyType {
                        minimumLength = 6
                        requireLowercase = true
                        requireNumbers = true
                        requireSymbols = true
                        requireUppercase = true
                        temporaryPasswordValidityDays = 7
                    }
                }
            }

            CognitoIdentityProviderClient {
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                val createUserPoolResponse = cognitoClient.createUserPool(request)
                return createUserPoolResponse.userPool?.id
            }
        }

        suspend fun createNewUser(userPoolRegion: String, userPoolId: String, name: String, email: String, endpointUrl: Url) {

            val attType = AttributeType {
                this.name = "email"
                value = email
            }

            val request = AdminCreateUserRequest {
                this.userPoolId = userPoolId
                username = name
                userAttributes = listOf(attType)
                messageAction = MessageActionType.Suppress
            }

            CognitoIdentityProviderClient {
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                val response = cognitoClient.adminCreateUser(request)
                println("User ${response.user?.username} is created. Status is ${response.user?.userStatus}")
            }
        }

        suspend fun setPassword(userPoolRegion: String, userPoolId: String, name: String, password: String, endpointUrl: Url){

            val request = AdminSetUserPasswordRequest {
                this.userPoolId = userPoolId
                this.username = name
                this.password = password
                this.permanent = true
            }

            CognitoIdentityProviderClient {
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                cognitoClient.adminSetUserPassword(request)
            }
        }

        suspend fun getToken(userPoolRegion: String, userPoolId: String, clientId: String, name: String, password: String, endpointUrl: Url):String?{
            val request = AdminInitiateAuthRequest{
                this.userPoolId = userPoolId
                this.clientId = clientId
                this.authFlow = AuthFlowType.AdminUserPasswordAuth
                this.authParameters = mapOf(
                    "USERNAME" to name,
                    "PASSWORD" to password
                )
            }

            CognitoIdentityProviderClient {
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                val response = cognitoClient.adminInitiateAuth(request)
                return response.authenticationResult?.accessToken
            }
        }
    }
}