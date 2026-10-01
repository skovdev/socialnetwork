package local.socialnetwork.profiles.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.follows.dto.http.response.FollowSummary;

import local.socialnetwork.profiles.entity.UserProfile;

/**
 * A user's public profile, excluding private fields.
 */
@Schema(description = "Public user profile")
public record UserProfileResponse(
        String username,
        String displayName,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        String country,
        String city,
        long followerCount,
        long followingCount,
        boolean followedByCurrentUser) {

    public static UserProfileResponse from(UserProfile profile) {
        return from(profile, profile == null ? null : profile.getAvatarUrl());
    }

    /**
     * Builds a response with the given {@code avatarUrl} in place of the profile's stored value.
     * Used to substitute the raw S3 storage key with a presigned, browser-usable URL.
     */
    public static UserProfileResponse from(UserProfile profile, String avatarUrl) {
        return from(profile, avatarUrl, FollowSummary.empty());
    }

    /**
     * Builds a response with the given {@code avatarUrl} and the profile's live {@code summary}
     * of follow engagement as seen by the requesting user.
     */
    public static UserProfileResponse from(UserProfile profile, String avatarUrl, FollowSummary summary) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile must not be null");
        }
        return new UserProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBiography(),
                avatarUrl,
                profile.getCountry(),
                profile.getCity(),
                summary.followerCount(),
                summary.followingCount(),
                summary.followedByCurrentUser()
        );
    }

}
