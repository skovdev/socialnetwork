package local.socialnetwork.authserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.Content;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.authserver.constant.VersionAPI;

import local.socialnetwork.authserver.dto.SignInDto;
import local.socialnetwork.authserver.dto.SignUpDto;
import local.socialnetwork.authserver.dto.ChangePasswordDto;

import local.socialnetwork.authserver.dto.authuser.AuthUserDto;
import local.socialnetwork.authserver.dto.authuser.AuthAdditionalTokenDataDto;

import local.socialnetwork.authserver.exception.AuthenticationUserException;
import local.socialnetwork.authserver.exception.AuthenticationUserNotFoundException;
import local.socialnetwork.authserver.exception.AuthenticationAdditionalTokenDataNotFoundException;

import local.socialnetwork.authserver.service.TokenService;
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

import java.util.Optional;

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

    final AuthenticationManager authenticationManager;
    final AuthUserService authUserService;
    final TokenService tokenService;

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
        Optional<AuthUserDto> authUser = authUserService.findByUsername(signUpDto.username());
        if (authUser.isPresent() && authUser.get().username().equalsIgnoreCase(signUpDto.username())) {
            log.info("User '{}' with this username already exists in database", authUser.get().username());
            return ResponseEntity.badRequest().body(authUser.get().username() + " is exists in databases");
        } else {
            authUserService.signUp(signUpDto);
            log.info("User '{}' signed up successfully", signUpDto.username());
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
            AuthUserDto authUser = authUserService.findByUsername(signInDto.username())
                    .orElseThrow(() -> new AuthenticationUserNotFoundException(signInDto.username() + " is not found"));
            log.info("User { id: {}, username: {} } successfully authenticated", authUser.id(), authUser.username());
            String token = authUserService.generateToken(authUser);
            tokenService.setToken(token);
            AuthAdditionalTokenDataDto authData = authUserService.findAdditionalTokenData(authUser.id())
                    .orElseThrow(() -> new AuthenticationAdditionalTokenDataNotFoundException("The authentication additional token data for username: '" + signInDto.username() + "' is not found"));
            String extendedToken = authUserService.extendToken(token, addAdditionalDataToToken(authData));
            return ResponseEntity.ok(createResponseModel(authUser, extendedToken));
        } catch (AuthenticationUserException | BadCredentialsException e) {
            log.info("Authorization error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Adds additional data to the token.
     *
     * @param authData The authentication data.
     * @return A map containing the additional data to be added to the token.
     */
    private Map<String, Object> addAdditionalDataToToken(AuthAdditionalTokenDataDto authData) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", authData.userId());
        data.put("profileId", authData.profileId());
        return data;
    }

    /**
     * Creates a response model containing the authentication user details and token.
     *
     * @param authUserDto The authentication user details.
     * @param token The JWT token generated for the authenticated user.
     * @return A map containing the authentication user details and token.
     */
    private Map<Object, Object> createResponseModel(AuthUserDto authUserDto, String token) {
        Map<Object, Object> model = new HashMap<>();
        model.put("authUserId", authUserDto.id());
        model.put("username", authUserDto.username());
        model.put("token", token);
        return model;
    }

    /**
     * Endpoint to find an auth user by username
     *
     * @param username The username of the auth user.
     * @return A ResponseEntity indicating the outcome of the auth user retrieval process.
     *         Returns OK (200) with the found auth user if the auth user exists,
     *         or NOT FOUND (404) if the auth user does not exist.
     */
    @Operation(summary = "Find the auth user by username")
    @ApiResponses(value = {
            @ApiResponse(description = "Return a found auth user", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Return error message if auth user does not exist", responseCode = "404")
    })
    @GetMapping(value = "/{username}")
    public ResponseEntity<?> findByUsername(@Parameter(description = "This parameter represents username of auth user") @PathVariable("username") String username) {
        return authUserService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Changes the password of the authenticated user.
     *
     * @param changePasswordDto Object containing the new password details.
     * @return ResponseEntity indicating the outcome of the password change process.
     *         Returns OK (200) if the password is successfully changed,
     *         or NOT FOUND (404) if the user is not found.
     */
    @Operation(summary = "Change the password of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(description = "Password successfully changed", responseCode = "200", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }),
            @ApiResponse(description = "User not found", responseCode = "404", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) })
    })
    @PutMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        boolean isChanged = authUserService.changePassword(changePasswordDto);
        return new ResponseEntity<>(isChanged, isChanged ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}