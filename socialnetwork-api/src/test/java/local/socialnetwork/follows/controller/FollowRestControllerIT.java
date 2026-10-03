package local.socialnetwork.follows.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.profiles.entity.UserProfile;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class FollowRestControllerIT extends BaseIntegrationTest {

    private static final String USERS_URL = "/api/v1/users";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    private String followerToken;
    private String targetToken;
    private String thirdUserToken;

    @BeforeEach
    void setUp() {
        followerToken = createUserAndGetToken("follower", "follower@example.com", "Follower One");
        targetToken = createUserAndGetToken("target", "target@example.com", "Target User");
        thirdUserToken = createUserAndGetToken("third", "third@example.com", "Third Person");
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
    void follow_returns200WithFollowedTrueAndIncrementedCounts() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(1))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(true));
    }

    @Test
    void follow_calledTwiceBySameUser_isIdempotent() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(1))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(true));
    }

    @Test
    void follow_self_returns400WithSelfFollowNotAllowedErrorCode() throws Exception {
        mockMvc.perform(post(USERS_URL + "/follower/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("SELF_FOLLOW_NOT_ALLOWED"));
    }

    @Test
    void follow_unknownUsername_returns404WithUserNotFoundErrorCode() throws Exception {
        mockMvc.perform(post(USERS_URL + "/nobody/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }

    @Test
    void follow_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unfollow_afterFollowing_removesAndReturns200WithFollowedFalse() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(delete(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(0))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(false));
    }

    @Test
    void unfollow_whenNeverFollowed_isIdempotentNoOp() throws Exception {
        mockMvc.perform(delete(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(0))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(false));
    }

    @Test
    void getFollowers_returnsFollowersNewestFirstWithFollowedByCurrentUserFlag() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + thirdUserToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();
        // The viewer (follower) also follows "third", so third's row should show followedByCurrentUser=true,
        // while the viewer's own row can never be self-followed and should show false.
        mockMvc.perform(post(USERS_URL + "/third/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(USERS_URL + "/target/followers")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].user.username").value("third"))
                .andExpect(jsonPath("$.data.content[0].followedByCurrentUser").value(true))
                .andExpect(jsonPath("$.data.content[1].user.username").value("follower"))
                .andExpect(jsonPath("$.data.content[1].followedByCurrentUser").value(false));
    }

    @Test
    void getFollowing_returnsFollowingNewestFirstWithFollowedByCurrentUserFlag() throws Exception {
        mockMvc.perform(post(USERS_URL + "/target/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();
        mockMvc.perform(post(USERS_URL + "/third/follow")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk());
        flushAndClearPersistenceContext();

        mockMvc.perform(get(USERS_URL + "/follower/following")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].user.username").value("third"))
                .andExpect(jsonPath("$.data.content[1].user.username").value("target"))
                .andExpect(jsonPath("$.data.content[0].followedByCurrentUser").value(true));
    }

    @Test
    void getFollowers_unknownUsername_returns404() throws Exception {
        mockMvc.perform(get(USERS_URL + "/nobody/followers")
                        .header("Authorization", "Bearer " + followerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }
}
