package local.socialnetwork.core.ai.impl;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.shared.exception.AiChatException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;

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
            throw e;
        } catch (RuntimeException e) {
            log.error("Error while communicating with AI model: {}", e.getMessage(), e);
            throw new AiChatException("Failed to communicate with AI model: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> chatList(String systemPrompt, String userPrompt) {
        validatePrompts(systemPrompt, userPrompt);
        try {
            var items = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(new ParameterizedTypeReference<List<String>>() {
                    });
            var cleaned = items == null ? List.<String>of() : items.stream()
                    .filter(Objects::nonNull)
                    .map(String::strip)
                    .filter(item -> !item.isEmpty())
                    .toList();
            if (cleaned.isEmpty()) {
                throw new AiChatException("AI model returned an empty list");
            }
            return cleaned;
        } catch (AiChatException e) {
            throw e;
        } catch (RuntimeException e) {
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
