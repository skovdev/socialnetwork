package local.socialnetwork.core.cloud.aws.s3.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Configures the AWS S3 client and presigner used for avatar storage.
 */
@Configuration
@RequiredArgsConstructor
public class AWSS3ClientConfig {

    private final AwsCredentialsProvider credentialsProvider;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}