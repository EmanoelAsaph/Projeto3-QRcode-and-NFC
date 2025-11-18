# Integração AWS AppSync + Cognito - Android/Kotlin

## 📋 Resumo da Integração

Este projeto Android foi integrado com AWS AppSync (GraphQL) e AWS Cognito (autenticação) usando:
- **AWS Amplify Android** - SDK oficial para integração com serviços AWS
- **Apollo Kotlin** - Cliente GraphQL type-safe para Android

## 🛠️ Configurações Realizadas

### 1. Dependências Adicionadas
- AWS Amplify Core `2.14.11`
- AWS Amplify Auth (Cognito) `2.14.11`
- AWS Amplify API (AppSync) `2.14.11`
- Apollo Kotlin `4.0.0`

### 2. Arquivos Criados

#### Configuração
- `app/config/AwsConfig.kt` - Configurações AWS (User Pool, Identity Pool, GraphQL endpoint)
- `ProgramaChamadaApplication.kt` - Application class que inicializa Amplify

#### Repositórios
- `repository/AuthRepository.kt` - Autenticação com Cognito (signIn, signUp, signOut)
- `repository/GraphQLClient.kt` - Cliente GraphQL com Apollo
- `repository/UserRepository.kt` - Operações GraphQL (buscar usuário, criar turma, etc)

#### GraphQL Queries/Mutations
- `graphql/GetUsuario.graphql`
- `graphql/ListTurmas.graphql`
- `graphql/CreateTurma.graphql`
- `graphql/ListAulas.graphql`
- `graphql/GerarQRCodeAula.graphql`
- `graphql/RegistrarPresencaQRCode.graphql`

#### Telas Atualizadas
- `telas/TelaDeLogin.kt` - Agora usa autenticação Cognito real

## 🚀 Próximos Passos Importantes

### 1. Sincronizar Projeto Gradle
```bash
# No Android Studio, clique em:
File > Sync Project with Gradle Files
```

### 2. Gerar Código Apollo (Type-Safe GraphQL)
```bash
# Execute no terminal do projeto:
./gradlew :app:generateApolloSources

# Ou no Windows:
gradlew.bat :app:generateApolloSources
```

Isso vai gerar classes Kotlin type-safe baseadas no `schema.graphql` no pacote:
`com.example.programachamada.graphql`

### 3. Criar Usuários no Cognito

Antes de fazer login, você precisa criar usuários no AWS Cognito User Pool.

**Opção 1: Via AWS Console**
1. Acesse AWS Console > Cognito
2. Selecione o User Pool: `us-east-2_zu5gNi4Ze`
3. Vá em "Users" > "Create user"
4. Crie usuários de teste

**Opção 2: Via AWS CLI**
```bash
# Criar usuário aluno
aws cognito-idp admin-create-user \
  --user-pool-id us-east-2_zu5gNi4Ze \
  --username aluno@gmail.com \
  --temporary-password TempPass123! \
  --user-attributes Name=email,Value=aluno@gmail.com \
  --region us-east-2

# Definir senha permanente
aws cognito-idp admin-set-user-password \
  --user-pool-id us-east-2_zu5gNi4Ze \
  --username aluno@gmail.com \
  --password SuaSenhaSegura123! \
  --permanent \
  --region us-east-2
```

### 4. Criar Registros no GraphQL

Após criar usuários no Cognito, você precisa criar registros correspondentes no GraphQL:

```kotlin
// Exemplo de criar usuário no GraphQL
val userRepository = UserRepository()

// Importante: O email deve ser o mesmo do Cognito
val mutation = """
    mutation {
      createTbUsuarios(input: {
        email: "aluno@gmail.com",
        nome: "João Aluno",
        cargo: ALUNO,
        cadastro_realizado: true,
        conta_ativa: true
      }) {
        email
        nome
        cargo
      }
    }
"""
```

## 📝 Como Usar

### Exemplo 1: Login
```kotlin
val authRepository = AuthRepository()
val result = authRepository.signIn("usuario@email.com", "senha123")

when (result) {
    is AuthRepository.AuthResult.Success -> {
        // Login bem-sucedido
    }
    is AuthRepository.AuthResult.Error -> {
        // Erro no login
    }
    is AuthRepository.AuthResult.ConfirmationRequired -> {
        // Precisa confirmar email
    }
}
```

### Exemplo 2: Buscar Usuário
```kotlin
val userRepository = UserRepository()
val result = userRepository.getUsuarioPorEmail("usuario@email.com")

result.onSuccess { usuario ->
    println("Nome: ${usuario?.nome}")
    println("Cargo: ${usuario?.cargo}")
}
```

