package local.socialnetwork.comments.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A single comment. Top-level comments carry their first few replies inline plus the total {@code replyCount};
 * the rest are paged via the replies endpoint.
 */
@Schema(description = "A single comment, with its first replies (if any) nested inline and the total reply count")
public record CommentResponse(
        UUID id,
        AuthorSummary author,
        String content,
        UUID parentCommentId,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> replies,
        long replyCount) {

    public static CommentResponse from(Comment comment, AuthorSummary author, List<CommentResponse> replies,
                                       long replyCount) {
        return new CommentResponse(
                comment.getId(),
                author,
                comment.getContent(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies,
                replyCount);
    }
}
