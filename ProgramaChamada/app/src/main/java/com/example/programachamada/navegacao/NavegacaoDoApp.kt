package com.example.programachamada.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.programachamada.telas.TelaAdicionarAluno
import com.example.programachamada.telas.TelaAdicionarTurma
import com.example.programachamada.telas.TelaAluno
import com.example.programachamada.telas.TelaDeLogin
import com.example.programachamada.telas.TelaProfessor

@Composable
fun NavegacaoDoApp() {
    val controladorDeNavegacao = rememberNavController()

    NavHost(
        navController = controladorDeNavegacao,
        startDestination = "tela_login"
    ) {
        composable("tela_login") {
            TelaDeLogin(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable("tela_professor") {
            TelaProfessor(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable("tela_aluno") {
            TelaAluno(controladorDeNavegacao = controladorDeNavegacao)
        }

        // --- NOVAS ROTAS ADICIONADAS ---
        composable("tela_adicionar_turma") {
            TelaAdicionarTurma(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable("tela_adicionar_aluno") {
            TelaAdicionarAluno(controladorDeNavegacao = controladorDeNavegacao)
        }
    }
}