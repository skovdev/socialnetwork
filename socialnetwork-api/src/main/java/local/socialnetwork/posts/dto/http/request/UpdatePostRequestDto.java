package local.socialnetwork.posts.dto.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for updating an existing post")
public record UpdatePostRequestDto(
        @Schema(description = "Post text content (required)") @NotBlank @Size(max = 5000) String content) {
}
