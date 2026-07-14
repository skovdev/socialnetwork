package local.socialnetwork.posts.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.posts.entity.Post;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A single post")
public record PostResponse(
        UUID id,
        PostAuthorSummary author,
        String content,
        Instant createdAt,
        Instant updatedAt) {

    public static PostResponse from(Post post, PostAuthorSummary author) {
        return new PostResponse(post.getId(), author, post.getContent(), post.getCreatedAt(), post.getUpdatedAt());
    }
}
