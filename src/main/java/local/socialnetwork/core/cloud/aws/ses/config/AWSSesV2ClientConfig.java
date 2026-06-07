package local.socialnetwork.core.cloud.aws.ses.config;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class AWSSesV2ClientConfig {

    @Value("${aws.iam.user.accessKey}")
    private String accessKey;

    @Value("${aws.iam.user.secretKey}")
    private String secretKey;

    @Bean
    public SesV2Client sesV2Client() {
       return SesV2Client
                .builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}
