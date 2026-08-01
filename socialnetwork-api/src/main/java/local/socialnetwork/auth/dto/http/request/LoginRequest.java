package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * Login credentials submitted to authenticate a user.
 */
@Schema(description = "Login credentials")
public record LoginRequest(
        @Schema(description = "Unique username") @NotBlank String username,
        @Schema(description = "Raw password") @NotBlank String password) {
}
