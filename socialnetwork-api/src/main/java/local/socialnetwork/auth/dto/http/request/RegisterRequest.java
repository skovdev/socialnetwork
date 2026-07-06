package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import local.socialnetwork.shared.validation.ValidAge;

import java.time.LocalDate;

@Schema(description = "New user registration payload")
public record RegisterRequest(
        @Schema(description = "First name") @NotBlank String firstName,
        @Schema(description = "Last name") @NotBlank String lastName,
        @Schema(description = "Desired username (3–50 characters)") @NotBlank @Size(min = 3, max = 50) String username,
        @Schema(description = "Valid email address") @NotBlank @Email String email,
        @Schema(description = "Password (12–128 characters; must contain uppercase, lowercase, digit, and special character)")
        @NotBlank
        @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String password,
        @Schema(description = "Optional phone number") String phoneNumber,
        @Schema(description = "Date of birth (must be in the past; age 13–120)")
        @NotNull @Past @ValidAge LocalDate birthDate) {
}
