package com.example.programachamada.telas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.programachamada.componentes.MenuNavegacao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaProfessor(controladorDeNavegacao: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Área do Professor") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            MenuNavegacao(
                controladorDeNavegacao = controladorDeNavegacao,
                rotaAtual = "tela_professor"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo, Professor!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão Fazer Chamada por NFC
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = "Ícone de NFC",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Fazer chamada por NFC")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp)) // Espaço maior para separar as seções

            // Botão Gerenciar Turma
            OutlinedButton(
                onClick = { controladorDeNavegacao.navigate("tela_adicionar_turma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "Gerenciar Turma")
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Ícone de seta",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}