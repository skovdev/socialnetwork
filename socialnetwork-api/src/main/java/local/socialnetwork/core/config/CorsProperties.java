package local.socialnetwork.core.config;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Immutable CORS configuration bound from the {@code cors.*} namespace.
 *
 * @param allowedOrigins list of allowed origins; defaults to {@code *} when not set
 */
@Validated
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        @NotEmpty List<String> allowedOrigins) {
}
