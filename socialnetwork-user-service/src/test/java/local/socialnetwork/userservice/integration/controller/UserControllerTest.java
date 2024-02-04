package local.socialnetwork.userservice.integration.controller;

import local.socialnetwork.userservice.SpringBootRunUserService;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Sql({"/sql/userservice/drop_table_user_userservice.sql",
        "/sql/userservice/create_table_user_userservice.sql",
        "/sql/userservice/insert_data_userservice.sql"})
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = SpringBootRunUserService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    // Existing user in the database
    private final UUID userId = UUID.fromString("8874f8ec-66ce-489d-a14f-3ad58f5daabe");

    // Non-existent user in the database
    private final UUID invalidUserId = UUID.fromString("2405ae03-6b9b-4b57-af03-67902cd918b5");

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldReturnUserById() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<UserDto> response = testRestTemplate.exchange(createURLWithPort("/users/" + userId), HttpMethod.GET,
                httpEntity, UserDto.class);

        UserDto userDto = response.getBody();

        assertNotNull(userDto);
        assertEquals(userId, userDto.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void shouldReturnNotFoundErrorIfUserDoesNotExist() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/users/" + invalidUserId), HttpMethod.GET,
                httpEntity, String.class);

        assertEquals("User does not exist", response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + "/api/v1" + uri;
    }
}