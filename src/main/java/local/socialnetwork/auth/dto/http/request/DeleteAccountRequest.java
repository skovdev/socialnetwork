package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for deleting the authenticated user's account.
 * The password is required as a confirmation step.
 */
@Schema(description = "Delete account request")
public record DeleteAccountRequest(
        @Schema(description = "Current password to confirm account deletion")
        @NotBlank String password) {
}
