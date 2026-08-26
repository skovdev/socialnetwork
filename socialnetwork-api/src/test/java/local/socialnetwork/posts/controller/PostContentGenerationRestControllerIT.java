package local.socialnetwork.posts.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.posts.dto.http.request.GeneratePostContentRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.shared.exception.AiChatException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class PostContentGenerationRestControllerIT extends BaseIntegrationTest {

    private static final String GENERATE_URL = "/api/v1/posts/generate";
    private static final String GENERATED_CONTENT = "Just finished my first marathon! Feeling proud and exhausted.";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AiChatService aiChatService;

    private String authorToken;

    @BeforeEach
    void setUp() {
        authorToken = createUserAndGetToken("author", "author@example.com", "Author One");
    }

    private String createUserAndGetToken(String username, String email, String displayName) {
        var authUser = new AuthUser();
        authUser.setEmail(email);
        authUser.setPasswordHash(passwordEncoder.encode("Secret1234"));
        authUser.setAuthStatus(AuthStatus.ACTIVE);

        var role = new AuthUserRole();
        role.setAuthority("ROLE_USER");
        role.setAuthUser(authUser);
        authUser.setAuthUserRoles(new HashSet<>(Set.of(role)));

        var profile = new UserProfile();
        profile.setUsername(username);
        profile.setFirstName(displayName.split(" ")[0]);
        profile.setLastName(displayName.split(" ")[1]);
        profile.setDisplayName(displayName);
        profile.setAuthUser(authUser);
        authUser.setUserProfile(profile);

        authUserRepository.save(authUser);

        return jwtTokenProvider.createToken(Map.of("username", username));
    }

    @Test
    void generatePostContent_withValidTopic_returns200WithGeneratedContent() throws Exception {
        when(aiChatService.chat(anyString(), anyString())).thenReturn(GENERATED_CONTENT);
        var request = new GeneratePostContentRequestDto("my first marathon");

        mockMvc.perform(post(GENERATE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value(GENERATED_CONTENT));
    }

    @Test
    void generatePostContent_withBlankTopic_returns400() throws Exception {
        var request = new GeneratePostContentRequestDto("  ");

        mockMvc.perform(post(GENERATE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void generatePostContent_withTopicTooLong_returns400() throws Exception {
        var request = new GeneratePostContentRequestDto("a".repeat(301));

        mockMvc.perform(post(GENERATE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void generatePostContent_whenUnauthenticated_returns401() throws Exception {
        var request = new GeneratePostContentRequestDto("my first marathon");

        mockMvc.perform(post(GENERATE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generatePostContent_whenAiProviderFails_returns503() throws Exception {
        doThrow(new AiChatException("boom")).when(aiChatService).chat(anyString(), anyString());
        var request = new GeneratePostContentRequestDto("my first marathon");

        mockMvc.perform(post(GENERATE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value("POST_CONTENT_GENERATION_FAILED"));
    }
}
