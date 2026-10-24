package local.socialnetwork.comments.service.impl;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.comments.dto.http.request.CreateCommentRequestDto;
import local.socialnetwork.comments.dto.http.request.UpdateCommentRequestDto;

import local.socialnetwork.comments.dto.http.response.CommentResponse;

import local.socialnetwork.comments.entity.Comment;

import local.socialnetwork.comments.repository.ReplyCount;
import local.socialnetwork.comments.repository.CommentRepository;

import local.socialnetwork.comments.service.CommentService;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.pagination.Pages;

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

    private static final int INLINE_REPLY_LIMIT = 3;

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
        return CommentResponse.from(saved, resolveAuthor(authUserId), List.of(), 0L);
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

        var page = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId, Pages.withoutClientSort(pageable));
        var topLevelIds = page.getContent().stream().map(Comment::getId).toList();
        var repliesByParentId = commentRepository.findFirstRepliesByParentIdIn(topLevelIds, INLINE_REPLY_LIMIT).stream()
                .collect(Collectors.groupingBy(reply -> reply.getParent().getId()));
        var replyCounts = commentRepository.countRepliesByParentIdIn(topLevelIds).stream()
                .collect(Collectors.toMap(ReplyCount::parentId, ReplyCount::count));
        var authorIds = Stream.concat(
                        page.getContent().stream().map(comment -> comment.getAuthor().getId()),
                        repliesByParentId.values().stream().flatMap(List::stream)
                                .map(reply -> reply.getAuthor().getId()))
                .distinct()
                .toList();
        var authorsById = userProfileService.getAuthorSummaries(authorIds);
        return page.map(comment -> CommentResponse.from(
                comment,
                AuthorSummary.require(authorsById, comment.getAuthor().getId()),
                repliesByParentId.getOrDefault(comment.getId(), List.of()).stream()
                        .map(reply -> CommentResponse.from(
                                reply, AuthorSummary.require(authorsById, reply.getAuthor().getId()), List.of(), 0L))
                        .toList(),
                replyCounts.getOrDefault(comment.getId(), 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getReplies(UUID commentId, Pageable pageable) {
        var parent = findCommentOrThrow(commentId);
        if (parent.getParent() != null) {
            throw new InvalidCommentParentException("Comment " + commentId + " is itself a reply and has no replies");
        }
        var page = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId, Pages.withoutClientSort(pageable));
        var authorsById = userProfileService.getAuthorSummaries(
                page.getContent().stream().map(reply -> reply.getAuthor().getId()).distinct().toList());
        return page.map(reply -> CommentResponse.from(
                reply, AuthorSummary.require(authorsById, reply.getAuthor().getId()), List.of(), 0L));
    }

    @Override
    @Transactional
    public CommentResponse updateComment(UUID authUserId, UUID commentId, UpdateCommentRequestDto request) {
        var comment = findCommentOrThrow(commentId);
        requireOwner(comment, authUserId);
        comment.setContent(request.content());
        comment.setUpdatedAt(Instant.now());
        var saved = commentRepository.save(comment);
        log.info("Comment {} updated by auth user id: {}", commentId, authUserId);
        var replyCount = saved.getParent() == null ? commentRepository.countByParentId(commentId) : 0L;
        return CommentResponse.from(saved, resolveAuthor(authUserId), List.of(), replyCount);
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

}
