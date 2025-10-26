package local.socialnetwork.core.cloud.aws.secrets;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSSecretsManagerProvider {

    private final SecretsManagerClient secretsManagerClient;
    private final ObjectMapper objectMapper;

    public String getValueByKeyAndSecretName(String key, String secretName) {
        return takeSecretValueBySecretName(secretName).getOrDefault(key, null);
    }

    private Map<String, String> takeSecretValueBySecretName(String secretName) {
        String secretValue = fetchSecretValueBySecretName(secretName);
        return parseSecretToMap(secretValue);
    }

    private String fetchSecretValueBySecretName(String secretName) {
        try {
            return secretsManagerClient.getSecretValue(request -> request.secretId(secretName)).secretString();
        } catch (SecretsManagerException e) {
            log.error("Failed to retrieve secret. Error: {}", e.awsErrorDetails());
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    private Map<String, String> parseSecretToMap(String secret) {
        try {
            return objectMapper.readValue(secret, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse secret. Error: {}", e.getMessage());
            throw new RuntimeException("Failed to parse secret", e);
        }
    }
}
