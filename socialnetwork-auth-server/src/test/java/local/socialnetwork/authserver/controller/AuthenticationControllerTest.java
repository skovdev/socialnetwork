package local.socialnetwork.authserver.controller;

import local.socialnetwork.authserver.SpringBootRunAuthServer;

import local.socialnetwork.authserver.dto.SignInDto;
import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.kafka.producer.user.UserProducer;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql({"/sql/authserver/drop_table_authuser_authserver.sql",
        "/sql/authserver/create_table_authuser_authserver.sql",
        "/sql/authserver/insert_data_authuser_authserver.sql"})
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = SpringBootRunAuthServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    // Existing auth user ID in the database
    private static final UUID AUTH_USER_ID = UUID.fromString("52721169-01f7-41ed-a8f3-683abf47765d");

    // Existing username in the database
    private static final String USERNAME = "testUsername";


    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private UserProducer userProducer;

    @Test
    public void shouldRegisterAuthNewUser() {

        Mockito.doNothing().when(userProducer).sendUserAndSave(Mockito.any(), Mockito.any(), Mockito.any());

        SignUpDto signUpDto = new SignUpDto(
                "testNewUsername",
                "testNewPassword",
                "testNewFirstname",
                "testNewLastname",
                "testNewCountry",
                "testNewCity",
                "testNewAddress",
                "0500000000",
                "01-01-2000",
                "testNewStatus");

        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/auth/sign-up"),
                signUpDto, String.class);

        assertEquals(signUpDto.username() + " signed up", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void shouldReturnBadRequestErrorIfAuthUserExists() {
        SignUpDto signUpDto = new SignUpDto(
                "testUsername",
                "testPassword",
                "testFirstname",
                "testLastname",
                "testCountry",
                "testCity",
                "testAddress",
                "0500000000",
                "01-01-2000",
                "testStatus");

        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/auth/sign-up"),
                signUpDto, String.class);

        assertEquals(USERNAME + " is exists in databases", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldLoginNewAuthUser() {

        SignInDto signInDto = new SignInDto("testUsername", "testPassword");

        ResponseEntity<Map<String, Object>> response = testRestTemplate.exchange(createURLWithPort("/auth/sign-in"),
                HttpMethod.POST,
                createHttpEntity(signInDto),
                new ParameterizedTypeReference<>() {});

        assertNotNull(response.getBody());
        assertEquals(AUTH_USER_ID, UUID.fromString(response.getBody().get("authUserId").toString()));
        assertEquals(USERNAME, response.getBody().get("username"));
        assertNotNull(response.getBody().get("token"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldReturnUnauthorizedErrorIfUserHasBadCredentials() {

        SignInDto signInDto = new SignInDto("testInvalidUserName", "testInvalidPassword");

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/auth/sign-in"),
                HttpMethod.POST,
                createHttpEntity(signInDto),
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    private <T> HttpEntity<T> createHttpEntity(T body) {
        return new HttpEntity<>(body, createHttpHeaders());
    }


    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/v1" + uri;
    }
}