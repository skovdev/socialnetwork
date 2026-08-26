package local.socialnetwork.posts.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI-generated post content, returned for review. Not persisted as a post.
 */
@Schema(description = "AI-generated post content")
public record GeneratedPostContentResponse(@Schema(description = "Generated post text") String content) {
}
