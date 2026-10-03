package local.socialnetwork.profiles.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.follows.dto.http.response.FollowCounts;

import local.socialnetwork.profiles.entity.UserProfile;
import local.socialnetwork.profiles.entity.FamilyStatus;

import java.time.LocalDate;

/**
 * The authenticated user's own profile, including private fields.
 */
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
        FamilyStatus familyStatus,
        long followerCount,
        long followingCount) {

    public static MyProfileResponse from(UserProfile profile) {
        return from(profile, profile == null ? null : profile.getAvatarUrl());
    }

    /**
     * Builds a response with the given {@code avatarUrl} in place of the profile's stored value.
     * Used to substitute the raw S3 storage key with a presigned, browser-usable URL.
     */
    public static MyProfileResponse from(UserProfile profile, String avatarUrl) {
        return from(profile, avatarUrl, FollowCounts.empty());
    }

    /**
     * Builds a response with the given {@code avatarUrl} and the profile's live follower/following {@code counts}.
     */
    public static MyProfileResponse from(UserProfile profile, String avatarUrl, FollowCounts counts) {
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
                profile.getFamilyStatus(),
                counts.followerCount(),
                counts.followingCount()
        );
    }

}