package local.socialnetwork.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.profiles.entity.UserProfile;

@Schema(description = "Minimal public author info attached to user-generated content")
public record AuthorSummary(String username, String displayName, String avatarUrl) {

    /**
     * @param avatarUrl a presigned, publicly reachable avatar URL, or {@code null} if the author has no avatar
     */
    public static AuthorSummary from(UserProfile profile, String avatarUrl) {
        return new AuthorSummary(profile.getUsername(), profile.getDisplayName(), avatarUrl);
    }
}
