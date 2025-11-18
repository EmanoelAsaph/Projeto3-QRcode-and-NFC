package com.example.programachamada.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuNavegacao(
    controladorDeNavegacao: NavController,
    rotaAtual: String
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") },
            selected = rotaAtual == "tela_professor" || rotaAtual == "tela_aluno",
            onClick = {
                // Volta para a tela principal (professor ou aluno)
                if (rotaAtual != "tela_professor" && rotaAtual != "tela_aluno") {
                    controladorDeNavegacao.navigate("tela_professor") {
                        launchSingleTop = true
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = rotaAtual == "tela_perfil",
            onClick = {
                if (rotaAtual != "tela_perfil") {
                    controladorDeNavegacao.navigate("tela_perfil") {
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
