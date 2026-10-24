package local.socialnetwork.posts.service.impl;

import local.socialnetwork.posts.dto.CachedPost;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.shared.exception.PostNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static local.socialnetwork.posts.config.PostsCacheConfig.POSTS_CACHE_NAME;

/**
 * Separate bean so the {@link Cacheable} proxy is actually applied (a self-invocation inside
 * {@code PostServiceImpl} would bypass it).
 */
@Component
@RequiredArgsConstructor
public class PostCacheReader {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = POSTS_CACHE_NAME, key = "#postId")
    public CachedPost read(UUID postId) {
        return postRepository.findById(postId)
                .map(CachedPost::from)
                .orElseThrow(() -> new PostNotFoundException("Post not found for id: " + postId));
    }
}
