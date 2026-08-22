package local.socialnetwork.core.ai.impl;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.shared.exception.AiChatException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Spring AI-backed implementation of {@link AiChatService}. Delegates to {@link ChatClient},
 * which is wired to the OpenAI model configured via {@code spring.ai.openai.chat.options.model}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        validatePrompts(systemPrompt, userPrompt);

        try {
            var content = buildChatCall(systemPrompt, userPrompt);
            if (content == null || content.isBlank()) {
                throw new AiChatException("AI model returned an empty response");
            }
            return content;
        } catch (AiChatException e) {
            log.error("Error while communicating with AI model: {}", e.getMessage(), e);
            throw new AiChatException("Failed to communicate with AI model: " + e.getMessage(), e);
        }
    }

    private void validatePrompts(String ...prompts) {
        Arrays.stream(prompts)
                .filter(prompt -> prompt == null || prompt.isBlank())
                .findAny()
                .ifPresent(_ -> {
                    throw new AiChatException("Prompt cannot be null or empty");
        });
    }

    private String buildChatCall(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }
}
