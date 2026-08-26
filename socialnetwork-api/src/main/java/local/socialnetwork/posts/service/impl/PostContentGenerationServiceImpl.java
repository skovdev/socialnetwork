package local.socialnetwork.posts.service.impl;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.posts.dto.http.request.GeneratePostContentRequestDto;

import local.socialnetwork.posts.dto.http.response.GeneratedPostContentResponse;

import local.socialnetwork.posts.prompt.PostContentGenerationPrompts;

import local.socialnetwork.posts.service.PostContentGenerationService;

import local.socialnetwork.shared.exception.AiChatException;
import local.socialnetwork.shared.exception.PostContentGenerationException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Default implementation of {@link PostContentGenerationService}. Delegates the actual model call
 * to {@link AiChatService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostContentGenerationServiceImpl implements PostContentGenerationService {

    private final AiChatService aiChatService;

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneratedPostContentResponse generateContent(UUID authUserId, GeneratePostContentRequestDto request) {
        try {
            log.info("Generating post content requested by auth user id: {}", authUserId);
            var content = aiChatService.chat(PostContentGenerationPrompts.SYSTEM_PROMPT, request.topic());
            return new GeneratedPostContentResponse(content.strip());
        } catch (AiChatException e) {
            log.error("Failed to generate post content for auth user id '{}': {}", authUserId, e.getMessage(), e);
            throw new PostContentGenerationException("An error occurred while generating post content", e);
        }
    }
}
