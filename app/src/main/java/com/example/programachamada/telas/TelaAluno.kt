package com.example.programachamada.telas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TelaAluno(controladorDeNavegacao: NavController) {
    // SOLUÇÃO DEFINITIVA: Separar a criação do State da delegação de valor.

    // 1. Primeiro, criamos o objeto State explicitamente, garantindo que ele nunca seja nulo.
    val turmaEscaneadaState: State<String?> = controladorDeNavegacao.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("turma_escaneada")
        ?.observeAsState() ?: remember { mutableStateOf(null) } // Usamos um State padrão se a expressão for nula.

    // 2. Agora, com um State bem definido, usamos a delegação para extrair o valor.
    val turmaEscaneada: String? by turmaEscaneadaState

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(turmaEscaneada) {
        if (turmaEscaneada != null) {
            showDialog = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo, Aluno!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Lógica NFC */ },
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = "Ícone de NFC",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Marcar presença por NFC")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { controladorDeNavegacao.navigate("tela_camera") },
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "Ícone de QR Code",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Marcar presença por QR Code")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        controladorDeNavegacao.currentBackStackEntry?.savedStateHandle?.remove<String>("turma_escaneada")
                    },
                    title = { Text("Presença confirmada") },
                    text = { Text("Presença confirmada para $turmaEscaneada.") },
                    confirmButton = {
                        Button(onClick = {
                            showDialog = false
                            controladorDeNavegacao.currentBackStackEntry?.savedStateHandle?.remove<String>("turma_escaneada")
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
