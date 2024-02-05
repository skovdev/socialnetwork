package local.socialnetwork.integration.profileservice.controller;

import local.socialnetwork.profileservice.SpringBootRunProfileService;

import local.socialnetwork.profileservice.client.UserClient;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoEditDto;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.service.ProfileService;
import local.socialnetwork.profileservice.util.ResourceUtil;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.jdbc.Sql;

import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql({"/sql/profileservice/drop_table_profile_profileservice.sql",
        "/sql/profileservice/create_table_profile_profileservice.sql",
        "/sql/profileservice/insert_data_profileservice.sql"})
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = SpringBootRunProfileService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProfileControllerTest {

    // Existing profile in the database
    private final UUID profileId = UUID.fromString("662483d4-4df3-4d05-8a2a-9635e713b4ac");

    // Non-existent profile in the database
    private final UUID invalidProfileId = UUID.fromString("200ca21d-47a9-4079-989b-a098e006811b");

    // Existing user in the database
    private final UUID userId = UUID.fromString("8874f8ec-66ce-489d-a14f-3ad58f5daabe");

    // Non-existent user in the database
    private final UUID invalidUserId = UUID.fromString("62bf0caa-5148-48b3-9ecb-20ade7be138f");

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ResourceUtil resourceUtil;

    @Autowired
    private ProfileService profileService;

    @MockBean
    private UserClient userClient;

    @Test
    public void shouldReturnAllProfiles() {

        ResponseEntity<List<ProfileDto>> response = testRestTemplate.exchange(createURLWithPort("/profiles"),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), new ParameterizedTypeReference<>() {});

        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), 1);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void shouldReturnProfileById() {

        ResponseEntity<ProfileDto> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), ProfileDto.class);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().id().toString(), "662483d4-4df3-4d05-8a2a-9635e713b4ac");
        assertEquals(response.getBody().userId().toString(), "8874f8ec-66ce-489d-a14f-3ad58f5daabe");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(response.getBody().isActive());
        assertNotNull(response.getBody().avatar());

    }

    @Test
    public void shouldReturnNotFoundErrorIfProfileDoesNotExist() {
        
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + invalidProfileId),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertNotNull(response);
        assertEquals(response.getBody(), "Profile does not exist");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void shouldReturnProfileInfoByIdAndUserId() {

        Mockito.when(userClient.findUserByUserId(userId)).thenReturn(createUserDto());

        ResponseEntity<ProfileInfoDto> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/users/" + userId),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), ProfileInfoDto.class);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().id().toString(), "662483d4-4df3-4d05-8a2a-9635e713b4ac");
        assertEquals(response.getBody().firstName(), "test");
        assertEquals(response.getBody().lastName(), "test");
        assertEquals(response.getBody().country(), "test");
        assertEquals(response.getBody().city(), "test");
        assertEquals(response.getBody().address(), "test");
        assertEquals(response.getBody().phone(), "0500000000");
        assertEquals(response.getBody().birthDay(), "01-01-2023");
        assertEquals(response.getBody().familyStatus(), "IN_ACTIVE");
        assertTrue(response.getBody().isActive());
        assertNotNull(response.getBody().avatar());
        assertEquals(response.getBody().userId().toString(), "8874f8ec-66ce-489d-a14f-3ad58f5daabe");
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void shouldReturnNotFoundErrorIfProfileInfoDoesNotExist() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + invalidProfileId + "/users/" + invalidUserId),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertNotNull(response);
        assertEquals(response.getBody(), "There is no profile information");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void shouldReturnProfileInfoEditByIdAndUserId() {

        Mockito.when(userClient.findUserByUserId(userId)).thenReturn(createUserDto());

        ResponseEntity<ProfileInfoEditDto> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId +
                        "/users/" + userId + "/edit"), HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null),
                ProfileInfoEditDto.class);

        assertNotNull(response.getBody());
        assertEquals(response.getBody().firstName(), "test");
        assertEquals(response.getBody().lastName(), "test");
        assertEquals(response.getBody().country(), "test");
        assertEquals(response.getBody().city(), "test");
        assertEquals(response.getBody().address(), "test");
        assertEquals(response.getBody().phone(), "0500000000");
        assertEquals(response.getBody().birthDay(), "01-01-2023");
        assertEquals(response.getBody().familyStatus(), "IN_ACTIVE");
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void shouldReturnNotFoundErrorIfProfileInfoEditDoesNotExist() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + invalidProfileId +
                        "/users/" + invalidUserId + "/edit"), HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null),
                String.class);

        assertNotNull(response);
        assertEquals(response.getBody(), "There is no profile information to edit");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void shouldSaveNewProfile() {

        ProfileDto profileDto = new ProfileDto(
                null,
                true,
                resourceUtil.getEncodedResource("/avatar/default-avatar.jpg"),
                UUID.randomUUID());

        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/profiles"), profileDto, String.class);

        assertEquals("Profile is saved successfully", response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());


    }

    @Test
    public void shouldReturnErrorIfProfileIsNotCreated() {

        ProfileDto profileDto = new ProfileDto(
                null,
                true,
                resourceUtil.getEncodedResource("/avatar/default-avatar.jpg"),
                // Can't create profile because userId is null
                null);

        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/profiles"), profileDto, String.class);

        assertEquals("Profile is not saved", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void shouldReturnAvatarById() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/avatar"),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        String expectedEncodedAvatar = resourceUtil.getEncodedResource("/avatar/default-avatar.jpg");
        String actualEncodedAvatar = response.getBody();

        assertNotNull(response.getBody());
        assertEquals(expectedEncodedAvatar, actualEncodedAvatar);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void shouldReturnNotFoundErrorIfProfileAvatarDoesNotExist() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + invalidProfileId + "/avatar"),
                HttpMethod.GET, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertEquals("Profile avatar does not exist", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void shouldUpdateAvatarById() {

        Resource resource = new ClassPathResource("/avatar/test-avatar.jpg");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/avatar"),
                HttpMethod.PUT, createHttpEntity(MediaType.MULTIPART_FORM_DATA, body), String.class);

        assertEquals("Profile avatar is updated successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void shouldReturnBadRequestErrorIfPathUploadOfAvatarIsNull() {

        ReflectionTestUtils.setField(profileService, "pathUploadAvatar", null);

        Resource resource = new ClassPathResource("/avatar/test-avatar.jpg");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/avatar"),
                HttpMethod.PUT, createHttpEntity(MediaType.MULTIPART_FORM_DATA, body), String.class);

        assertEquals("Profile avatar is not updated", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void shouldDeleteCurrentAvatarAndSetDefaultAvatarById() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/avatar"),
                HttpMethod.DELETE, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertEquals("Profile avatar is deleted successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void shouldReturnBadRequestErrorIfDefaultPathOfAvatarIsNull() {

        ReflectionTestUtils.setField(profileService, "pathDefaultAvatar", null);

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/avatar"),
                HttpMethod.DELETE, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertEquals("Profile avatar is not deleted", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void shouldChangeStatus() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + profileId + "/status?isActive=true"),
                HttpMethod.PUT, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertEquals("Status is changed successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldNotChangeStatusIfProfileDoesNotExist() {

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/profiles/" + invalidProfileId + "/status?isActive=true"),
                HttpMethod.PUT, createHttpEntity(MediaType.APPLICATION_JSON, null), String.class);

        assertEquals("Status is not changed", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private <T> HttpEntity<T> createHttpEntity(MediaType mediaType, T body) {
        return new HttpEntity<>(body, createHttpHeaders(mediaType));
    }


    private HttpHeaders createHttpHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/v1" + uri;
    }

    private UserDto createUserDto() {
        return new UserDto(
                userId,
                "test",
                "test",
                "test",
                "test",
                "test",
                "0500000000",
                "01-01-2023",
                "IN_ACTIVE"
        );
    }
}