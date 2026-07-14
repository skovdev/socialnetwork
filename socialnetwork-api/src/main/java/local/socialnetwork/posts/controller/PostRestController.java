package local.socialnetwork.posts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.dto.api.response.ApiResponseDto;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;
import local.socialnetwork.posts.dto.http.request.UpdatePostRequestDto;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.shared.constant.VersionApi;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PagedModel;

import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for post creation, browsing, and management.
 * All endpoints require a valid Bearer JWT token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1 + "/posts")
@Tag(name = "Posts", description = "Post creation, feed, and management endpoints")
public class PostRestController {

    private final PostService postService;

    /**
     * Creates a new post authored by the currently authenticated user.
     */
    @Operation(summary = "Create post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<PostResponse> createPost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "Post content") @RequestBody @Valid CreatePostRequestDto request) {
        return ApiResponseDto.buildSuccessResponse(postService.createPost(principal.getId(), request));
    }

    /**
     * Returns a page of posts ordered from newest to oldest.
     */
    @Operation(summary = "Get feed", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponseDto<PagedModel<PostResponse>> getFeed(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(postService.getFeed(pageable)));
    }

    /**
     * Returns a single post by ID.
     */
    @Operation(summary = "Get post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponseDto<PostResponse> getPost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, @PathVariable("id") UUID id) {
        return ApiResponseDto.buildSuccessResponse(postService.getPost(id));
    }

    /**
     * Updates a post's content. Only the author may update their own post.
     */
    @Operation(summary = "Update post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the author of the post"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ApiResponseDto<PostResponse> updatePost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") UUID id,
            @Parameter(description = "Post content") @RequestBody @Valid UpdatePostRequestDto request) {
        return ApiResponseDto.buildSuccessResponse(postService.updatePost(principal.getId(), id, request));
    }

    /**
     * Deletes a post. Only the author may delete their own post.
     */
    @Operation(summary = "Delete post", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the author of the post"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, @PathVariable("id") UUID id) {
        postService.deletePost(principal.getId(), id);
    }
}
