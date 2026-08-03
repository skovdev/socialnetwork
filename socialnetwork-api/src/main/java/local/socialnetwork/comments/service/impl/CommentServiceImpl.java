package local.socialnetwork.comments.service.impl;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;
import local.socialnetwork.comments.dto.http.request.UpdateCommentRequestDto;

import local.socialnetwork.comments.dto.http.response.CommentResponse;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.comments.repository.CommentRepository;

import local.socialnetwork.comments.service.CommentService;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.PostNotFoundException;
import local.socialnetwork.shared.exception.CommentNotFoundException;
import local.socialnetwork.shared.exception.CommentAccessDeniedException;
import local.socialnetwork.shared.exception.InvalidCommentParentException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import java.util.Map;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;

import java.util.stream.Stream;

/**
 * Default implementation of {@link CommentService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CommentResponse createComment(UUID authUserId, UUID postId, CreateCommentRequestDto request) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found for id: " + postId));
        var author = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + authUserId));

        var comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(request.content());
        comment.setCreatedAt(Instant.now());
        if (request.parentCommentId() != null) {
            comment.setParent(resolveParent(postId, request.parentCommentId()));
        }

        var saved = commentRepository.save(comment);
        log.info("Comment {} created by auth user id: {} on post: {}", saved.getId(), authUserId, postId);
        return CommentResponse.from(saved, resolveAuthor(authUserId), List.of());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(UUID postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found for id: " + postId);
        }

        var page = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId, pageable);
        var topLevelIds = page.getContent().stream().map(Comment::getId).toList();
        var repliesByParentId = commentRepository.findByParentIdInOrderByCreatedAtAsc(topLevelIds).stream()
                .collect(Collectors.groupingBy(reply -> reply.getParent().getId()));

        var authorIds = Stream.concat(
                        page.getContent().stream().map(comment -> comment.getAuthor().getId()),
                        repliesByParentId.values().stream().flatMap(List::stream)
                                .map(reply -> reply.getAuthor().getId()))
                .distinct()
                .toList();
        var authorsById = userProfileService.getAuthorSummaries(authorIds);

        return page.map(comment -> CommentResponse.from(
                comment,
                toAuthorSummary(authorsById, comment.getAuthor().getId()),
                repliesByParentId.getOrDefault(comment.getId(), List.of()).stream()
                        .map(reply -> CommentResponse.from(
                                reply, toAuthorSummary(authorsById, reply.getAuthor().getId()), List.of()))
                        .toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CommentResponse updateComment(UUID authUserId, UUID commentId, UpdateCommentRequestDto request) {
        var comment = findCommentOrThrow(commentId);
        requireOwner(comment, authUserId);
        comment.setContent(request.content());
        comment.setUpdatedAt(Instant.now());
        var saved = commentRepository.save(comment);
        log.info("Comment {} updated by auth user id: {}", commentId, authUserId);
        return CommentResponse.from(saved, resolveAuthor(authUserId), List.of());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteComment(UUID authUserId, UUID commentId) {
        var comment = findCommentOrThrow(commentId);
        requireOwner(comment, authUserId);
        commentRepository.delete(comment);
        log.info("Comment {} deleted by auth user id: {}", commentId, authUserId);
    }

    private Comment resolveParent(UUID postId, UUID parentCommentId) {
        var parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found for id: " + parentCommentId));
        if (!parent.getPost().getId().equals(postId)) {
            throw new InvalidCommentParentException(
                    "Comment " + parentCommentId + " does not belong to post " + postId);
        }
        if (parent.getParent() != null) {
            throw new InvalidCommentParentException(
                    "Comment " + parentCommentId + " is itself a reply and cannot have nested replies");
        }
        return parent;
    }

    private Comment findCommentOrThrow(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found for id: " + commentId));
    }

    private void requireOwner(Comment comment, UUID authUserId) {
        if (!comment.getAuthor().getId().equals(authUserId)) {
            throw new CommentAccessDeniedException(
                    "User " + authUserId + " is not the author of comment " + comment.getId());
        }
    }

    private AuthorSummary resolveAuthor(UUID authUserId) {
        return userProfileService.getAuthorSummary(authUserId);
    }

    private AuthorSummary toAuthorSummary(Map<UUID, AuthorSummary> authorsById, UUID authUserId) {
        var author = authorsById.get(authUserId);
        if (author == null) {
            throw new UserNotFoundException("Profile not found for user id: " + authUserId);
        }
        return author;
    }
}
