package com.example.programachamada

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.example.programachamada.config.AwsConfig
import org.json.JSONObject

/**
 * Classe Application customizada para inicializar o AWS Amplify
 */
class ProgramaChamadaApplication : Application() {

    companion object {
        private const val TAG = "ProgramaChamadaApp"
    }

    override fun onCreate() {
        super.onCreate()
        initializeAmplify()
    }

    /**
     * Inicializa o AWS Amplify com configurações do Cognito e AppSync
     */
    private fun initializeAmplify() {
        try {
            // Adiciona os plugins de Auth (Cognito) e API (AppSync)
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())

            // Configura o Amplify com as configurações do AwsConfig
            val configJson = JSONObject(AwsConfig.getAmplifyConfigJson())
            Amplify.configure(configJson, applicationContext)

            Log.i(TAG, "Amplify inicializado com sucesso")
            Log.i(TAG, "Cognito User Pool: ${AwsConfig.USER_POOL_ID}")
            Log.i(TAG, "GraphQL Endpoint: ${AwsConfig.GRAPHQL_ENDPOINT}")

        } catch (error: AmplifyException) {
            Log.e(TAG, "Erro ao inicializar Amplify", error)
        }
    }
}
