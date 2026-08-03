package local.socialnetwork.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Minimal public author info attached to user-generated content such as posts and comments.
 *
 * @param avatarUrl a presigned, publicly reachable avatar URL, or {@code null} if the author has no avatar
 */
@Schema(description = "Minimal public author info attached to user-generated content")
public record AuthorSummary(String username, String displayName, String avatarUrl) {
}
