// Configuração do Amplify conectando ao backend RuralCheck

const config = {
	Auth: {
		Cognito: {
			userPoolId: "us-east-2_zu5gNi4Ze",
			userPoolClientId: "63b4p8q8562eft3u4t2t0qsvmg",
			identityPoolId: "us-east-2:381c2ea6-7c76-45a5-bc33-fba6bfb349e0",
			loginWith: {
				email: true,
			},
		},
	},
	API: {
		GraphQL: {
			endpoint: "https://2umok2mglrdejaxw6uobw53rse.appsync-api.us-east-2.amazonaws.com/graphql",
			region: "us-east-2",
			defaultAuthMode: "iam" as const,
		},
	},
};

export default config;
