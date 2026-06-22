package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for changing the authenticated user's password.
 */
@Schema(description = "Change password request")
public record ChangePasswordRequest(
        @Schema(description = "Current password for verification")
        @NotBlank String currentPassword,
        @Schema(description = "New password (8–64 characters)")
        @NotBlank @Size(min = 8, max = 64) String newPassword) {
}
