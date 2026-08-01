package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for outgoing account emails.
 */
@Validated
@ConfigurationProperties(prefix = "socialnetwork.email")
public record EmailProperties(
        @NotBlank String from,
        @NotBlank String verifyBaseUrl
) {}
