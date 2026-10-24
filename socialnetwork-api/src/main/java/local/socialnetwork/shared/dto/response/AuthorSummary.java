package local.socialnetwork.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.shared.exception.UserNotFoundException;

import java.util.Map;
import java.util.UUID;

/**
 * Minimal public author info attached to user-generated content such as posts and comments.
 *
 * @param avatarUrl a presigned, publicly reachable avatar URL, or {@code null} if the author has no avatar
 */
@Schema(description = "Minimal public author info attached to user-generated content")
public record AuthorSummary(String username, String displayName, String avatarUrl) {

    /**
     * Looks up an author in a batch-loaded map.
     *
     * @throws UserNotFoundException if the author has no profile (content must always have a resolvable author)
     */
    public static AuthorSummary require(Map<UUID, AuthorSummary> authorsById, UUID authUserId) {
        var author = authorsById.get(authUserId);
        if (author == null) {
            throw new UserNotFoundException("Profile not found for user id: " + authUserId);
        }
        return author;
    }
}
