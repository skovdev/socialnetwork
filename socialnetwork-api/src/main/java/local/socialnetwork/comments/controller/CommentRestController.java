package local.socialnetwork.comments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;
import local.socialnetwork.comments.dto.http.request.UpdateCommentRequestDto;

import local.socialnetwork.comments.dto.http.response.CommentResponse;

import local.socialnetwork.comments.service.CommentService;

import local.socialnetwork.core.config.security.principal.UserPrincipal;

import local.socialnetwork.dto.api.response.ApiResponseDto;

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
 * REST controller for comment creation, browsing, and management.
 * All endpoints require a valid Bearer JWT token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(VersionApi.API_V1)
@Tag(name = "Comments", description = "Comment creation, browsing, and management endpoints")
public class CommentRestController {

    private final CommentService commentService;

    /**
     * Creates a new comment (or reply) authored by the currently authenticated user.
     */
    @Operation(summary = "Create comment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid parent comment"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post or parent comment not found")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<CommentResponse> createComment(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") UUID postId,
            @Parameter(description = "Comment content") @RequestBody @Valid CreateCommentRequestDto request) {
        return ApiResponseDto.buildSuccessResponse(commentService.createComment(principal.getId(), postId, request));
    }

    /**
     * Returns a page of top-level comments for a post, oldest first, with replies nested inline.
     */
    @Operation(summary = "Get comments", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/{postId}/comments")
    public ApiResponseDto<PagedModel<CommentResponse>> getComments(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") UUID postId,
            Pageable pageable) {
        return ApiResponseDto.buildSuccessResponse(new PagedModel<>(commentService.getComments(postId, pageable)));
    }

    /**
     * Updates a comment's content. Only the author may update their own comment.
     */
    @Operation(summary = "Update comment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the author of the comment"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comments/{id}")
    public ApiResponseDto<CommentResponse> updateComment(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") UUID id,
            @Parameter(description = "Comment content") @RequestBody @Valid UpdateCommentRequestDto request) {
        return ApiResponseDto.buildSuccessResponse(commentService.updateComment(principal.getId(), id, request));
    }

    /**
     * Deletes a comment (and its replies). Only the author may delete their own comment.
     */
    @Operation(summary = "Delete comment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not the author of the comment"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, @PathVariable("id") UUID id) {
        commentService.deleteComment(principal.getId(), id);
    }
}
