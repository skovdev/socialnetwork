package local.socialnetwork.comments.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

/**
 * Payload for creating a new comment or reply.
 */
@Schema(description = "Payload for creating a new comment or reply")
public record CreateCommentRequestDto(
        @Schema(description = "Comment text content (required)") @NotBlank @Size(max = 2000) String content,
        @Schema(description = "ID of the top-level comment this is a reply to (omit for a top-level comment)")
        UUID parentCommentId) {
}
