package net.hennabatch.dojinapi.test

import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.*
import aws.sdk.kotlin.services.cognitoidentityprovider.paginators.listUserPoolsPaginated
import aws.smithy.kotlin.runtime.net.url.Url
import kotlinx.coroutines.flow.map

import io.ktor.client.HttpClient
import io.ktor.client.request.post


class InitCognito {

    companion object{

        suspend fun setSeed(port: Int, seed: Int){
            HttpClient().use {client ->
                client.post("http://localhost:$port/moto-api/seed?a=$seed")
            }
        }

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

        suspend fun getUserPoolId(userPoolRegion: String, userPoolName: String, endpointUrl: Url): String?{
            return getUserPools(userPoolRegion, endpointUrl).find { it.name == userPoolName}?.id
        }

        suspend fun getUserPools(userPoolRegion: String, endpointUrl: Url): List<UserPoolDescriptionType> {
            val request = ListUserPoolsRequest{
                maxResults = 10
            }

            CognitoIdentityProviderClient{
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                val listIdentityPoolsResponse = cognitoClient.listUserPoolsPaginated(request)
                val userPools = mutableListOf<UserPoolDescriptionType>()
                listIdentityPoolsResponse.map { it.userPools }.collect{ it?.let { it1 -> userPools.addAll(it1) } }
                return userPools
            }
        }

        suspend fun deleteUserPool(userPoolRegion: String, userPoolId: String, endpointUrl: Url){
            val request = DeleteUserPoolRequest{
                this.userPoolId = userPoolId
            }

            CognitoIdentityProviderClient {
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                cognitoClient.deleteUserPool(request)
            }
        }

        suspend fun createUserPoolClient(userPoolRegion: String, userPoolId: String, clientName: String,  endpointUrl: Url): String? {
            val request = CreateUserPoolClientRequest{
                this.userPoolId = userPoolId
                this.clientName = clientName
            }

            CognitoIdentityProviderClient{
                region = userPoolRegion
                this.endpointUrl = endpointUrl
            }.use { cognitoClient ->
                val response = cognitoClient.createUserPoolClient(request)
                return response.userPoolClient?.clientId
            }
        }

        suspend fun createNewUser(userPoolRegion: String, userPoolId: String, name: String, email: String, endpointUrl: Url): String? {

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
                return response.user?.userStatus?.value
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