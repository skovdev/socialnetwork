package local.socialnetwork.profiles.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.shared.dto.response.ApiResponseDto;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.profiles.dto.http.response.UserProfileResponse;

import local.socialnetwork.profiles.service.UserProfileService;
import local.socialnetwork.profiles.service.AvatarStorageService;

import local.socialnetwork.shared.constant.VersionApi;

import local.socialnetwork.shared.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PagedModel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for viewing other users' public profiles and posts.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/users")
@Tag(name = "Users", description = "User profile endpoints")
public class UserProfileRestController {

    private final UserProfileService userProfileService;
    private final AvatarStorageService avatarStorageService;
    private final PostService postService;

    @Operation(summary = "Get user profile by username", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ApiResponseDto<UserProfileResponse> getProfile(
            @Parameter(description = "Username to retrieve") @PathVariable("username") String username) {
        var profile = userProfileService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
        var avatarUrl = avatarStorageService.presign(profile.getAvatarUrl());
        return ApiResponseDto.buildSuccessResponse(UserProfileResponse.from(profile, avatarUrl));
    }

    @Operation(summary = "Get posts authored by a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posts retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}/posts")
    public ApiResponseDto<PagedModel<PostResponse>> getPosts(
            @Parameter(description = "Username to retrieve posts for") @PathVariable("username") String username,
            Pageable pageable) {
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(postService.getPostsByUsername(username, pageable)));
    }

}
