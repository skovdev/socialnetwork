package local.socialnetwork.comments.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;
import local.socialnetwork.comments.dto.http.request.UpdateCommentRequestDto;

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
import java.util.UUID;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CommentRestControllerIT extends BaseIntegrationTest {

    private static final String POSTS_URL = "/api/v1/posts";
    private static final String COMMENTS_URL = "/api/v1/comments";

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
    private String postId;

    @BeforeEach
    void setUp() throws Exception {
        authorToken = createUserAndGetToken("author", "author@example.com", "Author One");
        otherUserToken = createUserAndGetToken("other", "other@example.com", "Other User");
        postId = createPostAndGetId(authorToken, "A post to comment on");
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

    private String createCommentAndGetId(String bearerToken, String postId, String content, String parentCommentId)
            throws Exception {
        var request = new CreateCommentRequestDto(content, parentCommentId == null ? null : UUID.fromString(parentCommentId));
        var response = mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
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
    void createComment_withValidContent_returns201WithCommentData() throws Exception {
        var request = new CreateCommentRequestDto("Nice post!", null);

        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content").value("Nice post!"))
                .andExpect(jsonPath("$.data.author.username").value("other"))
                .andExpect(jsonPath("$.data.parentCommentId").doesNotExist())
                .andExpect(jsonPath("$.data.createdAt").exists());
    }

    @Test
    void createComment_withBlankContent_returns400WithProblemJson() throws Exception {
        var request = new CreateCommentRequestDto("", null);

        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void createComment_whenUnauthenticated_returns401() throws Exception {
        var request = new CreateCommentRequestDto("Nice post!", null);

        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createComment_onMissingPost_returns404() throws Exception {
        var request = new CreateCommentRequestDto("Nice post!", null);

        mockMvc.perform(post(POSTS_URL + "/00000000-0000-0000-0000-000000000000/comments")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void createReply_withValidParent_returns201NestedUnderParent() throws Exception {
        var parentId = createCommentAndGetId(otherUserToken, postId, "Top-level comment", null);

        var replyRequest = new CreateCommentRequestDto("A reply", UUID.fromString(parentId));
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.parentCommentId").value(parentId));

        mockMvc.perform(get(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(parentId))
                .andExpect(jsonPath("$.data.content[0].replies[0].content").value("A reply"))
                .andExpect(jsonPath("$.data.content[0].replies[0].author.username").value("author"));
    }

    @Test
    void createReply_toAReply_returns400() throws Exception {
        var parentId = createCommentAndGetId(otherUserToken, postId, "Top-level comment", null);
        var replyId = createCommentAndGetId(authorToken, postId, "A reply", parentId);

        var nestedReplyRequest = new CreateCommentRequestDto("Nested reply", UUID.fromString(replyId));
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nestedReplyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_COMMENT_PARENT"));
    }

    @Test
    void createReply_withParentFromDifferentPost_returns400() throws Exception {
        var otherPostId = createPostAndGetId(authorToken, "Another post");
        var parentId = createCommentAndGetId(otherUserToken, otherPostId, "Comment on other post", null);

        var replyRequest = new CreateCommentRequestDto("Cross-post reply", UUID.fromString(parentId));
        mockMvc.perform(post(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_COMMENT_PARENT"));
    }

    @Test
    void getComments_returnsTopLevelCommentsOldestFirst() throws Exception {
        createCommentAndGetId(otherUserToken, postId, "First comment", null);
        createCommentAndGetId(otherUserToken, postId, "Second comment", null);

        mockMvc.perform(get(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("First comment"))
                .andExpect(jsonPath("$.data.content[1].content").value("Second comment"));
    }

    @Test
    void getComments_onMissingPost_returns404() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/00000000-0000-0000-0000-000000000000/comments")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("POST_NOT_FOUND"));
    }

    @Test
    void updateComment_byOwner_returns200WithUpdatedContent() throws Exception {
        var commentId = createCommentAndGetId(otherUserToken, postId, "Original content", null);
        var request = new UpdateCommentRequestDto("Edited content");

        mockMvc.perform(put(COMMENTS_URL + "/" + commentId)
                        .header("Authorization", "Bearer " + otherUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("Edited content"))
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    void updateComment_byNonOwner_returns403() throws Exception {
        var commentId = createCommentAndGetId(otherUserToken, postId, "Original content", null);
        var request = new UpdateCommentRequestDto("Edited content");

        mockMvc.perform(put(COMMENTS_URL + "/" + commentId)
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("COMMENT_ACCESS_DENIED"));
    }

    @Test
    void updateComment_whenMissing_returns404() throws Exception {
        var request = new UpdateCommentRequestDto("Edited content");

        mockMvc.perform(put(COMMENTS_URL + "/00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + authorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COMMENT_NOT_FOUND"));
    }

    @Test
    void deleteComment_byOwner_returns204() throws Exception {
        var commentId = createCommentAndGetId(otherUserToken, postId, "To be deleted", null);

        mockMvc.perform(delete(COMMENTS_URL + "/" + commentId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isNoContent());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void deleteComment_byNonOwner_returns403() throws Exception {
        var commentId = createCommentAndGetId(otherUserToken, postId, "Not yours", null);

        mockMvc.perform(delete(COMMENTS_URL + "/" + commentId)
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("COMMENT_ACCESS_DENIED"));
    }

    @Test
    void deleteComment_cascadesToReplies() throws Exception {
        var parentId = createCommentAndGetId(otherUserToken, postId, "Top-level comment", null);
        createCommentAndGetId(authorToken, postId, "A reply", parentId);

        mockMvc.perform(delete(COMMENTS_URL + "/" + parentId)
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isNoContent());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(POSTS_URL + "/" + postId + "/comments")
                        .header("Authorization", "Bearer " + authorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
