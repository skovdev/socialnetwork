package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Per-IP throttle for unauthenticated endpoints that trigger email or token work.
 */
@Validated
@ConfigurationProperties(prefix = "socialnetwork.security.request-rate-limit")
public record RequestRateLimitProperties(
        @Positive int maxRequests,
        @NotNull Duration window) {
}
