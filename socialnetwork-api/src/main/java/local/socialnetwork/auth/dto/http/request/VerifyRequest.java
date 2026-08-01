package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload used to verify an account via the token sent in the verification email.
 */
@Schema(description = "Email verification payload")
public record VerifyRequest(
        @Schema(description = "Raw verification token from the email link") @NotBlank String token) {
}
