package local.socialnetwork.posts.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload for generating AI-drafted post content from a short topic or instruction.
 */
@Schema(description = "Payload for generating AI-drafted post content")
public record GeneratePostContentRequestDto(
        @Schema(description = "Short topic or instruction to base the generated post on (required)")
        @NotBlank @Size(max = 300) String topic) {
}
