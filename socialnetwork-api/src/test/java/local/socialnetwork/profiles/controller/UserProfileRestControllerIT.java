package local.socialnetwork.profiles.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.profiles.entity.UserProfile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserProfileRestControllerIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/users";
    private static final String TEST_USERNAME = "alice";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String bearerToken;
    private String otherBearerToken;

    @BeforeEach
    void setUp() {
        var authUser = new AuthUser();
        authUser.setEmail("alice@example.com");
        authUser.setPasswordHash(passwordEncoder.encode("Secret1234"));
        authUser.setAuthStatus(AuthStatus.ACTIVE);

        var role = new AuthUserRole();
        role.setAuthority("ROLE_USER");
        role.setAuthUser(authUser);
        authUser.setAuthUserRoles(Set.of(role));

        var profile = new UserProfile();
        profile.setUsername(TEST_USERNAME);
        profile.setFirstName("Alice");
        profile.setLastName("Smith");
        profile.setDisplayName("Alice Smith");
        profile.setBirthDate(LocalDate.of(1995, 4, 10));
        profile.setAuthUser(authUser);
        authUser.setUserProfile(profile);

        authUserRepository.save(authUser);

        bearerToken = jwtTokenProvider.createToken(Map.of("username", TEST_USERNAME));

        var otherAuthUser = new AuthUser();
        otherAuthUser.setEmail("bob@example.com");
        otherAuthUser.setPasswordHash(passwordEncoder.encode("Secret1234"));
        otherAuthUser.setAuthStatus(AuthStatus.ACTIVE);

        var otherRole = new AuthUserRole();
        otherRole.setAuthority("ROLE_USER");
        otherRole.setAuthUser(otherAuthUser);
        otherAuthUser.setAuthUserRoles(Set.of(otherRole));

        var otherProfile = new UserProfile();
        otherProfile.setUsername("bob");
        otherProfile.setFirstName("Bob");
        otherProfile.setLastName("Jones");
        otherProfile.setDisplayName("Bob Jones");
        otherProfile.setAuthUser(otherAuthUser);
        otherAuthUser.setUserProfile(otherProfile);

        authUserRepository.save(otherAuthUser);

        otherBearerToken = jwtTokenProvider.createToken(Map.of("username", "bob"));
    }

    @Test
    void getProfile_whenAuthenticatedAndUsernameExists_returns200WithProfileData() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.firstName").value("Alice"))
                .andExpect(jsonPath("$.data.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.followerCount").value(0))
                .andExpect(jsonPath("$.data.followingCount").value(0))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(false));
    }

    @Test
    void getProfile_afterBeingFollowed_reflectsFollowerCountAndFollowedByCurrentUser() throws Exception {
        mockMvc.perform(post("/api/v1/users/" + TEST_USERNAME + "/follow")
                        .header("Authorization", "Bearer " + otherBearerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL + "/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + otherBearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(1))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(true));

        mockMvc.perform(get(BASE_URL + "/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.followerCount").value(1))
                .andExpect(jsonPath("$.data.followedByCurrentUser").value(false));
    }

    @Test
    void getProfile_doesNotExposePrivateFields() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + TEST_USERNAME)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.birthDate").doesNotExist())
                .andExpect(jsonPath("$.data.phoneNumber").doesNotExist())
                .andExpect(jsonPath("$.data.address").doesNotExist())
                .andExpect(jsonPath("$.data.familyStatus").doesNotExist());
    }

    @Test
    void getProfile_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + TEST_USERNAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProfile_withUnknownUsername_returns404WithProblemJson() throws Exception {
        mockMvc.perform(get(BASE_URL + "/nobody")
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }
}
