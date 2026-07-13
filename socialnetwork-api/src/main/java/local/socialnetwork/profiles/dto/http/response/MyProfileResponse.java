package local.socialnetwork.profiles.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.profiles.entity.FamilyStatus;
import local.socialnetwork.profiles.entity.UserProfile;

import java.time.LocalDate;

@Schema(description = "The authenticated user's own profile, including private fields")
public record MyProfileResponse(
        String username,
        String displayName,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDate birthDate,
        String phoneNumber,
        String country,
        String city,
        String address,
        FamilyStatus familyStatus) {

    public static MyProfileResponse from(UserProfile profile) {
        return from(profile, profile == null ? null : profile.getAvatarUrl());
    }

    /**
     * Builds a response with the given {@code avatarUrl} in place of the profile's stored value.
     * Used to substitute the raw S3 storage key with a presigned, browser-usable URL.
     */
    public static MyProfileResponse from(UserProfile profile, String avatarUrl) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile must not be null");
        }
        return new MyProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBiography(),
                avatarUrl,
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getCountry(),
                profile.getCity(),
                profile.getAddress(),
                profile.getFamilyStatus()
        );
    }

}