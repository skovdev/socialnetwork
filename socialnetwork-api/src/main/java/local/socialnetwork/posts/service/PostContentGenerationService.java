package local.socialnetwork.posts.service;

import local.socialnetwork.posts.dto.http.request.GeneratePostContentRequestDto;

import local.socialnetwork.posts.dto.http.response.GeneratedPostContentResponse;

import java.util.UUID;

/**
 * Service interface for AI-generated post content.
 */
public interface PostContentGenerationService {

    /**
     * Generates natural, clear post content from a short topic or instruction supplied by the
     * user. The result is returned for review and is never persisted as a post automatically.
     *
     * @throws local.socialnetwork.shared.exception.PostContentGenerationException if the AI provider fails to
     *                                                                             generate content
     */
    GeneratedPostContentResponse generateContent(UUID authUserId, GeneratePostContentRequestDto request);
}
