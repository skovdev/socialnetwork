package local.socialnetwork.comments.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;

import local.socialnetwork.core.ai.AiChatService;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.shared.exception.AiChatException;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CommentReplySuggestionRestControllerIT extends BaseIntegrationTest {

    private static final String POSTS_URL = "/api/v1/posts";
    private static final String COMMENTS_URL = "/api/v1/comments";
    private static final String THREE_LINE_RESPONSE = """
            1. Thanks so much!
            2. Really appreciate that.
            3. Glad you liked it!""";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private AiChatService aiChatService;

    private String authorToken;
    private String commentId;

    @BeforeEach
    void setUp() throws Exception {
        authorToken = createUserAndGetToken("author", "author@example.com", "Author One");
        var postId = createPostAndGetId(authorToken, "A post to comment on");
        commentId = createCommentAndGetId(authorToken, postId, "Nice post!");
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

    private String createPostAndGetId(String bearerToken, String content) throws Exception {
        var request = new CreatePostRequestDto(content);
        var response = mockMvc.perform(post(POSTS_URL)
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        flushAndClearPersistenceContext();
        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("id").asText();
    }

    private String createCommentAndGetId(String bearerToken, String postId, String content) throws Exception {
        var request = new CreateCommentRequestDto(content, null);
        var response = mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        flushAndClearPersistenceContext();
        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("id").asText();
    }

    private void flushAndClearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void generateReplySuggestions_returns200WithThreeSuggestions() throws Exception {
        when(aiChatService.chat(anyString(), anyString())).thenReturn(THREE_LINE_RESPONSE);

        mockMvc.perform(post(COMMENTS_URL + "/" + commentId + "/suggestions")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("Thanks so much!"))
                .andExpect(jsonPath("$.data[2]").value("Glad you liked it!"));
    }

    @Test
    void generateReplySuggestions_withTone_returns200() throws Exception {
        when(aiChatService.chat(anyString(), anyString())).thenReturn(THREE_LINE_RESPONSE);

        mockMvc.perform(post(COMMENTS_URL + "/" + commentId + "/suggestions")
                        .header("Authorization", "Bearer " + authorToken)
                        .param("tone", "FRIENDLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    void generateReplySuggestions_withInvalidTone_returns400() throws Exception {
        mockMvc.perform(post(COMMENTS_URL + "/" + commentId + "/suggestions")
                        .header("Authorization", "Bearer " + authorToken)
                        .param("tone", "NOT_A_TONE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void generateReplySuggestions_onMissingComment_returns404() throws Exception {
        mockMvc.perform(post(COMMENTS_URL + "/00000000-0000-0000-0000-000000000000/suggestions")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COMMENT_NOT_FOUND"));
    }

    @Test
    void generateReplySuggestions_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post(COMMENTS_URL + "/" + commentId + "/suggestions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generateReplySuggestions_whenAiProviderFails_returns503() throws Exception {
        doThrow(new AiChatException("boom")).when(aiChatService).chat(anyString(), anyString());

        mockMvc.perform(post(COMMENTS_URL + "/" + commentId + "/suggestions")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value("COMMENT_SUGGESTION_GENERATION_FAILED"));
    }
}
