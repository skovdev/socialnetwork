package local.socialnetwork.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.dto.api.response.ApiResponseDto;

import local.socialnetwork.profiles.dto.http.response.UserProfileResponse;

import local.socialnetwork.profiles.service.UserProfileService;
import local.socialnetwork.profiles.service.AvatarStorageService;

import local.socialnetwork.shared.constant.VersionApi;

import local.socialnetwork.shared.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/users")
@Tag(name = "Users", description = "User profile endpoints")
public class UserProfileRestController {

    private final UserProfileService userProfileService;
    private final AvatarStorageService avatarStorageService;

    @Operation(summary = "Get user profile by username", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}")
    public ApiResponseDto<UserProfileResponse> getProfile(
            @Parameter(description = "Username to retrieve") @PathVariable("username") String username) {
        var profile = userProfileService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
        var avatarUrl = avatarStorageService.presign(profile.getAvatarUrl());
        return ApiResponseDto.buildSuccessResponse(UserProfileResponse.from(profile, avatarUrl));
    }

}
