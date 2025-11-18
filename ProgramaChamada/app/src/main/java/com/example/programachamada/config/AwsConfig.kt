package com.example.programachamada.config

/**
 * Configuração do AWS Amplify
 * Configurações do backend RuralCheck - Cognito e AppSync
 */
object AwsConfig {
    // Cognito User Pool
    const val USER_POOL_ID = "us-east-2_zu5gNi4Ze"
    const val USER_POOL_CLIENT_ID = "63b4p8q8562eft3u4t2t0qsvmg"
    const val IDENTITY_POOL_ID = "us-east-2:381c2ea6-7c76-45a5-bc33-fba6bfb349e0"
    const val REGION = "us-east-2"

    // AppSync GraphQL
    const val GRAPHQL_ENDPOINT = "https://2umok2mglrdejaxw6uobw53rse.appsync-api.us-east-2.amazonaws.com/graphql"
    const val GRAPHQL_REGION = "us-east-2"
    const val DEFAULT_AUTH_MODE = "AMAZON_COGNITO_USER_POOLS"

    /**
     * Retorna a configuração Amplify em formato JSON
     */
    fun getAmplifyConfigJson(): String {
        return """
        {
            "UserAgent": "aws-amplify-cli/2.0",
            "Version": "1.0",
            "auth": {
                "plugins": {
                    "awsCognitoAuthPlugin": {
                        "UserAgent": "aws-amplify-cli/0.1.0",
                        "Version": "0.1.0",
                        "IdentityManager": {
                            "Default": {}
                        },
                        "CredentialsProvider": {
                            "CognitoIdentity": {
                                "Default": {
                                    "PoolId": "$IDENTITY_POOL_ID",
                                    "Region": "$REGION"
                                }
                            }
                        },
                        "CognitoUserPool": {
                            "Default": {
                                "PoolId": "$USER_POOL_ID",
                                "AppClientId": "$USER_POOL_CLIENT_ID",
                                "Region": "$REGION"
                            }
                        },
                        "Auth": {
                            "Default": {
                                "authenticationFlowType": "USER_SRP_AUTH",
                                "socialProviders": [],
                                "usernameAttributes": ["EMAIL"],
                                "signupAttributes": ["EMAIL"],
                                "passwordProtectionSettings": {
                                    "passwordPolicyMinLength": 8,
                                    "passwordPolicyCharacters": []
                                },
                                "mfaConfiguration": "OFF",
                                "mfaTypes": ["SMS"],
                                "verificationMechanisms": ["EMAIL"]
                            }
                        }
                    }
                }
            },
            "api": {
                "plugins": {
                    "awsAPIPlugin": {
                        "ruralcheck": {
                            "endpointType": "GraphQL",
                            "endpoint": "$GRAPHQL_ENDPOINT",
                            "region": "$GRAPHQL_REGION",
                            "authorizationType": "$DEFAULT_AUTH_MODE"
                        }
                    }
                }
            }
        }
        """.trimIndent()
    }
}
