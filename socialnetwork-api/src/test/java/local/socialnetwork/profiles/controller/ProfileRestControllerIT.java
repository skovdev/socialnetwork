package local.socialnetwork.profiles.controller;

import local.socialnetwork.BaseIntegrationTest;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthStatus;
import local.socialnetwork.auth.entity.AuthUserRole;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.core.config.jwt.JwtTokenProvider;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.security.crypto.password.PasswordEncoder;

import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ProfileRestControllerIT extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/v1/profiles";
    private static final String TEST_USERNAME = "meuser";
    private static final String PRESIGNED_URL = "https://test-avatar-bucket.s3.eu-north-1.amazonaws.com/avatars/test.png";

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
        authUser.setEmail("me@example.com");
        authUser.setPasswordHash(passwordEncoder.encode("Secret1234"));
        authUser.setAuthStatus(AuthStatus.ACTIVE);

        var role = new AuthUserRole();
        role.setAuthority("ROLE_USER");
        role.setAuthUser(authUser);
        authUser.setAuthUserRoles(new HashSet<>(Set.of(role)));

        var profile = new UserProfile();
        profile.setUsername(TEST_USERNAME);
        profile.setFirstName("Me");
        profile.setLastName("User");
        profile.setDisplayName("Me User");
        profile.setBirthDate(LocalDate.of(1992, 3, 20));
        profile.setAuthUser(authUser);
        authUser.setUserProfile(profile);

        authUserRepository.save(authUser);

        bearerToken = jwtTokenProvider.createToken(Map.of("username", TEST_USERNAME));
    }

    @Test
    void getProfile_whenAuthenticated_returns200WithProfileData() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.firstName").value("Me"))
                .andExpect(jsonPath("$.data.lastName").value("User"));
    }

    @Test
    void getProfile_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_withValidData_returns200WithUpdatedProfile() throws Exception {
        var request = new UpdateProfileRequestDto(
                "Updated Name", "My new bio",
                LocalDate.of(1992, 3, 20), null,
                "Germany", "Berlin", null, null);

        mockMvc.perform(put(BASE_URL)
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.displayName").value("Updated Name"))
                .andExpect(jsonPath("$.data.country").value("Germany"))
                .andExpect(jsonPath("$.data.city").value("Berlin"));
    }

    @Test
    void updateProfile_withBlankDisplayName_returns400WithProblemJson() throws Exception {
        var request = new UpdateProfileRequestDto(
                "", null, null, null, null, null, null, null);

        mockMvc.perform(put(BASE_URL)
                        .header("Authorization", "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void updateProfile_whenUnauthenticated_returns401() throws Exception {
        var request = new UpdateProfileRequestDto(
                "Name", null, null, null, null, null, null, null);

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadAvatar_withValidImage_returns200WithPresignedUrl() throws Exception {
        var file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());
        var presigned = presignedRequest();
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presigned);

        mockMvc.perform(multipart(BASE_URL + "/avatar").file(file)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl").value(PRESIGNED_URL));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void uploadAvatar_withUnsupportedFileType_returns422() throws Exception {
        var file = new MockMultipartFile("file", "avatar.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());

        mockMvc.perform(multipart(BASE_URL + "/avatar").file(file)
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_AVATAR_FILE"));
    }

    @Test
    void uploadAvatar_whenUnauthenticated_returns401() throws Exception {
        var file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());

        mockMvc.perform(multipart(BASE_URL + "/avatar").file(file))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAvatar_whenAvatarSet_returns204() throws Exception {
        var file = new MockMultipartFile("file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());
        var presigned = presignedRequest();
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presigned);
        mockMvc.perform(multipart(BASE_URL + "/avatar").file(file)
                .header("Authorization", "Bearer " + bearerToken));

        mockMvc.perform(delete(BASE_URL + "/avatar")
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNoContent());

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteAvatar_whenNoAvatarSet_returns404() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/avatar")
                        .header("Authorization", "Bearer " + bearerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("AVATAR_NOT_FOUND"));
    }

    private static PresignedGetObjectRequest presignedRequest() {
        var presigned = org.mockito.Mockito.mock(PresignedGetObjectRequest.class);
        try {
            when(presigned.url()).thenReturn(java.net.URI.create(PRESIGNED_URL).toURL());
        } catch (java.net.MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return presigned;
    }
}
