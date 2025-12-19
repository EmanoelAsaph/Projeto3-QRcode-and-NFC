package com.example.programachamada.telas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TelaProfessor(controladorDeNavegacao: NavController, turma: String?) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Turma: $turma",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center, // Centraliza o texto dentro do componente
                modifier = Modifier.fillMaxWidth() // Garante que o texto ocupe a largura total
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

            // Botão Fazer Chamada por QR Code
            Button(
                onClick = { controladorDeNavegacao.navigate("tela_gerar_qrcode/$turma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "Ícone de QR Code",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "Fazer chamada por QR Code")
            }
        }
    }
}