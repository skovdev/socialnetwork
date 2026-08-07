package local.socialnetwork.likes.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class LikeRestControllerIT extends BaseIntegrationTest {

    private static final String POSTS_URL = "/api/v1/posts";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private String authorToken;
    private String otherUserToken;
    private String thirdUserToken;
    private String postId;

    @BeforeEach
    void setUp() throws Exception {
        authorToken = createUserAndGetToken("author", "author@example.com", "Author One");
        otherUserToken = createUserAndGetToken("other", "other@example.com", "Other User");
        thirdUserToken = createUserAndGetToken("third", "third@example.com", "Third Person");
        postId = createPostAndGetId(authorToken, "A post to like");
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

    /**
     * Each mockMvc call above joins the single per-test transaction, so entities created by one
     * "request" would otherwise stay managed in the same Hibernate session for the rest of the
     * test — unlike production, where each request gets a fresh persistence context. Flushing and
     * clearing after every write keeps the test faithful to that: subsequent requests re-read from
     * the database instead of reusing stale in-session object references.
     */
    private void flushAndClearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void likePost_returns200WithCountOneAndLikedTrue() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(true));
    }

    @Test
    void likePost_calledTwiceByTheSameUser_isIdempotent() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(true));
    }

    @Test
    void likePost_byMultipleUsers_accumulatesCount() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + thirdUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(2))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(true));
    }

    @Test
    void likePost_onMissingPost_returns404() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/00000000-0000-0000-0000-000000000000/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void likePost_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unlikePost_afterLiking_removesLikeAndReturnsSummary() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(delete(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(false));
    }

    @Test
    void unlikePost_whenNeverLiked_isIdempotentNoOp() throws Exception {
        mockMvc.perform(delete(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(false));
    }

    @Test
    void unlikePost_onMissingPost_returns404() throws Exception {
        mockMvc.perform(delete(POSTS_URL + "/00000000-0000-0000-0000-000000000000/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void getLikers_returnsLikersNewestFirst() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + thirdUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].author.username").value("third"))
                .andExpect(jsonPath("$.data.content[1].author.username").value("other"));
    }

    @Test
    void getLikers_onMissingPost_returns404() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/00000000-0000-0000-0000-000000000000/likes")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void getPost_reflectsLikeCountAndLikedByCurrentUserPerViewer() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(POSTS_URL + "/" + postId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(true));

        mockMvc.perform(get(POSTS_URL + "/" + postId)
                        .header("Authorization", "Bearer " + thirdUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1))
                .andExpect(jsonPath("$.data.likedByCurrentUser").value(false));
    }

    @Test
    void getFeed_reflectsLikeCountsPerPost() throws Exception {
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/likes")
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(POSTS_URL)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].likeCount").value(1))
                .andExpect(jsonPath("$.data.content[0].likedByCurrentUser").value(true));
    }
}
