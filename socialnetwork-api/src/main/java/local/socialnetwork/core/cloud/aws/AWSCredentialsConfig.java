package local.socialnetwork.core.cloud.aws;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Single credentials source shared by every AWS client. Explicit keys win (handy locally); when none are
 * configured the SDK's default chain applies (environment, shared profile, SSO, instance/task role), so no
 * long-lived keys have to live in this application's configuration.
 */
@Configuration
public class AWSCredentialsConfig {

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${aws.iam.user.accessKey:}") String accessKey,
            @Value("${aws.iam.user.secretKey:}") String secretKey) {
        if (!accessKey.isBlank() && !secretKey.isBlank()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }
        return DefaultCredentialsProvider.builder().build();
    }
}
