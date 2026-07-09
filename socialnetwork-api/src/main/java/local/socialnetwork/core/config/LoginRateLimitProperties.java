package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Immutable login brute-force protection configuration bound from the
 * {@code socialnetwork.security.login-rate-limit} namespace.
 *
 * @param maxAttempts number of failed login attempts allowed per username within {@code window}
 * @param window      sliding time window in which {@code maxAttempts} is enforced
 */
@Validated
@ConfigurationProperties(prefix = "socialnetwork.security.login-rate-limit")
public record LoginRateLimitProperties(
        @Positive int maxAttempts,
        @NotNull Duration window) {
}
