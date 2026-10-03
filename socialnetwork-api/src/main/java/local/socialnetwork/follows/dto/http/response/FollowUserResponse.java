package local.socialnetwork.follows.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.follows.entity.Follow;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;

import java.util.UUID;

/**
 * A single row in a followers/following list: the row's user, when the follow relationship was
 * created, and whether the viewer already follows that user.
 */
@Schema(description = "A single row in a followers or following list")
public record FollowUserResponse(UUID id, AuthorSummary user, Instant followedAt, boolean followedByCurrentUser) {

    public static FollowUserResponse from(Follow follow, AuthorSummary user, boolean followedByCurrentUser) {
        if (follow == null) {
            throw new IllegalArgumentException("Follow must not be null");
        }
        return new FollowUserResponse(follow.getId(), user, follow.getCreatedAt(), followedByCurrentUser);
    }
}
