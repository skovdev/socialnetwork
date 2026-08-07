package local.socialnetwork.posts.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.likes.dto.http.response.LikeSummary;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;
import java.util.UUID;

/**
 * A single post, as returned to clients.
 * <p>
 * {@code likeCount}/{@code likedByCurrentUser} are viewer-specific and must never be trusted from
 * a cached instance of this record ({@link local.socialnetwork.posts.service.PostService#getPost}
 * is {@code @Cacheable} keyed only by post ID) — callers decorate with live values via
 * {@link #withLikeSummary(LikeSummary)} after retrieving the post.
 */
@Schema(description = "A single post")
public record PostResponse(
        UUID id,
        AuthorSummary author,
        String content,
        Instant createdAt,
        Instant updatedAt,
        long likeCount,
        boolean likedByCurrentUser) {

    public static PostResponse from(Post post, AuthorSummary author) {
        if (post == null) {
            throw new IllegalArgumentException("Post must not be null");
        }
        return new PostResponse(
                post.getId(), author, post.getContent(), post.getCreatedAt(), post.getUpdatedAt(), 0L, false);
    }

    /**
     * Returns a copy of this response with its like engagement fields replaced by {@code summary}.
     */
    public PostResponse withLikeSummary(LikeSummary summary) {
        return new PostResponse(id, author, content, createdAt, updatedAt, summary.count(), summary.likedByCurrentUser());
    }
}
