package local.socialnetwork.posts.service;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.posts.dto.http.request.GeneratePostContentRequestDto;

import local.socialnetwork.posts.prompt.PostContentGenerationPrompts;

import local.socialnetwork.posts.service.impl.PostContentGenerationServiceImpl;

import local.socialnetwork.shared.exception.AiChatException;
import local.socialnetwork.shared.exception.PostContentGenerationException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostContentGenerationServiceImplTest {

    @Mock
    private AiChatService aiChatService;

    @InjectMocks
    private PostContentGenerationServiceImpl service;

    @Test
    void generateContent_returnsTrimmedAiGeneratedContent() {
        var authUserId = UUID.randomUUID();
        var request = new GeneratePostContentRequestDto("my first marathon");
        when(aiChatService.chat(anyString(), eq("my first marathon")))
                .thenReturn("  Just finished my first marathon! Feeling proud and exhausted.  \n");

        var result = service.generateContent(authUserId, request);

        assertThat(result.content()).isEqualTo("Just finished my first marathon! Feeling proud and exhausted.");
    }

    @Test
    void generateContent_sendsPostContentGenerationSystemPromptAndTopicAsUserPrompt() {
        var authUserId = UUID.randomUUID();
        var request = new GeneratePostContentRequestDto("launching a new app");
        when(aiChatService.chat(anyString(), anyString())).thenReturn("Excited to launch my new app today!");

        service.generateContent(authUserId, request);

        var systemPromptCaptor = ArgumentCaptor.forClass(String.class);
        var userPromptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiChatService).chat(systemPromptCaptor.capture(), userPromptCaptor.capture());
        assertThat(systemPromptCaptor.getValue()).isEqualTo(PostContentGenerationPrompts.SYSTEM_PROMPT);
        assertThat(userPromptCaptor.getValue()).isEqualTo("launching a new app");
    }

    @Test
    void generateContent_whenAiChatServiceThrows_wrapsInPostContentGenerationException() {
        var authUserId = UUID.randomUUID();
        var request = new GeneratePostContentRequestDto("a topic");
        when(aiChatService.chat(anyString(), anyString())).thenThrow(new AiChatException("boom"));

        assertThatThrownBy(() -> service.generateContent(authUserId, request))
                .isInstanceOf(PostContentGenerationException.class)
                .hasCauseInstanceOf(AiChatException.class);
    }
}