### Exemplo 3: Criar Turma
```kotlin
val userRepository = UserRepository()
val result = userRepository.criarTurma(
    nome = "Programação Mobile 2025",
    periodo = "2025/1",
    professorEmail = "professor@gmail.com",
    descricao = "Curso de Android com Kotlin"
)

result.onSuccess { turma ->
    println("Turma criada: ${turma.id}")
}
```

### Exemplo 4: Registrar Presença via QR Code
```kotlin
val userRepository = UserRepository()
val result = userRepository.registrarPresencaQRCode("qrcode-string")

result.onSuccess { presencaId ->
    println("Presença registrada: $presencaId")
}
```

## 🔐 Configurações de Segurança

### Cognito User Pool
- **ID**: `us-east-2_zu5gNi4Ze`
- **Client ID**: `63b4p8q8562eft3u4t2t0qsvmg`
- **Identity Pool**: `us-east-2:381c2ea6-7c76-45a5-bc33-fba6bfb349e0`
- **Região**: `us-east-2`
- **Login**: Email-based

### AppSync GraphQL
- **Endpoint**: `https://2umok2mglrdejaxw6uobw53rse.appsync-api.us-east-2.amazonaws.com/graphql`
- **Auth Mode**: AWS_IAM (padrão)
- **Região**: `us-east-2`

## 🎯 Funcionalidades Implementadas

### Autenticação (AuthRepository)
- ✅ Login (signIn)
- ✅ Cadastro (signUp)
- ✅ Confirmação de email (confirmSignUp)
- ✅ Logout (signOut)
- ✅ Buscar usuário atual (getCurrentUser)
- ✅ Verificar sessão (isUserSignedIn)

### GraphQL (UserRepository)
- ✅ Buscar usuário por email
- ✅ Listar turmas (com filtro por professor)
- ✅ Criar turma
- ✅ Registrar presença via QR Code
- ✅ Gerar QR Code para aula

### Interface
- ✅ Tela de login integrada com Cognito
- ✅ Loading indicator durante autenticação
- ✅ Mensagens de erro
- ✅ Navegação baseada no cargo do usuário (ALUNO/PROFESSOR)

## 🐛 Troubleshooting

### Erro: "Amplify has not been configured"
**Solução**: Verifique se `ProgramaChamadaApplication` está configurada no `AndroidManifest.xml`:
```xml
<application
    android:name=".ProgramaChamadaApplication"
    ...>
```

### Erro: "Network error" ou "Unauthorized"
**Solução**: Verifique se as credenciais no `AwsConfig.kt` estão corretas e se o usuário existe no Cognito.

### Erro ao gerar código Apollo
**Solução**: Verifique se o arquivo `schema.graphql` está no caminho correto:
```
ProgramaChamada/schema.graphql
```

### Erro: "User does not exist"
**Solução**: Crie o usuário no Cognito User Pool primeiro antes de tentar fazer login.

## 📚 Recursos Adicionais

- [AWS Amplify Android Docs](https://docs.amplify.aws/android/)
- [Apollo Kotlin Docs](https://www.apollographql.com/docs/kotlin/)
- [AWS Cognito Docs](https://docs.aws.amazon.com/cognito/)
- [AWS AppSync Docs](https://docs.aws.amazon.com/appsync/)

## ✅ Checklist de Verificação

Antes de testar o app, verifique:
- [ ] Sincronizou o projeto Gradle
- [ ] Executou `generateApolloSources`
- [ ] Criou usuários no Cognito User Pool
- [ ] Criou registros correspondentes no GraphQL (tbUsuarios)
- [ ] Verificou as configurações no `AwsConfig.kt`
- [ ] Adicionou permissões de internet no `AndroidManifest.xml` (já feito)

## 🎓 Diferenças entre JavaScript/TypeScript e Android/Kotlin

| Aspecto | JavaScript/TypeScript | Android/Kotlin |
|---------|----------------------|----------------|
| **Configuração** | `aws-exports.js` | `AwsConfig.kt` (JSON config) |
| **Inicialização** | `Amplify.configure(config)` | `Amplify.configure(json, context)` |
| **Biblioteca** | `@aws-amplify/auth` | `aws-auth-cognito` |
| **GraphQL Codegen** | `@aws-amplify/cli codegen` | Apollo Kotlin Plugin |
| **Auth Calls** | Promises/async-await | Suspending functions/coroutines |
| **GraphQL Client** | Amplify API | Apollo Client ou Amplify API |

---

**Autor**: Integração realizada por Claude Code
**Data**: 2025-11-12
**Versão**: 1.0
