package com.example.programachamada.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.programachamada.componentes.MenuNavegacao
import com.example.programachamada.repository.AuthRepository
import com.example.programachamada.repository.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(controladorDeNavegacao: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var usuario by remember { mutableStateOf<UserRepository.Usuario?>(null) }
    var email by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    // Buscar dados do usuário logado
    LaunchedEffect(Unit) {
        isLoading = true
        val userData = authRepository.getCurrentUser()
        if (userData != null && userData.isSignedIn) {
            email = userData.email

            val usuarioResult = userRepository.getUsuarioPorEmail(userData.email)
            usuarioResult.onSuccess { usuarioData ->
                usuario = usuarioData
                isLoading = false
            }.onFailure { error ->
                mensagemErro = "Erro ao buscar dados: ${error.message}"
                isLoading = false
            }
        } else {
            // Não está logado, redirecionar para login
            controladorDeNavegacao.navigate("tela_login") {
                popUpTo("tela_perfil") { inclusive = true }
            }
        }
    }

    // Função para fazer logout
    fun fazerLogout() {
        isLoading = true
        coroutineScope.launch {
            authRepository.signOut()
            controladorDeNavegacao.navigate("tela_login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            MenuNavegacao(
                controladorDeNavegacao = controladorDeNavegacao,
                rotaAtual = "tela_perfil"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Spacer(modifier = Modifier.height(100.dp))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Carregando perfil...")
            } else if (mensagemErro != null) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    text = mensagemErro!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else if (usuario != null) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nome
                Text(
                    text = usuario!!.nome ?: "Nome não informado",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cargo
                Surface(
                    color = when (usuario!!.cargo?.uppercase()) {
                        "ADMIN" -> MaterialTheme.colorScheme.errorContainer
                        "PROFESSOR" -> MaterialTheme.colorScheme.primaryContainer
                        "ALUNO" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = usuario!!.cargo?.uppercase() ?: "SEM CARGO",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Card com informações
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Credenciais",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        InfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = email
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cargo
                        InfoItem(
                            icon = Icons.Default.Person,
                            label = "Cargo",
                            value = usuario!!.cargo ?: "Não definido"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status da conta
                        InfoItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "Conta Ativa",
                            value = if (usuario!!.contaAtiva == true) "Sim" else "Não",
                            valueColor = if (usuario!!.contaAtiva == true)
                                Color(0xFF4CAF50) else Color(0xFFF44336)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cadastro realizado
                        InfoItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "Cadastro Completo",
                            value = if (usuario!!.cadastroRealizado == true) "Sim" else "Não",
                            valueColor = if (usuario!!.cadastroRealizado == true)
                                Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão de logout
                OutlinedButton(
                    onClick = { fazerLogout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sair",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sair da Conta")
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}
