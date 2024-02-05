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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import java.util.Optional;

import java.util.stream.Collectors;

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

    @Operation(summary = "User registration")
    @ApiResponses(value = {
            @ApiResponse(description = "User successfully registered", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "User already exists in database", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "400")
    })
    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> signUp(@Parameter(description = "This parameter represents designed for user registration")
                                             @RequestBody SignUpDto signUpDto) {
        Optional<AuthUser> authUser = authUserService.findByUsername(signUpDto.username());
        if (authUser.isPresent() && authUser.get().getUsername().equalsIgnoreCase(signUpDto.username())) {
            log.info("User '{}' with this username already exists in database", authUser.get().getUsername());
            return new ResponseEntity<>(authUser.get().getUsername() + " is exists in databases", HttpStatus.BAD_REQUEST);
        } else {
            authUserService.signUp(signUpDto);
            log.info("User signed up successfully");
            return new ResponseEntity<>(signUpDto.username() + " signed up", HttpStatus.OK);
        }
    }

    @Operation(summary = "User authentication and authorization")
    @ApiResponses(value = {
            @ApiResponse(description = "User successfully logged in", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "200"),
            @ApiResponse(description = "Authorization error", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE) }, responseCode = "401")
    })
    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Object, Object>> signIn(@Parameter(description = "This parameter represents designed for user authentication during the sign-in process")
                                                          @RequestBody SignInDto signInDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDto.username(), signInDto.password()));
            AuthUser authUser = authUserService.findByUsername(signInDto.username())
                    .orElseThrow(() -> new AuthenticationUserNotFoundException(signInDto.username() + " is not found"));
            List<String> roles = getRoles(authUser.getAuthRoles());
            String token = jwtTokenProvider.createToken(authUser.getId(), authUser.getUsername(), roles);
            Map<Object, Object> model = new HashMap<>();
            model.put("authUserId", authUser.getId());
            model.put("username", authUser.getUsername());
            model.put("token", token);
            log.info("User { id: {}, username: {} } successfully authenticated", authUser.getId(), authUser.getUsername());
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new AuthenticationUserException(e.getMessage());
        }
    }

    private List<String> getRoles(List<AuthRole> authRoles) {
        return authRoles.stream()
                .map(AuthRole::getAuthority)
                .collect(Collectors.toList());
    }

    @ExceptionHandler(AuthenticationUserException.class)
    public ResponseEntity<?> handlerAuthenticationException(RuntimeException e) {
        log.info("Authorization Error: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}