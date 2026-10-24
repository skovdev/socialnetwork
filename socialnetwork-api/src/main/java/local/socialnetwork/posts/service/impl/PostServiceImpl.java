package local.socialnetwork.posts.service.impl;

import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.follows.service.FollowService;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;
import local.socialnetwork.posts.dto.http.request.UpdatePostRequestDto;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.posts.service.PostService;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.pagination.Pages;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.PostNotFoundException;
import local.socialnetwork.shared.exception.PostAccessDeniedException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import static local.socialnetwork.posts.config.PostsCacheConfig.POSTS_CACHE_NAME;

import java.time.Instant;

import java.util.UUID;

/**
 * Default implementation of {@link PostService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;
    private final FollowService followService;
    private final PostCacheReader postCacheReader;

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
        var post = postCacheReader.read(postId);
        return new PostResponse(post.id(), resolveAuthor(post.authorId()), post.content(),
                post.createdAt(), post.updatedAt(), 0L, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<PostResponse> getFeed(Pageable pageable) {
        return withAuthors(postRepository.findAllByOrderByCreatedAtDesc(Pages.withoutClientSort(pageable)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<PostResponse> getPostsByUsername(String username, Pageable pageable) {
        var author = userProfileService.findAuthorByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
        var page = postRepository.findByAuthorIdOrderByCreatedAtDesc(author.authUserId(), Pages.withoutClientSort(pageable));
        return page.map(post -> PostResponse.from(post, author.summary()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Slice<PostResponse> getFollowingFeed(UUID viewerId, Pageable pageable) {
        if (!followService.followsAnyone(viewerId)) {
            return getFeed(pageable);
        }
        return withAuthors(postRepository.findFollowingFeed(viewerId, Pages.withoutClientSort(pageable)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = POSTS_CACHE_NAME, key = "#postId")
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
    @CacheEvict(cacheNames = POSTS_CACHE_NAME, key = "#postId")
    public void deletePost(UUID authUserId, UUID postId) {
        var post = findPostOrThrow(postId);
        requireOwner(post, authUserId);
        postRepository.delete(post);
        log.info("Post {} deleted by auth user id: {}", postId, authUserId);
    }

    private Slice<PostResponse> withAuthors(Slice<Post> posts) {
        var authorIds = posts.getContent().stream().map(post -> post.getAuthor().getId()).distinct().toList();
        var authorsById = userProfileService.getAuthorSummaries(authorIds);
        return posts.map(post -> PostResponse.from(post, AuthorSummary.require(authorsById, post.getAuthor().getId())));
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

    private AuthorSummary resolveAuthor(UUID authUserId) {
        return userProfileService.getAuthorSummary(authUserId);
    }

}
