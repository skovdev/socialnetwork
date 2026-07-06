package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for re-sending the account verification email.
 */
@Schema(description = "Resend verification email request")
public record ResendVerificationRequest(
        @Schema(description = "Email address of the unverified account")
        @NotBlank @Email String email) {
}
