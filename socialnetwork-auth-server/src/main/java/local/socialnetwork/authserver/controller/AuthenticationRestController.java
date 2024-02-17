package local.socialnetwork.authserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.authserver.config.jwt.JwtTokenProvider;

import local.socialnetwork.authserver.constant.VersionAPI;

import local.socialnetwork.authserver.dto.SignInDto;
import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.exception.AuthenticationUserException;
import local.socialnetwork.authserver.exception.AuthenticationUserNotFoundException;

import local.socialnetwork.authserver.model.entity.AuthUser;
import local.socialnetwork.authserver.model.entity.AuthRole;

import local.socialnetwork.authserver.service.AuthUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import java.util.Optional;

import java.util.stream.Collectors;

/**
 * Handles the registration and authentication/authorization of users.
 * Provides endpoints for new user registration and existing user login,
 * issuing JWT tokens for successful authentication.
 */
@Tag(name = "AuthenticationRestController", description = "Controller for registration and authentication/authorization of users.")
@Slf4j
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping(VersionAPI.API_V1 + "/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    final AuthUserService authUserService;
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider jwtTokenProvider;

    /**
     * Registers a new user with the given sign-up details.
     * Validates if a user already exists with the provided username to prevent duplicates.
     *
     * @param signUpDto Object containing the sign-up details such as username and password.
     * @return ResponseEntity indicating the outcome of the registration process.
     *         Returns OK (200) with a success message if registration is successful,
     *         or BAD REQUEST (400) with an error message if the user already exists.
     */
    @Operation(summary = "User registration")
    @ApiResponses(value = {
            @ApiResponse(description = "User successfully registered", responseCode = "200"),
            @ApiResponse(description = "User already exists in database", responseCode = "400")
    })
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signUp(@Parameter(description = "This parameter represents designed for user registration")
                                             @RequestBody SignUpDto signUpDto) {
        Optional<AuthUser> authUser = authUserService.findByUsername(signUpDto.username());
        if (authUser.isPresent() && authUser.get().getUsername().equalsIgnoreCase(signUpDto.username())) {
            log.info("User '{}' with this username already exists in database", authUser.get().getUsername());
            return ResponseEntity.badRequest().body(authUser.get().getUsername() + " is exists in databases");
        } else {
            authUserService.signUp(signUpDto);
            log.info("User signed up successfully");
            return ResponseEntity.ok(signUpDto.username() + " signed up");
        }
    }

    /**
     * Authenticates a user with the provided sign-in credentials.
     * If authentication is successful, generates and returns a JWT token along with user details.
     *
     * @param signInDto Object containing the sign-in details such as username and password.
     * @return ResponseEntity containing the authentication token and user details if successful,
     *         or UNAUTHORIZED (401) status if authentication fails due to invalid credentials or other errors.
     * @throws AuthenticationUserNotFoundException if the user does not exist in the database.
     */
    @Operation(summary = "User authentication and authorization")
    @ApiResponses(value = {
            @ApiResponse(description = "User successfully logged in", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Authorization error", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "401")
    })
    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> signIn(@Parameter(description = "This parameter represents designed for user authentication during the sign-in process")
                                                          @RequestBody SignInDto signInDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDto.username(), signInDto.password()));
            AuthUser authUser = authUserService.findByUsername(signInDto.username())
                    .orElseThrow(() -> new AuthenticationUserNotFoundException(signInDto.username() + " is not found"));
            log.info("User { id: {}, username: {} } successfully authenticated", authUser.getId(), authUser.getUsername());
            return ResponseEntity.ok(createResponseModel(authUser));
        } catch (AuthenticationUserException | BadCredentialsException e) {
            log.info("Authorization error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Constructs the response model for a successfully authenticated user,
     * including user ID, username, and authentication token.
     *
     * @param authUser The authenticated user for whom the response model is being created.
     * @return A map representing the response model with user details and token.
     */
    private Map<Object, Object> createResponseModel(AuthUser authUser) {
        Map<Object, Object> model = new HashMap<>();
        model.put("authUserId", authUser.getId());
        model.put("username", authUser.getUsername());
        model.put("token", generateToken(authUser));
        return model;
    }

    /**
     * Generates a JWT token for the given user based on their ID, username, and roles.
     *
     * @param authUser The user for whom the token is being generated.
     * @return A JWT token string that represents the user's authentication and authorization information.
     */
    private String generateToken(AuthUser authUser) {
       return jwtTokenProvider.createToken(authUser.getId(), authUser.getUsername(), getRoles(authUser.getAuthRoles()));
    }

    /**
     * Extracts the role authorities from the user's roles.
     *
     * @param authRoles The list of roles associated with the user.
     * @return A list of strings representing the authorities of the user's roles.
     */
    private List<String> getRoles(List<AuthRole> authRoles) {
        return authRoles.stream()
                .map(AuthRole::getAuthority)
                .collect(Collectors.toList());
    }
}