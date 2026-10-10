package local.socialnetwork.follows.service.impl;

import local.socialnetwork.auth.entity.AuthUser;


import local.socialnetwork.follows.dto.http.response.FollowCounts;
import local.socialnetwork.follows.dto.http.response.FollowSummary;
import local.socialnetwork.follows.dto.http.response.FollowUserResponse;

import local.socialnetwork.follows.entity.Follow;

import local.socialnetwork.follows.repository.FollowRepository;

import local.socialnetwork.follows.service.FollowService;

import local.socialnetwork.profiles.dto.AuthorProfile;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.exception.SelfFollowException;
import local.socialnetwork.shared.exception.UserNotFoundException;

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
import java.util.HashSet;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserProfileService userProfileService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public FollowSummary follow(UUID followerId, String targetUsername) {
        var target = resolveTargetOrThrow(targetUsername);
        if (target.authUserId().equals(followerId)) {
            throw new SelfFollowException("Users cannot follow themselves");
        }
        var inserted = followRepository.insertIfAbsent(
                UUID.randomUUID(), followerId, target.authUserId(), Instant.now());
        if (inserted > 0) {
            log.info("Auth user {} followed auth user {}", followerId, target.authUserId());
        }
        return computeSummary(target.authUserId(), followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public FollowSummary unfollow(UUID followerId, String targetUsername) {
        var target = resolveTargetOrThrow(targetUsername);
        followRepository.deleteByFollowerIdAndFollowedId(followerId, target.authUserId());
        log.info("Auth user {} unfollowed auth user {}", followerId, target.authUserId());
        return computeSummary(target.authUserId(), followerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FollowSummary getSummary(String targetUsername, UUID viewerId) {
        var target = resolveTargetOrThrow(targetUsername);
        return computeSummary(target.authUserId(), viewerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FollowCounts getCounts(UUID authUserId) {
        return new FollowCounts(followRepository.countByFollowedId(authUserId), followRepository.countByFollowerId(authUserId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowers(String username, UUID viewerId, Pageable pageable) {
        var target = resolveTargetOrThrow(username);
        var page = followRepository.findByFollowedIdOrderByCreatedAtDesc(target.authUserId(), pageable);
        return hydrate(page, Follow::getFollower, viewerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowing(String username, UUID viewerId, Pageable pageable) {
        var target = resolveTargetOrThrow(username);
        var page = followRepository.findByFollowerIdOrderByCreatedAtDesc(target.authUserId(), pageable);
        return hydrate(page, Follow::getFollowed, viewerId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UUID> getFollowedAuthorIds(UUID followerId) {
        return followRepository.findFollowedIdsByFollowerId(followerId);
    }

    private AuthorProfile resolveTargetOrThrow(String username) {
        return userProfileService.findAuthorByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
    }

    private FollowSummary computeSummary(UUID targetId, UUID viewerId) {
        return new FollowSummary(
                followRepository.countByFollowedId(targetId),
                followRepository.countByFollowerId(targetId),
                followRepository.existsByFollowerIdAndFollowedId(viewerId, targetId));
    }

    private Page<FollowUserResponse> hydrate(Page<Follow> page, Function<Follow, AuthUser> rowUser, UUID viewerId) {
        var rowUserIds = page.getContent().stream().map(follow -> rowUser.apply(follow).getId()).distinct().toList();
        if (rowUserIds.isEmpty()) {
            return page.map(follow -> FollowUserResponse.from(follow, null, false));
        }
        var authorsById = userProfileService.getAuthorSummaries(rowUserIds);
        var alreadyFollowed = new HashSet<>(followRepository.findFollowedIdsByFollowerIdAndFollowedIdIn(viewerId, rowUserIds));
        return page.map(follow -> {
            var rowUserId = rowUser.apply(follow).getId();
            return FollowUserResponse.from(follow, toAuthorSummary(authorsById, rowUserId), alreadyFollowed.contains(rowUserId));
        });
    }

    private AuthorSummary toAuthorSummary(Map<UUID, AuthorSummary> authorsById, UUID authUserId) {
        var author = authorsById.get(authUserId);
        if (author == null) {
            throw new UserNotFoundException("Profile not found for user id: " + authUserId);
        }
        return author;
    }
}
