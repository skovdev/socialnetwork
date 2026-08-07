package local.socialnetwork.likes.service.impl;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.repository.AuthUserRepository;

import local.socialnetwork.likes.dto.http.response.LikeSummary;
import local.socialnetwork.likes.dto.http.response.LikeResponse;

import local.socialnetwork.likes.entity.Like;

import local.socialnetwork.likes.repository.PostLikeCount;
import local.socialnetwork.likes.repository.LikeRepository;

import local.socialnetwork.likes.service.LikeService;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import local.socialnetwork.posts.entity.Post;

import local.socialnetwork.posts.repository.PostRepository;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.exception.UserNotFoundException;
import local.socialnetwork.shared.exception.PostNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import java.util.Map;
import java.util.UUID;
import java.util.Collection;

import java.util.stream.Collectors;

/**
 * Default implementation of {@link LikeService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final AuthUserRepository authUserRepository;
    private final UserProfileService userProfileService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public LikeSummary likePost(UUID authUserId, UUID postId) {
        var post = findPostOrThrow(postId);
        if (!likeRepository.existsByPostIdAndAuthorId(postId, authUserId)) {
            var author = authUserRepository.findById(authUserId)
                    .orElseThrow(() -> new UserNotFoundException("User not found for id: " + authUserId));
            var like = createLike(post, author);
            try {
                likeRepository.save(like);
                log.info("Post {} liked by auth user id: {}", postId, authUserId);
            } catch (DataIntegrityViolationException ex) {
                log.debug("Post {} already liked by auth user id: {} (concurrent like)", postId, authUserId);
            }
        }
        return computeSummary(postId, authUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public LikeSummary unlikePost(UUID authUserId, UUID postId) {
        requirePostExists(postId);
        likeRepository.deleteByPostIdAndAuthorId(postId, authUserId);
        log.info("Post {} unliked by auth user id: {}", postId, authUserId);
        return computeSummary(postId, authUserId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LikeResponse> getLikers(UUID postId, Pageable pageable) {
        requirePostExists(postId);
        var page = likeRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        var authorIds = page.getContent().stream().map(like -> like.getAuthor().getId()).distinct().toList();
        var authorsById = userProfileService.getAuthorSummaries(authorIds);
        return page.map(like -> LikeResponse.from(like, toAuthorSummary(authorsById, like.getAuthor().getId())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public LikeSummary getSummary(UUID postId, UUID viewerId) {
        requirePostExists(postId);
        return computeSummary(postId, viewerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Map<UUID, LikeSummary> getSummaries(Collection<UUID> postIds, UUID viewerId) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        var countsByPostId = likeRepository.countByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(PostLikeCount::postId, PostLikeCount::count));
        var likedPostIds = likeRepository.findByPostIdInAndAuthorId(postIds, viewerId).stream()
                .map(like -> like.getPost().getId())
                .collect(Collectors.toSet());

        return postIds.stream().distinct().collect(Collectors.toMap(
                postId -> postId,
                postId -> new LikeSummary(countsByPostId.getOrDefault(postId, 0L), likedPostIds.contains(postId))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse decorate(PostResponse post, UUID viewerId) {
        return post.withLikeSummary(computeSummary(post.id(), viewerId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> decorate(Page<PostResponse> posts, UUID viewerId) {
        var summaries = getSummaries(posts.getContent().stream().map(PostResponse::id).toList(), viewerId);
        return posts.map(post -> post.withLikeSummary(summaries.get(post.id())));
    }

    private Like createLike(Post post, AuthUser author) {
        var like = new Like();
        like.setPost(post);
        like.setAuthor(author);
        like.setCreatedAt(Instant.now());
        return like;
    }

    private LikeSummary computeSummary(UUID postId, UUID viewerId) {
        return new LikeSummary(likeRepository.countByPostId(postId), likeRepository.existsByPostIdAndAuthorId(postId, viewerId));
    }

    private Post findPostOrThrow(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found for id: " + postId));
    }

    private void requirePostExists(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found for id: " + postId);
        }
    }

    private AuthorSummary toAuthorSummary(Map<UUID, AuthorSummary> authorsById, UUID authUserId) {
        var author = authorsById.get(authUserId);
        if (author == null) {
            throw new UserNotFoundException("Profile not found for user id: " + authUserId);
        }
        return author;
    }
}
