package local.socialnetwork.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import local.socialnetwork.core.cloud.aws.secrets.AWSSecretsManagerProvider;

import org.mockito.Mockito;

import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;

/**
 * Replaces AWS Secrets Manager with a test double so the full Spring context
 * starts without live AWS credentials.
 *
 * <p>An RSA key pair is generated at context start-up and returned by the
 * {@code AWSSecretsManagerProvider} override, allowing {@code JwtTokenProvider}
 * to load real keys without touching Secrets Manager.
 * SES is mocked via {@code @MockBean} in {@link local.socialnetwork.BaseIntegrationTest}.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestAwsSecretsConfig {

    /**
     * Provides a primary {@link AWSSecretsManagerProvider} that returns freshly
     * generated RSA test keys, bypassing real Secrets Manager calls.
     */
    @Bean
    @Primary
    AWSSecretsManagerProvider awsSecretsManagerProvider() {
        try {
            var kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            var kp = kpg.generateKeyPair();
            var privateKeyB64 = Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded());
            var publicKeyB64 = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());

            return new AWSSecretsManagerProvider(
                    Mockito.mock(SecretsManagerClient.class), new ObjectMapper()) {
                @Override
                public String getValueByKeyAndSecretName(String key, String secretName) {
                    return "socialnetwork-private-key".equals(key) ? privateKeyB64 : publicKeyB64;
                }
            };
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA key generation failed in test config", e);
        }
    }
}
