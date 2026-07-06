package local.socialnetwork.auth.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response body returned after a successful login or token refresh.
 *
 * @param accessToken  signed JWT for authenticating API requests
 * @param refreshToken opaque UUID used to obtain a new access token
 * @param expiresIn    access-token lifetime in seconds
 */
@Schema(description = "Authentication token pair")
public record TokenResponse(
        @Schema(description = "JWT access token") String accessToken,
        @Schema(description = "Opaque refresh token") String refreshToken,
        @Schema(description = "Access token lifetime in seconds") long expiresIn) {
}
