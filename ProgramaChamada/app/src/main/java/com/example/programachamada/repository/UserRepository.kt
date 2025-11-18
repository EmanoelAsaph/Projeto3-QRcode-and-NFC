package com.example.programachamada.repository

import android.util.Log
import org.json.JSONObject

/**
 * Repositório para operações relacionadas a usuários usando GraphQL
 */
class UserRepository {

    companion object {
        private const val TAG = "UserRepository"
    }

    /**
     * Model para Usuário
     */
    data class Usuario(
        val email: String,
        val nome: String?,
        val cargo: String?,
        val cadastroRealizado: Boolean?,
        val contaAtiva: Boolean?
    )

    /**
     * Model para Turma
     */
    data class Turma(
        val id: String,
        val nome: String,
        val descricao: String?,
        val periodo: String,
        val professorEmail: String,
        val turmaAtiva: Boolean?
    )

    /**
     * Busca um usuário por email
     *
     * Exemplo de uso:
     * ```
     * val repository = UserRepository()
     * val result = repository.getUsuarioPorEmail("professor@gmail.com")
     * if (result.isSuccess) {
     *     val usuario = result.getOrNull()
     *     println("Usuário: ${usuario?.nome}")
     * }
     * ```
     */
    suspend fun getUsuarioPorEmail(email: String): Result<Usuario?> {
        val query = """
            query GetUsuario {
              getTbUsuarios(email: "$email") {
                email
                nome
                cargo
                cadastro_realizado
                conta_ativa
              }
            }
        """.trimIndent()

        return try {
            GraphQLClient.executeQuery(query) { responseData ->
                val json = JSONObject(responseData)
                val usuarioJson = json.optJSONObject("getTbUsuarios")

                if (usuarioJson != null) {
                    Usuario(
                        email = usuarioJson.getString("email"),
                        nome = usuarioJson.optString("nome", null),
                        cargo = usuarioJson.optString("cargo", null),
                        cadastroRealizado = usuarioJson.optBoolean("cadastro_realizado"),
                        contaAtiva = usuarioJson.optBoolean("conta_ativa")
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar usuário", e)
            Result.failure(e)
        }
    }

    /**
     * Lista todas as turmas (com filtro opcional)
     *
     * Exemplo de uso:
     * ```
     * val repository = UserRepository()
     * val result = repository.listarTurmas(professorEmail = "professor@gmail.com")
     * if (result.isSuccess) {
     *     val turmas = result.getOrNull()
     *     turmas?.forEach { turma ->
     *         println("Turma: ${turma.nome}")
     *     }
     * }
     * ```
     */
    suspend fun listarTurmas(
        professorEmail: String? = null,
        limit: Int = 100
    ): Result<List<Turma>> {
        val filterPart = if (professorEmail != null) {
            """filter: { professorEmail: { eq: "$professorEmail" } },"""
        } else {
            ""
        }

        val query = """
            query ListTurmas {
              listTbTurmas($filterPart limit: $limit) {
                items {
                  id
                  nome
                  descricao
                  periodo
                  professorEmail
                  turma_ativa
                }
              }
            }
        """.trimIndent()

        return try {
            GraphQLClient.executeQuery(query) { responseData ->
                val json = JSONObject(responseData)
                val listTurmasJson = json.optJSONObject("listTbTurmas")
                val itemsArray = listTurmasJson?.optJSONArray("items")

                val turmas = mutableListOf<Turma>()
                if (itemsArray != null) {
                    for (i in 0 until itemsArray.length()) {
                        val turmaJson = itemsArray.getJSONObject(i)
                        turmas.add(
                            Turma(
                                id = turmaJson.getString("id"),
                                nome = turmaJson.getString("nome"),
                                descricao = turmaJson.optString("descricao", null),
                                periodo = turmaJson.getString("periodo"),
                                professorEmail = turmaJson.getString("professorEmail"),
                                turmaAtiva = turmaJson.optBoolean("turma_ativa")
                            )
                        )
                    }
                }
                turmas
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao listar turmas", e)
            Result.failure(e)
        }
    }

    /**
     * Cria uma nova turma
     *
     * Exemplo de uso:
     * ```
     * val repository = UserRepository()
     * val result = repository.criarTurma(
     *     nome = "Programação Mobile",
     *     periodo = "2025/1",
     *     professorEmail = "professor@gmail.com",
     *     descricao = "Curso de desenvolvimento Android"
     * )
     * if (result.isSuccess) {
     *     val turma = result.getOrNull()
     *     println("Turma criada: ${turma?.id}")
     * }
     * ```
     */
    suspend fun criarTurma(
        nome: String,
        periodo: String,
        professorEmail: String,
        descricao: String? = null,
        turmaAtiva: Boolean = true
    ): Result<Turma> {
        val descricaoPart = if (descricao != null) {
            """, descricao: "$descricao" """
        } else {
            ""
        }

        val mutation = """
            mutation CreateTurma {
              createTbTurmas(input: {
                nome: "$nome",
                periodo: "$periodo",
                professorEmail: "$professorEmail"
                $descricaoPart,
                turma_ativa: $turmaAtiva
              }) {
                id
                nome
                descricao
                periodo
                professorEmail
                turma_ativa
              }
            }
        """.trimIndent()

        return try {
            GraphQLClient.executeMutation(mutation) { responseData ->
                val json = JSONObject(responseData)
                val turmaJson = json.getJSONObject("createTbTurmas")

                Turma(
                    id = turmaJson.getString("id"),
                    nome = turmaJson.getString("nome"),
                    descricao = turmaJson.optString("descricao", null),
                    periodo = turmaJson.getString("periodo"),
                    professorEmail = turmaJson.getString("professorEmail"),
                    turmaAtiva = turmaJson.optBoolean("turma_ativa")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar turma", e)
            Result.failure(e)
        }
    }

    /**
     * Registra presença usando QR Code
     *
     * Exemplo de uso:
     * ```
     * val repository = UserRepository()
     * val result = repository.registrarPresencaQRCode("qrcode-string-aqui")
     * if (result.isSuccess) {
     *     println("Presença registrada com sucesso!")
     * }
     * ```
     */
    suspend fun registrarPresencaQRCode(qrcode: String): Result<String> {
        val mutation = """
            mutation RegistrarPresenca {
              registrarPresencaQRCode(qrcode: "$qrcode") {
                id
                alunoEmail
                presente
                tipo
              }
            }
        """.trimIndent()

        return try {
            GraphQLClient.executeMutation(mutation) { responseData ->
                val json = JSONObject(responseData)
                val presencaJson = json.getJSONObject("registrarPresencaQRCode")
                presencaJson.getString("id")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao registrar presença", e)
            Result.failure(e)
        }
    }

    /**
     * Gera QR Code para uma aula
     *
     * Exemplo de uso:
     * ```
     * val repository = UserRepository()
     * val result = repository.gerarQRCodeAula("aula-id-123")
     * if (result.isSuccess) {
     *     val qrCode = result.getOrNull()
     *     println("QR Code gerado: $qrCode")
     * }
     * ```
     */
    suspend fun gerarQRCodeAula(aulaId: String): Result<String?> {
        val mutation = """
            mutation GerarQRCode {
              gerarQRCodeAula(aulaId: "$aulaId")
            }
        """.trimIndent()

        return try {
            GraphQLClient.executeMutation(mutation) { responseData ->
                val json = JSONObject(responseData)
                json.optString("gerarQRCodeAula", null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao gerar QR Code", e)
            Result.failure(e)
        }
    }
}
