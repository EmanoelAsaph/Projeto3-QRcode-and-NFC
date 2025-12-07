package com.example.programachamada.repository

import android.util.Log
import com.amplifyframework.api.aws.AppSyncGraphQLRequest
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.core.Amplify
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloRequest
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.network.http.HttpNetworkTransport
import com.example.programachamada.config.AwsConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Cliente GraphQL usando Apollo com autenticação AWS
 */
object GraphQLClient {

    private const val TAG = "GraphQLClient"

    /**
     * Cliente Apollo configurado com endpoint AppSync
     */
    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(AwsConfig.GRAPHQL_ENDPOINT)
            .build()
    }

    /**
     * Executa uma query GraphQL usando Amplify (recomendado para autenticação AWS)
     */
    suspend inline fun <reified T> executeQuery(
        query: String,
        variables: Map<String, Any?> = emptyMap(),
        crossinline mapper: (String) -> T
    ): Result<T> {
        return try {
            suspendCancellableCoroutine { continuation ->
                val request = AppSyncGraphQLRequest.builder()
                    .query(query)
                    .variables(variables)
                    .responseType(String::class.java)
                    .build()

                Amplify.API.query(
                    request,
                    { response: GraphQLResponse<String> ->
                        if (response.hasErrors()) {
                            val errors = response.errors.joinToString { it.message }
                            Log.e(TAG, "Erros GraphQL: $errors")
                            continuation.resume(Result.failure(Exception(errors)))
                        } else {
                            try {
                                val data = response.data
                                if (data != null) {
                                    val result = mapper(data)
                                    continuation.resume(Result.success(result))
                                } else {
                                    continuation.resume(Result.failure(Exception("Resposta vazia")))
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao processar resposta", e)
                                continuation.resume(Result.failure(e))
                            }
                        }
                    },
                    { error ->
                        Log.e(TAG, "Erro na query: ${error.message}", error)
                        continuation.resume(Result.failure(error as? Exception ?: Exception(error.message)))
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante query", e)
            Result.failure(e)
        }
    }

    /**
     * Executa uma mutation GraphQL usando Amplify (recomendado para autenticação AWS)
     */
    suspend inline fun <reified T> executeMutation(
        mutation: String,
        variables: Map<String, Any?> = emptyMap(),
        crossinline mapper: (String) -> T
    ): Result<T> {
        return try {
            suspendCancellableCoroutine { continuation ->
                val request = AppSyncGraphQLRequest.builder()
                    .query(mutation)
                    .variables(variables)
                    .responseType(String::class.java)
                    .build()

                Amplify.API.mutate(
                    request,
                    { response: GraphQLResponse<String> ->
                        if (response.hasErrors()) {
                            val errors = response.errors.joinToString { it.message }
                            Log.e(TAG, "Erros GraphQL: $errors")
                            continuation.resume(Result.failure(Exception(errors)))
                        } else {
                            try {
                                val data = response.data
                                if (data != null) {
                                    val result = mapper(data)
                                    continuation.resume(Result.success(result))
                                } else {
                                    continuation.resume(Result.failure(Exception("Resposta vazia")))
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao processar resposta", e)
                                continuation.resume(Result.failure(e))
                            }
                        }
                    },
                    { error ->
                        Log.e(TAG, "Erro na mutation: ${error.message}", error)
                        continuation.resume(Result.failure(error as? Exception ?: Exception(error.message)))
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante mutation", e)
            Result.failure(e)
        }
    }

    /**
     * Método auxiliar para executar queries Apollo (type-safe)
     * Nota: Para usar este método, você precisa gerar o código Apollo primeiro
     * executando: ./gradlew :app:generateApolloSources
     */
    suspend fun <D : Query.Data> query(query: Query<D>): ApolloResponse<D> {
        return try {
            apolloClient.query(query).execute()
        } catch (e: ApolloException) {
            Log.e(TAG, "Erro Apollo query", e)
            throw e
        }
    }

    /**
     * Método auxiliar para executar mutations Apollo (type-safe)
     * Nota: Para usar este método, você precisa gerar o código Apollo primeiro
     * executando: ./gradlew :app:generateApolloSources
     */
    suspend fun <D : Mutation.Data> mutate(mutation: Mutation<D>): ApolloResponse<D> {
        return try {
            apolloClient.mutation(mutation).execute()
        } catch (e: ApolloException) {
            Log.e(TAG, "Erro Apollo mutation", e)
            throw e
        }
    }
}
