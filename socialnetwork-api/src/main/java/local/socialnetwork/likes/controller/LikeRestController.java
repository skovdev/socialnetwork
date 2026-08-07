package local.socialnetwork.likes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.likes.dto.http.response.LikeSummary;
import local.socialnetwork.likes.dto.http.response.LikeResponse;

import local.socialnetwork.likes.service.LikeService;

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

import java.util.UUID;

/**
 * REST controller for liking, unliking, and browsing the likes on a post.
 * All endpoints require a valid Bearer JWT token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1)
@Tag(name = "Likes", description = "Post like/unlike and browsing endpoints")
public class LikeRestController {

    private final LikeService likeService;

    /**
     * Likes a post on behalf of the currently authenticated user. Idempotent: liking an
     * already-liked post simply returns its current like summary.
     */
    @Operation(summary = "Like post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post liked (or already liked)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping("/posts/{postId}/likes")
    public ApiResponseDto<LikeSummary> likePost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") UUID postId) {
        return ApiResponseDto.buildSuccessResponse(likeService.likePost(principal.getId(), postId));
    }

    /**
     * Unlikes a post on behalf of the currently authenticated user. Idempotent: unliking a
     * not-liked post simply returns its current like summary.
     */
    @Operation(summary = "Unlike post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post unliked (or already not liked)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/posts/{postId}/likes")
    public ApiResponseDto<LikeSummary> unlikePost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") UUID postId) {
        return ApiResponseDto.buildSuccessResponse(likeService.unlikePost(principal.getId(), postId));
    }

    /**
     * Returns a page of users who liked a post, newest like first.
     */
    @Operation(summary = "Get likers", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Likers retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/posts/{postId}/likes")
    public ApiResponseDto<PagedModel<LikeResponse>> getLikers(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") UUID postId,
            Pageable pageable) {
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(likeService.getLikers(postId, pageable)));
    }
}
