package local.socialnetwork.auth.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import local.socialnetwork.auth.dto.http.request.LoginRequest;
import local.socialnetwork.auth.dto.http.request.VerifyRequest;
import local.socialnetwork.auth.dto.http.request.RefreshRequest;
import local.socialnetwork.auth.dto.http.request.RegisterRequest;
import local.socialnetwork.auth.dto.http.request.DeleteAccountRequest;
import local.socialnetwork.auth.dto.http.request.ChangePasswordRequest;
import local.socialnetwork.auth.dto.http.request.ResendVerificationRequest;

import local.socialnetwork.auth.dto.http.response.TokenResponse;

import local.socialnetwork.auth.service.AuthUserService;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.dto.api.response.ApiResponseDto;

import local.socialnetwork.shared.constant.VersionApi;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * REST controller for authentication and account lifecycle operations.
 * Public endpoints are explicitly permitted in {@code SecurityConfig}.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/auth")
@Tag(name = "Auth", description = "Authentication and authorization endpoints")
public class AuthUserRestController {

    private final AuthUserService authUserService;

    /**
     * Registers a new user account and triggers email verification.
     */
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered — verification email sent"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "503", description = "Email delivery failed")
    })
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<Void> register(@RequestBody @Valid RegisterRequest request) {
        authUserService.register(request);
        return ApiResponseDto.buildSuccessResponse(null);
    }

    /**
     * Verifies the user's email address using the token delivered via email.
     */
    @Operation(summary = "Verify email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verified — account activated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Token not found"),
            @ApiResponse(responseCode = "410", description = "Token expired or already used")
    })
    @PreAuthorize("permitAll()")
    @GetMapping("/verify")
    public ApiResponseDto<Void> verify(@NotBlank @RequestParam("token") String token) {
        authUserService.verify(new VerifyRequest(token));
        return ApiResponseDto.buildSuccessResponse(null);
    }

    /**
     * Authenticates a user and returns an access + refresh token pair.
     */
    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "Account not verified")
    })
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponseDto.buildSuccessResponse(authUserService.login(request));
    }

    /**
     * Rotates the refresh token and issues a new access + refresh token pair.
     */
    @Operation(summary = "Refresh access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens refreshed"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Refresh token not found"),
            @ApiResponse(responseCode = "410", description = "Refresh token expired")
    })
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto<TokenResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        return ApiResponseDto.buildSuccessResponse(authUserService.refresh(request));
    }

    /**
     * Invalidates all refresh tokens for the authenticated user, logging out all sessions.
     */
    @Operation(summary = "Logout — invalidate all sessions", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal principal) {
        authUserService.logout(principal.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Re-sends the verification email for an unverified account.
     * Always returns 200 regardless of whether the email is registered or already verified,
     * to prevent account enumeration.
     */
    @Operation(summary = "Resend verification email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request accepted — if the email is registered and unverified, a verification email has been sent"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "503", description = "Email delivery failed")
    })
    @PreAuthorize("permitAll()")
    @PostMapping(value = "/resend-verification", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponseDto<Void> resendVerification(@RequestBody @Valid ResendVerificationRequest request) {
        authUserService.resendVerification(request);
        return ApiResponseDto.buildSuccessResponse(null);
    }

    /**
     * Changes the password for the currently authenticated user.
     * All existing sessions are invalidated after a successful change.
     */
    @Operation(summary = "Change password", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed — all sessions invalidated"),
            @ApiResponse(responseCode = "400", description = "Validation error or wrong current password"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest request) {
        authUserService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permanently deletes the authenticated user's account and all associated data.
     * Password confirmation is required.
     */
    @Operation(summary = "Delete account", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted"),
            @ApiResponse(responseCode = "400", description = "Validation error or wrong password"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid DeleteAccountRequest request) {
        authUserService.deleteAccount(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

}
