package local.socialnetwork.posts.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.profiles.entity.UserProfile;

@Schema(description = "Minimal public author info attached to a post")
public record PostAuthorSummary(String username, String displayName) {

    public static PostAuthorSummary from(UserProfile profile) {
        return new PostAuthorSummary(profile.getUsername(), profile.getDisplayName());
    }
}
