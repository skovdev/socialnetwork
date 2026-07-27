package local.socialnetwork.comments.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "A single comment, with its replies (if any) nested inline")
public record CommentResponse(
        UUID id,
        AuthorSummary author,
        String content,
        UUID parentCommentId,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> replies) {

    public static CommentResponse from(Comment comment, AuthorSummary author, List<CommentResponse> replies) {
        return new CommentResponse(
                comment.getId(),
                author,
                comment.getContent(),
                comment.getParent() != null ? comment.getParent().getId() : null,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies);
    }
}
