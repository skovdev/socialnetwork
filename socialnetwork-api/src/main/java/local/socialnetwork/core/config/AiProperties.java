package local.socialnetwork.core.config;

import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Immutable AI safeguards bound from the {@code socialnetwork.ai} namespace.
 *
 * @param rateLimit per-user limit on AI-backed requests
 * @param timeout   HTTP timeouts applied to calls to the AI provider
 */
@Validated
@ConfigurationProperties(prefix = "socialnetwork.ai")
public record AiProperties(
        @Valid @NotNull RateLimit rateLimit,
        @Valid @NotNull Timeout timeout) {

    /**
     * @param maxRequests number of AI requests allowed per user within {@code window}
     * @param window      time window in which {@code maxRequests} is enforced
     */
    public record RateLimit(@Positive int maxRequests, @NotNull Duration window) {
    }

    /**
     * @param connect time allowed to establish a connection to the provider
     * @param read    time allowed to wait for the provider's response
     */
    public record Timeout(@NotNull Duration connect, @NotNull Duration read) {
    }
}
