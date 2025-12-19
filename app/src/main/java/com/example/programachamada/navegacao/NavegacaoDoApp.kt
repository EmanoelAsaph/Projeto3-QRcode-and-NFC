package com.example.programachamada.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.programachamada.telas.TelaAluno
import com.example.programachamada.telas.TelaCamera
import com.example.programachamada.telas.TelaDeLogin
import com.example.programachamada.telas.TelaGerarQrCode
import com.example.programachamada.telas.TelaProfessor
import com.example.programachamada.telas.TelaTurmasProfessor

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
        composable(
            route = "tela_professor/{turma}",
            arguments = listOf(navArgument("turma") { type = NavType.StringType })
        ) { backStackEntry ->
            TelaProfessor(
                controladorDeNavegacao = controladorDeNavegacao,
                turma = backStackEntry.arguments?.getString("turma")
            )
        }
        composable("tela_aluno") {
            TelaAluno(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable("tela_turmas_professor") {
            TelaTurmasProfessor(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable("tela_camera") {
            TelaCamera(controladorDeNavegacao = controladorDeNavegacao)
        }
        composable(
            route = "tela_gerar_qrcode/{turma}",
            arguments = listOf(navArgument("turma") { type = NavType.StringType })
        ) { backStackEntry ->
            TelaGerarQrCode(
                controladorDeNavegacao = controladorDeNavegacao,
                turma = backStackEntry.arguments?.getString("turma")
            )
        }
    }
}