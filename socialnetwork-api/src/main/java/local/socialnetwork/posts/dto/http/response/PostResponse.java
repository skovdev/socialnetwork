package local.socialnetwork.posts.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;
import java.util.UUID;

/**
 * A single post, as returned to clients.
 */
@Schema(description = "A single post")
public record PostResponse(
        UUID id,
        AuthorSummary author,
        String content,
        Instant createdAt,
        Instant updatedAt) {

    public static PostResponse from(Post post, AuthorSummary author) {
        return new PostResponse(post.getId(), author, post.getContent(), post.getCreatedAt(), post.getUpdatedAt());
    }
}
