package local.socialnetwork.core.cloud.aws.secrets.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

/**
 * Configures the AWS Secrets Manager client bean.
 */
@Configuration
@RequiredArgsConstructor
public class AWSSecretsManagerConfig {

    private final AwsCredentialsProvider credentialsProvider;

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
