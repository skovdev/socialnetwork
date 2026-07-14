package local.socialnetwork.posts.service.impl;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;
import local.socialnetwork.posts.dto.http.request.UpdatePostRequestDto;

import local.socialnetwork.posts.dto.http.response.PostResponse;
import local.socialnetwork.posts.dto.http.response.PostAuthorSummary;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.profiles.repository.UserProfileRepository;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.PostNotFoundException;
import local.socialnetwork.shared.exception.PostAccessDeniedException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link PostService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PostResponse createPost(UUID authUserId, CreatePostRequestDto request) {
        var author = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + authUserId));
        var post = new Post();
        post.setAuthor(author);
        post.setContent(request.content());
        post.setCreatedAt(Instant.now());
        var saved = postRepository.save(post);
        log.info("Post {} created by auth user id: {}", saved.getId(), authUserId);
        return PostResponse.from(saved, resolveAuthor(authUserId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPost(UUID postId) {
        var post = findPostOrThrow(postId);
        return PostResponse.from(post, resolveAuthor(post.getAuthor().getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getFeed(Pageable pageable) {
        var page = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        var authorIds = page.getContent().stream().map(post -> post.getAuthor().getId()).distinct().toList();
        var authorsById = userProfileRepository.findByAuthUserIdIn(authorIds).stream()
                .collect(Collectors.toMap(profile -> profile.getAuthUser().getId(), Function.identity()));
        return page.map(post -> PostResponse.from(post, toAuthorSummary(authorsById, post.getAuthor().getId())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PostResponse updatePost(UUID authUserId, UUID postId, UpdatePostRequestDto request) {
        var post = findPostOrThrow(postId);
        requireOwner(post, authUserId);
        post.setContent(request.content());
        post.setUpdatedAt(Instant.now());
        var saved = postRepository.save(post);
        log.info("Post {} updated by auth user id: {}", postId, authUserId);
        return PostResponse.from(saved, resolveAuthor(authUserId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deletePost(UUID authUserId, UUID postId) {
        var post = findPostOrThrow(postId);
        requireOwner(post, authUserId);
        postRepository.delete(post);
        log.info("Post {} deleted by auth user id: {}", postId, authUserId);
    }

    private Post findPostOrThrow(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found for id: " + postId));
    }

    private void requireOwner(Post post, UUID authUserId) {
        if (!post.getAuthor().getId().equals(authUserId)) {
            throw new PostAccessDeniedException("User " + authUserId + " is not the author of post " + post.getId());
        }
    }

    private PostAuthorSummary resolveAuthor(UUID authUserId) {
        var profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found for user id: " + authUserId));
        return PostAuthorSummary.from(profile);
    }

    private PostAuthorSummary toAuthorSummary(Map<UUID, UserProfile> authorsById, UUID authUserId) {
        var profile = authorsById.get(authUserId);
        if (profile == null) {
            throw new UserNotFoundException("Profile not found for user id: " + authUserId);
        }
        return PostAuthorSummary.from(profile);
    }
}
