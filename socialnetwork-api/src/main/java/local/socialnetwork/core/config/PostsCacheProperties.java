package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Immutable posts cache configuration bound from the
 * {@code socialnetwork.cache.posts} namespace.
 *
 * @param ttl how long a cached post is retained before expiring
 */
@Validated
@ConfigurationProperties(prefix = "socialnetwork.cache.posts")
public record PostsCacheProperties(@NotNull Duration ttl) {
}
