package local.socialnetwork.core.cloud.aws.ses.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * Configures the AWS SES v2 client used to send transactional email.
 */
@Configuration
@RequiredArgsConstructor
public class AWSSesV2ClientConfig {

    private final AwsCredentialsProvider credentialsProvider;

    @Bean
    public SesV2Client sesV2Client() {
       return SesV2Client
                .builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
