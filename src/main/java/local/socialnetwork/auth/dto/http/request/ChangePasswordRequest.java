package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for changing the authenticated user's password.
 */
@Schema(description = "Change password request")
public record ChangePasswordRequest(
        @Schema(description = "Current password for verification")
        @NotBlank String currentPassword,
        @Schema(description = "New password (12–128 characters; must contain uppercase, lowercase, digit, and special character)")
        @NotBlank
        @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String newPassword) {
}
