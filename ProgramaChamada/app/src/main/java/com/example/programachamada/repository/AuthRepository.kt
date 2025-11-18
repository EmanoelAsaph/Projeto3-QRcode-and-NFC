package com.example.programachamada.repository

import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Repositório para gerenciar autenticação com AWS Cognito
 */
class AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    /**
     * Resultado de operações de autenticação
     */
    sealed class AuthResult {
        data class Success(val message: String = "Sucesso") : AuthResult()
        data class Error(val message: String, val exception: Exception? = null) : AuthResult()
        data class ConfirmationRequired(val email: String) : AuthResult()
    }

    /**
     * Dados do usuário autenticado
     */
    data class UserData(
        val email: String,
        val userId: String,
        val isSignedIn: Boolean
    )

    /**
     * Faz login com email e senha
     */
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signIn(
                    email,
                    password,
                    { result ->
                        if (result.isSignedIn) {
                            Log.i(TAG, "Login bem-sucedido para: $email")
                            continuation.resume(AuthResult.Success("Login realizado com sucesso"))
                        } else {
                            Log.w(TAG, "Login requer confirmação adicional")
                            continuation.resume(AuthResult.ConfirmationRequired(email))
                        }
                    },
                    { error ->
                        Log.e(TAG, "Erro no login: ${error.message}", error)
                        continuation.resume(
                            AuthResult.Error(
                                "Erro ao fazer login: ${error.message}",
                                error as? Exception
                            )
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante o login", e)
            AuthResult.Error("Erro inesperado no login: ${e.message}", e)
        }
    }

    /**
     * Cadastra um novo usuário
     */
    suspend fun signUp(email: String, password: String, name: String? = null): AuthResult {
        return try {
            suspendCancellableCoroutine { continuation ->
                val options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), email)
                    .apply {
                        name?.let {
                            userAttribute(AuthUserAttributeKey.name(), it)
                        }
                    }
                    .build()

                Amplify.Auth.signUp(
                    email,
                    password,
                    options,
                    { result ->
                        if (result.isSignUpComplete) {
                            Log.i(TAG, "Cadastro completo para: $email")
                            continuation.resume(AuthResult.Success("Cadastro realizado com sucesso"))
                        } else {
                            Log.i(TAG, "Cadastro requer confirmação: $email")
                            continuation.resume(
                                AuthResult.ConfirmationRequired(email)
                            )
                        }
                    },
                    { error ->
                        Log.e(TAG, "Erro no cadastro: ${error.message}", error)
                        continuation.resume(
                            AuthResult.Error(
                                "Erro ao cadastrar: ${error.message}",
                                error as? Exception
                            )
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante o cadastro", e)
            AuthResult.Error("Erro inesperado no cadastro: ${e.message}", e)
        }
    }

    /**
     * Confirma o cadastro com código enviado por email
     */
    suspend fun confirmSignUp(email: String, confirmationCode: String): AuthResult {
        return try {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.confirmSignUp(
                    email,
                    confirmationCode,
                    { result ->
                        if (result.isSignUpComplete) {
                            Log.i(TAG, "Confirmação bem-sucedida para: $email")
                            continuation.resume(AuthResult.Success("Conta confirmada com sucesso"))
                        } else {
                            continuation.resume(AuthResult.Error("Confirmação incompleta"))
                        }
                    },
                    { error ->
                        Log.e(TAG, "Erro na confirmação: ${error.message}", error)
                        continuation.resume(
                            AuthResult.Error(
                                "Erro ao confirmar código: ${error.message}",
                                error as? Exception
                            )
                        )
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante a confirmação", e)
            AuthResult.Error("Erro inesperado na confirmação: ${e.message}", e)
        }
    }

    /**
     * Faz logout do usuário
     */
    suspend fun signOut(): AuthResult {
        return try {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signOut(
                    Consumer { result ->
                        Log.i(TAG, "Logout realizado com sucesso")
                        continuation.resume(AuthResult.Success("Logout realizado com sucesso"))
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção durante o logout", e)
            AuthResult.Error("Erro inesperado no logout: ${e.message}", e)
        }
    }

    /**
     * Retorna informações do usuário atualmente autenticado
     */
    suspend fun getCurrentUser(): UserData? {
        return try {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.fetchUserAttributes(
                    { attributes ->
                        val email = attributes.find {
                            it.key == AuthUserAttributeKey.email()
                        }?.value ?: ""

                        Amplify.Auth.getCurrentUser(
                            { user ->
                                val userData = UserData(
                                    email = email,
                                    userId = user.userId,
                                    isSignedIn = true
                                )
                                Log.i(TAG, "Usuário atual: $email")
                                continuation.resume(userData)
                            },
                            { error ->
                                Log.e(TAG, "Erro ao buscar usuário: ${error.message}", error)
                                continuation.resume(null)
                            }
                        )
                    },
                    { error ->
                        Log.e(TAG, "Erro ao buscar atributos: ${error.message}", error)
                        continuation.resume(null)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao buscar usuário atual", e)
            null
        }
    }

    /**
     * Verifica se há um usuário autenticado
     */
    suspend fun isUserSignedIn(): Boolean {
        return try {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.fetchAuthSession(
                    { session ->
                        continuation.resume(session.isSignedIn)
                    },
                    { error ->
                        Log.e(TAG, "Erro ao verificar sessão: ${error.message}", error)
                        continuation.resume(false)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exceção ao verificar sessão", e)
            false
        }
    }
}
