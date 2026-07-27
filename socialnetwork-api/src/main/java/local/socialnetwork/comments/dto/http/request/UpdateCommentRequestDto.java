package local.socialnetwork.comments.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for updating an existing comment")
public record UpdateCommentRequestDto(
        @Schema(description = "Comment text content (required)") @NotBlank @Size(max = 2000) String content) {
}
