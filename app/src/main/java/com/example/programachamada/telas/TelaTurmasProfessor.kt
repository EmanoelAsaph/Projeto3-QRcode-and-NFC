package com.example.programachamada.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaTurmasProfessor(controladorDeNavegacao: NavController) {
    // Lista estática de turmas para demonstração
    val turmas = listOf("Engenharia de Software", "Arquitetura de Computadores", "Algoritmos e Estrutura de Dados")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Professor", fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = {
                        controladorDeNavegacao.navigate("tela_login") {
                            popUpTo(controladorDeNavegacao.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }) {
                        Text("Sair")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Turmas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(turmas) { turma ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { controladorDeNavegacao.navigate("tela_professor/$turma") },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = turma, style = MaterialTheme.typography.bodyLarge)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver turma"
                            )
                        }
                    }
                }
            }
        }
    }
}