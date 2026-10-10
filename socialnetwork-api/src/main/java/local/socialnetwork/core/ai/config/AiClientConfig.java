package local.socialnetwork.core.ai.config;

import local.socialnetwork.core.cloud.aws.secrets.AWSSecretsManagerProvider;

import local.socialnetwork.core.config.AiProperties;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import org.springframework.ai.openai.api.OpenAiApi;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import org.springframework.web.client.RestClient;

/**
 * Configures the Spring AI {@link ChatClient} using an OpenAI API key retrieved from AWS Secrets
 * Manager. Declaring {@link OpenAiChatModel} as a bean causes Spring AI's
 * {@code @ConditionalOnMissingBean(OpenAiChatModel.class)} auto-configuration to back off,
 * ensuring only this AWS-backed model is used.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiClientConfig {

    private static final String KEY_NAME = "openai-public-api-key";

    private final AWSSecretsManagerProvider awsSecretsManagerProvider;
    private final AiProperties aiProperties;

    @Value("${aws.secretsmanager.secretName.socialnetwork-openai-api-key}")
    private String openAiApiKeySecretName;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    /**
     * Creates an {@link OpenAiChatModel} backed by an API key from AWS Secrets Manager and the
     * model configured via {@code spring.ai.openai.chat.options.model}.
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        var openAiApi = OpenAiApi.builder()
                .apiKey(retrieveApiKey())
                .restClientBuilder(RestClient.builder().requestFactory(requestFactory()))
                .build();
        var options = OpenAiChatOptions.builder()
                .model(model)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * Builds the {@link ChatClient} from the auto-configured builder, which uses the
     * {@link OpenAiChatModel} bean above.
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    private ClientHttpRequestFactory requestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(aiProperties.timeout().connect());
        factory.setReadTimeout(aiProperties.timeout().read());
        return factory;
    }

    private String retrieveApiKey() {
        var apiKey = awsSecretsManagerProvider.getValueByKeyAndSecretName(KEY_NAME, openAiApiKeySecretName);
        if (apiKey == null || apiKey.isBlank()) {
            log.error("OpenAI API key not found. Please check your AWS Secrets Manager configuration");
            throw new IllegalStateException("OpenAI API key is not found in AWS Secrets Manager");
        }
        log.info("OpenAI API key successfully retrieved from AWS Secrets Manager");
        return apiKey;
    }
}
