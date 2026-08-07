package local.socialnetwork.likes.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.likes.entity.Like;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;

import java.util.UUID;

/**
 * A single like on a post, identifying who liked it and when.
 */
@Schema(description = "A single like on a post")
public record LikeResponse(UUID id, AuthorSummary author, Instant createdAt) {

    public static LikeResponse from(Like like, AuthorSummary author) {
        if (like == null) {
            throw new IllegalArgumentException("Like must not be null");
        }
        return new LikeResponse(like.getId(), author, like.getCreatedAt());
    }
}
