package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload used to exchange a refresh token for a new access/refresh token pair.
 */
@Schema(description = "Token refresh payload")
public record RefreshRequest(
        @Schema(description = "Opaque refresh token (UUID) issued at login") @NotBlank String refreshToken) {
}
