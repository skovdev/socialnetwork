package local.socialnetwork.auth.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Schema(description = "New user registration payload")
public record RegisterRequest(
        @Schema(description = "First name") @NotBlank String firstName,
        @Schema(description = "Last name") @NotBlank String lastName,
        @Schema(description = "Desired username (3-50 characters)") @NotBlank @Size(min = 3, max = 50) String username,
        @Schema(description = "Valid email address") @NotBlank @Email String email,
        @Schema(description = "Password (minimum 8 characters)") @NotBlank @Size(min = 8) String password,
        @Schema(description = "Optional phone number") String phoneNumber,
        @Schema(description = "Date of birth (must be in the past)") @NotNull @Past LocalDate birthDate) {
}
