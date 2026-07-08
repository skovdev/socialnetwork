package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "socialnetwork.aws.s3")
public record AWSS3Properties(
        @NotBlank String avatarBucketName,
        @NotNull Duration avatarPresignedUrlDuration
) {}