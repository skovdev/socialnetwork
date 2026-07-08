package local.socialnetwork.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.dto.api.response.ApiResponseDto;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.dto.http.response.UserProfileResponse;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.service.UserProfileService;
import local.socialnetwork.profiles.service.AvatarStorageService;

import local.socialnetwork.shared.constant.VersionApi;

import local.socialnetwork.shared.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for the authenticated user's own profile.
 * All endpoints require a valid Bearer JWT token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/profiles")
@Tag(name = "Profile", description = "Authenticated user's own profile endpoints")
public class ProfileRestController {

    private final UserProfileService userProfileService;
    private final AvatarStorageService avatarStorageService;

    /**
     * Returns the profile of the currently authenticated user.
     */
    @Operation(summary = "Get profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponseDto<UserProfileResponse> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        var profile = userProfileService.findByAuthUserId(principal.getId())
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user: " + principal.getUsername()));
        return ApiResponseDto.buildSuccessResponse(toResponse(profile));
    }

    /**
     * Updates the profile of the currently authenticated user and returns the updated profile.
     */
    @Operation(summary = "Update profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ApiResponseDto<UserProfileResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Profile fields to update") @RequestBody @Valid UpdateProfileRequestDto request) {
        var updated = userProfileService.update(principal.getId(), request);
        return ApiResponseDto.buildSuccessResponse(toResponse(updated));
    }

    /**
     * Uploads a new avatar image for the currently authenticated user, replacing any existing one.
     */
    @Operation(summary = "Upload avatar", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar uploaded"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "422", description = "Invalid file type or size")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseDto<UserProfileResponse> uploadAvatar(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Avatar image file (JPEG, PNG, or WebP; max 5 MB)") @RequestParam("file") MultipartFile file) {
        var updated = userProfileService.updateAvatar(principal.getId(), file);
        return ApiResponseDto.buildSuccessResponse(toResponse(updated));
    }

    /**
     * Deletes the avatar of the currently authenticated user.
     */
    @Operation(summary = "Delete avatar", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Avatar deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Profile or avatar not found")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) {
        userProfileService.deleteAvatar(principal.getId());
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.from(profile, avatarStorageService.presign(profile.getAvatarUrl()));
    }
}
