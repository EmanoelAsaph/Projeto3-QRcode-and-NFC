package com.example.programachamada.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.programachamada.repository.AuthRepository
import com.example.programachamada.repository.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDeLogin(controladorDeNavegacao: NavController) {
    // Estados da tela
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }
    var usuarioLogado by remember { mutableStateOf<String?>(null) }

    // Repositórios
    val authRepository = remember { AuthRepository() }
    val userRepository = remember { UserRepository() }
    val coroutineScope = rememberCoroutineScope()

    /**
     * Verifica se já existe usuário logado ao abrir a tela
     */
    LaunchedEffect(Unit) {
        isLoading = true
        val userData = authRepository.getCurrentUser()
        if (userData != null && userData.isSignedIn) {
            usuarioLogado = userData.email
            // Redirecionar automaticamente
            val usuarioResult = userRepository.getUsuarioPorEmail(userData.email)
            usuarioResult.onSuccess { usuario ->
                if (usuario != null) {
                    when (usuario.cargo?.uppercase()) {
                        "ALUNO" -> {
                            controladorDeNavegacao.navigate("tela_aluno") {
                                popUpTo("tela_login") { inclusive = true }
                            }
                        }
                        "PROFESSOR", "ADMIN" -> {
                            controladorDeNavegacao.navigate("tela_professor") {
                                popUpTo("tela_login") { inclusive = true }
                            }
                        }
                        else -> {
                            // Fallback: redireciona para tela de professor
                            controladorDeNavegacao.navigate("tela_professor") {
                                popUpTo("tela_login") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
        isLoading = false
    }

    /**
     * Função para fazer logout
     */
    fun fazerLogout() {
        isLoading = true
        mensagemErro = null
        coroutineScope.launch {
            authRepository.signOut()
            usuarioLogado = null
            isLoading = false
        }
    }

    /**
     * Função para fazer login usando AWS Cognito
     */
    fun fazerLogin() {
        if (email.isBlank() || senha.isBlank()) {
            mensagemErro = "Por favor, preencha email e senha"
            return
        }

        isLoading = true
        mensagemErro = null

        coroutineScope.launch {
            // 1. Fazer login no Cognito
            val loginResult = authRepository.signIn(email, senha)

            when (loginResult) {
                is AuthRepository.AuthResult.Success -> {
                    // 2. Buscar dados do usuário no GraphQL
                    val usuarioResult = userRepository.getUsuarioPorEmail(email)

                    usuarioResult.onSuccess { usuario ->
                        if (usuario != null) {
                            // 3. Navegar baseado no cargo do usuário
                            when (usuario.cargo?.uppercase()) {
                                "ALUNO" -> {
                                    controladorDeNavegacao.navigate("tela_aluno") {
                                        popUpTo("tela_login") { inclusive = true }
                                    }
                                }
                                "PROFESSOR", "ADMIN" -> {
                                    // ADMIN e PROFESSOR vão para a mesma tela (ou crie tela_admin se necessário)
                                    controladorDeNavegacao.navigate("tela_professor") {
                                        popUpTo("tela_login") { inclusive = true }
                                    }
                                }
                                else -> {
                                    // Fallback: redireciona para tela de professor mesmo se cargo desconhecido
                                    controladorDeNavegacao.navigate("tela_professor") {
                                        popUpTo("tela_login") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            mensagemErro = "Usuário não encontrado no sistema"
                        }
                        isLoading = false
                    }.onFailure { error ->
                        mensagemErro = "Erro ao buscar dados do usuário: ${error.message}"
                        isLoading = false
                    }
                }

                is AuthRepository.AuthResult.Error -> {
                    // Se o erro for que já existe usuário logado, fazer logout e tentar novamente
                    if (loginResult.message.contains("already a user signed in", ignoreCase = true)) {
                        authRepository.signOut()
                        mensagemErro = "Fazendo logout do usuário anterior... Tente novamente."
                    } else {
                        mensagemErro = loginResult.message
                    }
                    isLoading = false
                }

                is AuthRepository.AuthResult.ConfirmationRequired -> {
                    mensagemErro = "Confirmação de email necessária. Verifique seu email."
                    isLoading = false
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sistema de Chamada",
                fontSize = 28.sp,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "RuralCheck - Login com AWS Cognito",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Mostrar informação se já existe usuário logado
            if (usuarioLogado != null) {
                Text(
                    text = "Usuário já conectado:",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = usuarioLogado!!,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { fazerLogout() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(text = "Trocar de Conta")
                    }
                }
                return@Column
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    mensagemErro = null
                },
                label = { Text("Email") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = "Ícone de usuário")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    mensagemErro = null
                },
                label = { Text("Senha") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Ícone de cadeado")
                }
            )

            // Mensagem de erro
            if (mensagemErro != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensagemErro!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { fazerLogin() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Entrar")
                }
            }
        }
    }
}