package local.socialnetwork.auth.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AuthUserAccountRestControllerIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/auth";
    private static final String RAW_PASSWORD = "Secret1234";
    private static final String TEST_USERNAME = "accountuser";
    private static final String TEST_EMAIL = "account@example.com";

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String bearerToken;

    @BeforeEach
    void setUp() {
        var authUser = new AuthUser();
        authUser.setEmail(TEST_EMAIL);
        authUser.setPasswordHash(passwordEncoder.encode(RAW_PASSWORD));
        authUser.setAuthStatus(AuthStatus.ACTIVE);

        var role = new AuthUserRole();
        role.setAuthority("ROLE_USER");
        role.setAuthUser(authUser);
        authUser.setAuthUserRoles(Set.of(role));

        var profile = new UserProfile();
        profile.setUsername(TEST_USERNAME);
        profile.setFirstName("Account");
        profile.setLastName("User");
        profile.setDisplayName("Account User");
        profile.setBirthDate(LocalDate.of(1990, 1, 1));
        profile.setAuthUser(authUser);
        authUser.setUserProfile(profile);

        authUserRepository.save(authUser);

        bearerToken = jwtTokenProvider.createToken(Map.of("username", TEST_USERNAME));
    }

    // --- resend-verification ---

    @Test
    void resendVerification_whenAccountIsPendingVerification_returns200() throws Exception {
        var pendingUser = new AuthUser();
        pendingUser.setEmail("pending@example.com");
        pendingUser.setPasswordHash(passwordEncoder.encode(RAW_PASSWORD));
        pendingUser.setAuthStatus(AuthStatus.PENDING_VERIFICATION);

        var role = new AuthUserRole();
        role.setAuthority("ROLE_USER");
        role.setAuthUser(pendingUser);
        pendingUser.setAuthUserRoles(Set.of(role));

        var profile = new UserProfile();
        profile.setUsername("pendinguser");
        profile.setFirstName("Pending");
        profile.setLastName("User");
        profile.setDisplayName("Pending User");
        profile.setBirthDate(LocalDate.of(1990, 1, 1));
        profile.setAuthUser(pendingUser);
        pendingUser.setUserProfile(profile);

        authUserRepository.save(pendingUser);

        var request = new ResendVerificationRequest("pending@example.com");

        mockMvc.perform(post(BASE_URL + "/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void resendVerification_whenAccountAlreadyActive_returns409WithProblemJson() throws Exception {
        var request = new ResendVerificationRequest(TEST_EMAIL);

        mockMvc.perform(post(BASE_URL + "/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_ALREADY_VERIFIED"));
    }

    @Test
    void resendVerification_withUnknownEmail_returns404WithProblemJson() throws Exception {
        var request = new ResendVerificationRequest("nobody@example.com");

        mockMvc.perform(post(BASE_URL + "/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"));
    }

    @Test
    void resendVerification_withInvalidEmail_returns400WithProblemJson() throws Exception {
        var request = new ResendVerificationRequest("not-an-email");

        mockMvc.perform(post(BASE_URL + "/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    // --- change password ---

    @Test
    void changePassword_withValidCurrentPassword_returns204() throws Exception {
        var request = new ChangePasswordRequest(RAW_PASSWORD, "NewPassword99");

        mockMvc.perform(put(BASE_URL + "/password")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_withWrongCurrentPassword_returns400WithProblemJson() throws Exception {
        var request = new ChangePasswordRequest("WrongPassword", "NewPassword99");

        mockMvc.perform(put(BASE_URL + "/password")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CURRENT_PASSWORD"));
    }

    @Test
    void changePassword_withTooShortNewPassword_returns400WithProblemJson() throws Exception {
        var request = new ChangePasswordRequest(RAW_PASSWORD, "short");

        mockMvc.perform(put(BASE_URL + "/password")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void changePassword_whenUnauthenticated_returns401() throws Exception {
        var request = new ChangePasswordRequest(RAW_PASSWORD, "NewPassword99");

        mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // --- delete account ---

    @Test
    void deleteAccount_withCorrectPassword_returns204() throws Exception {
        var request = new DeleteAccountRequest(RAW_PASSWORD);

        mockMvc.perform(delete(BASE_URL + "/account")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAccount_withWrongPassword_returns400WithProblemJson() throws Exception {
        var request = new DeleteAccountRequest("WrongPassword");

        mockMvc.perform(delete(BASE_URL + "/account")
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CURRENT_PASSWORD"));
    }

    @Test
    void deleteAccount_whenUnauthenticated_returns401() throws Exception {
        var request = new DeleteAccountRequest(RAW_PASSWORD);

        mockMvc.perform(delete(BASE_URL + "/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
