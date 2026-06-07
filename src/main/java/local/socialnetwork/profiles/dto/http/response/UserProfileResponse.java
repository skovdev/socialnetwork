package local.socialnetwork.profiles.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.profiles.entity.UserProfile;

import java.time.LocalDate;

@Schema(description = "Public user profile")
public record UserProfileResponse(
        String username,
        String displayName,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDate birthDate,
        String country,
        String city) {

    public static UserProfileResponse from(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile must not be null");
        }
        return new UserProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBiography(),
                profile.getAvatarUrl(),
                profile.getBirthDate(),
                profile.getCountry(),
                profile.getCity()
        );
    }

}
