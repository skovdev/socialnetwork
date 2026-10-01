package local.socialnetwork.follows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.follows.dto.http.response.FollowSummary;
import local.socialnetwork.follows.dto.http.response.FollowUserResponse;

import local.socialnetwork.follows.service.FollowService;

import local.socialnetwork.shared.dto.response.ApiResponseDto;

import local.socialnetwork.shared.constant.VersionApi;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PagedModel;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for following, unfollowing, and browsing a user's followers/following lists.
 * All endpoints require a valid Bearer JWT token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/users")
@Tag(name = "Follows", description = "Follow/unfollow and followers/following browsing endpoints")
public class FollowRestController {

    private final FollowService followService;

    /**
     * Follows a user on behalf of the currently authenticated user. Idempotent: following an
     * already-followed user simply returns its current follow summary.
     */
    @Operation(summary = "Follow user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User followed (or already followed)"),
            @ApiResponse(responseCode = "400", description = "Cannot follow yourself"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{username}/follow")
    public ApiResponseDto<FollowSummary> follow(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Username to follow") @PathVariable("username") String username) {
        return ApiResponseDto.buildSuccessResponse(followService.follow(principal.getId(), username));
    }

    /**
     * Unfollows a user on behalf of the currently authenticated user. Idempotent: unfollowing a
     * not-followed user simply returns its current follow summary.
     */
    @Operation(summary = "Unfollow user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User unfollowed (or already not followed)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{username}/follow")
    public ApiResponseDto<FollowSummary> unfollow(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Username to unfollow") @PathVariable("username") String username) {
        return ApiResponseDto.buildSuccessResponse(followService.unfollow(principal.getId(), username));
    }

    /**
     * Returns a page of users following the given user, newest follow first.
     */
    @Operation(summary = "Get followers", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Followers retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}/followers")
    public ApiResponseDto<PagedModel<FollowUserResponse>> getFollowers(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Username to retrieve followers for") @PathVariable("username") String username,
            Pageable pageable) {
        var followers = followService.getFollowers(username, principal.getId(), pageable);
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(followers));
    }

    /**
     * Returns a page of users the given user follows, newest follow first.
     */
    @Operation(summary = "Get following", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Following retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}/following")
    public ApiResponseDto<PagedModel<FollowUserResponse>> getFollowing(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Username to retrieve following for") @PathVariable("username") String username,
            Pageable pageable) {
        var following = followService.getFollowing(username, principal.getId(), pageable);
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(following));
    }
}
