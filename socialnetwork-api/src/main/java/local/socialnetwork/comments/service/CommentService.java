package local.socialnetwork.comments.service;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;
import local.socialnetwork.comments.dto.http.request.UpdateCommentRequestDto;

import local.socialnetwork.comments.dto.http.response.CommentResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for comment operations.
 */
public interface CommentService {

    /**
     * Creates a new comment authored by {@code authUserId} on {@code postId}. If
     * {@code request.parentCommentId()} is set, the comment is created as a reply to that
     * top-level comment.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException      if no post exists with the given ID
     * @throws local.socialnetwork.shared.exception.CommentNotFoundException   if the parent comment does not exist
     * @throws local.socialnetwork.shared.exception.InvalidCommentParentException if the parent belongs to a
     *                                                                          different post or is itself a reply
     */
    CommentResponse createComment(UUID authUserId, UUID postId, CreateCommentRequestDto request);

    /**
     * Returns a page of top-level comments for {@code postId}, oldest first, with each
     * comment's replies nested inline.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    Page<CommentResponse> getComments(UUID postId, Pageable pageable);

    /**
     * Updates the content of the comment belonging to {@code commentId}, provided
     * {@code authUserId} is the author.
     *
     * @throws local.socialnetwork.shared.exception.CommentNotFoundException     if no comment exists with the given ID
     * @throws local.socialnetwork.shared.exception.CommentAccessDeniedException if {@code authUserId} is not the author
     */
    CommentResponse updateComment(UUID authUserId, UUID commentId, UpdateCommentRequestDto request);

    /**
     * Deletes the comment belonging to {@code commentId} (and any of its replies), provided
     * {@code authUserId} is the author.
     *
     * @throws local.socialnetwork.shared.exception.CommentNotFoundException     if no comment exists with the given ID
     * @throws local.socialnetwork.shared.exception.CommentAccessDeniedException if {@code authUserId} is not the author
     */
    void deleteComment(UUID authUserId, UUID commentId);
}
