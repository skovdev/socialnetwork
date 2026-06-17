package local.socialnetwork.profiles.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import local.socialnetwork.profiles.entity.FamilyStatus;

import java.time.LocalDate;

/**
 * Request payload for updating the authenticated user's profile.
 */
@Schema(description = "Profile update payload")
public record UpdateProfileRequestDto(
        @Schema(description = "Display name (required)") @NotBlank @Size(max = 100) String displayName,
        @Schema(description = "Short biography") @Size(max = 500) String bio,
        @Schema(description = "Avatar image URL") String avatarUrl,
        @Schema(description = "Date of birth") LocalDate birthDate,
        @Schema(description = "Phone number") String phoneNumber,
        @Schema(description = "Country of residence") String country,
        @Schema(description = "City of residence") String city,
        @Schema(description = "Street address") String address,
        @Schema(description = "Family / relationship status") FamilyStatus familyStatus) {
}
