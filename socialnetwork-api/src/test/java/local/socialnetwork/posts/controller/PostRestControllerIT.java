package local.socialnetwork.posts.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;
import local.socialnetwork.posts.dto.http.request.UpdatePostRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class PostRestControllerIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/posts";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authorToken;
    private String otherUserToken;

    @BeforeEach
    void setUp() {
        authorToken = createUserAndGetToken("author", "author@example.com", "Author One");
        otherUserToken = createUserAndGetToken("other", "other@example.com", "Other User");
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
        var response = mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.get("data").get("id").asText();
    }

    @Test
    void createPost_withValidContent_returns201WithPostData() throws Exception {
        var request = new CreatePostRequestDto("Hello world");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content").value("Hello world"))
                .andExpect(jsonPath("$.data.author.username").value("author"))
                .andExpect(jsonPath("$.data.author.displayName").value("Author One"))
                .andExpect(jsonPath("$.data.createdAt").exists());
    }

    @Test
    void createPost_withBlankContent_returns400WithProblemJson() throws Exception {
        var request = new CreatePostRequestDto("");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void createPost_whenUnauthenticated_returns401() throws Exception {
        var request = new CreatePostRequestDto("Hello world");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPost_whenExists_returns200WithPostData() throws Exception {
        var postId = createPostAndGetId(authorToken, "A single post");

        mockMvc.perform(get(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("A single post"));
    }

    @Test
    void getPost_whenMissing_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void getFeed_returnsPostsNewestFirst() throws Exception {
        createPostAndGetId(authorToken, "First post");
        createPostAndGetId(authorToken, "Second post");

        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("Second post"))
                .andExpect(jsonPath("$.data.content[1].content").value("First post"));
    }

    @Test
    void updatePost_byOwner_returns200WithUpdatedContent() throws Exception {
        var postId = createPostAndGetId(authorToken, "Original content");
        var request = new UpdatePostRequestDto("Edited content");

        mockMvc.perform(put(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("Edited content"))
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    void updatePost_byNonOwner_returns403() throws Exception {
        var postId = createPostAndGetId(authorToken, "Original content");
        var request = new UpdatePostRequestDto("Edited content");

        mockMvc.perform(put(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("POST_ACCESS_DENIED"));
    }

    @Test
    void deletePost_byOwner_returns204() throws Exception {
        var postId = createPostAndGetId(authorToken, "To be deleted");

        mockMvc.perform(delete(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_byNonOwner_returns403() throws Exception {
        var postId = createPostAndGetId(authorToken, "Not yours");

        mockMvc.perform(delete(BASE_URL + "/" + postId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("POST_ACCESS_DENIED"));
    }
}
