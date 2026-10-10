package local.socialnetwork.follows.service;

import local.socialnetwork.auth.entity.AuthUser;


import local.socialnetwork.follows.entity.Follow;

import local.socialnetwork.follows.repository.FollowRepository;

import local.socialnetwork.follows.service.impl.FollowServiceImpl;

import local.socialnetwork.profiles.dto.AuthorProfile;

import local.socialnetwork.profiles.service.UserProfileService;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import local.socialnetwork.shared.exception.SelfFollowException;
import local.socialnetwork.shared.exception.UserNotFoundException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;

import org.mockito.junit.jupiter.MockitoExtension;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private FollowServiceImpl followService;

    private AuthUser authUserWithId(UUID id) {
        var authUser = new AuthUser();
        authUser.setId(id);
        return authUser;
    }

    private AuthorProfile authorProfile(UUID authUserId, String username) {
        return new AuthorProfile(authUserId, new AuthorSummary(username, username, null));
    }

    @Test
    void follow_newFollow_insertsAndReturnsSummaryWithFollowedTrue() {
        var followerId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.insertIfAbsent(any(), eq(followerId), eq(targetId), any())).thenReturn(1);
        when(followRepository.existsByFollowerIdAndFollowedId(followerId, targetId)).thenReturn(true);
        when(followRepository.countByFollowedId(targetId)).thenReturn(1L);
        when(followRepository.countByFollowerId(targetId)).thenReturn(0L);

        var result = followService.follow(followerId, "target");

        assertThat(result.followerCount()).isEqualTo(1L);
        assertThat(result.followedByCurrentUser()).isTrue();
    }

    @Test
    void follow_alreadyFollowingOrConcurrent_isIdempotent_insertIsNoOp() {
        var followerId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.insertIfAbsent(any(), eq(followerId), eq(targetId), any())).thenReturn(0);
        when(followRepository.existsByFollowerIdAndFollowedId(followerId, targetId)).thenReturn(true);
        when(followRepository.countByFollowedId(targetId)).thenReturn(1L);
        when(followRepository.countByFollowerId(targetId)).thenReturn(0L);

        var result = followService.follow(followerId, "target");

        assertThat(result.followerCount()).isEqualTo(1L);
        verify(followRepository, never()).save(any());
    }

    @Test
    void follow_self_throwsSelfFollowException() {
        var userId = UUID.randomUUID();
        when(userProfileService.findAuthorByUsername("me")).thenReturn(Optional.of(authorProfile(userId, "me")));

        assertThatThrownBy(() -> followService.follow(userId, "me"))
                .isInstanceOf(SelfFollowException.class);
    }

    @Test
    void follow_targetUsernameNotFound_throwsUserNotFoundException() {
        when(userProfileService.findAuthorByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.follow(UUID.randomUUID(), "nobody"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void unfollow_afterFollowing_removesAndReturnsSummaryWithFollowedFalse() {
        var followerId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.countByFollowedId(targetId)).thenReturn(0L);
        when(followRepository.countByFollowerId(targetId)).thenReturn(0L);
        when(followRepository.existsByFollowerIdAndFollowedId(followerId, targetId)).thenReturn(false);

        var result = followService.unfollow(followerId, "target");

        verify(followRepository).deleteByFollowerIdAndFollowedId(followerId, targetId);
        assertThat(result.followedByCurrentUser()).isFalse();
    }

    @Test
    void unfollow_neverFollowed_isIdempotentNoOp() {
        var followerId = UUID.randomUUID();
        var targetId = UUID.randomUUID();

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.countByFollowedId(targetId)).thenReturn(0L);
        when(followRepository.countByFollowerId(targetId)).thenReturn(0L);
        when(followRepository.existsByFollowerIdAndFollowedId(followerId, targetId)).thenReturn(false);

        var result = followService.unfollow(followerId, "target");

        assertThat(result.followerCount()).isZero();
        verify(followRepository).deleteByFollowerIdAndFollowedId(followerId, targetId);
    }

    @Test
    void getSummary_returnsCountsAndFollowedFlag() {
        var targetId = UUID.randomUUID();
        var viewerId = UUID.randomUUID();
        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.countByFollowedId(targetId)).thenReturn(5L);
        when(followRepository.countByFollowerId(targetId)).thenReturn(2L);
        when(followRepository.existsByFollowerIdAndFollowedId(viewerId, targetId)).thenReturn(true);

        var result = followService.getSummary("target", viewerId);

        assertThat(result.followerCount()).isEqualTo(5L);
        assertThat(result.followingCount()).isEqualTo(2L);
        assertThat(result.followedByCurrentUser()).isTrue();
    }

    @Test
    void getCounts_returnsFollowerAndFollowingCounts() {
        var userId = UUID.randomUUID();
        when(followRepository.countByFollowedId(userId)).thenReturn(3L);
        when(followRepository.countByFollowerId(userId)).thenReturn(7L);

        var result = followService.getCounts(userId);

        assertThat(result.followerCount()).isEqualTo(3L);
        assertThat(result.followingCount()).isEqualTo(7L);
    }

    @Test
    void getFollowers_hydratesAuthorSummariesAndFollowedByCurrentUserFlag() {
        var targetId = UUID.randomUUID();
        var viewerId = UUID.randomUUID();
        var followerId = UUID.randomUUID();
        var pageable = Pageable.ofSize(20);

        var follow = new Follow();
        follow.setFollower(authUserWithId(followerId));
        follow.setFollowed(authUserWithId(targetId));

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.findByFollowedIdOrderByCreatedAtDesc(eq(targetId), any()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        var summary = new AuthorSummary("follower", "Follower Name", null);
        when(userProfileService.getAuthorSummaries(List.of(followerId))).thenReturn(Map.of(followerId, summary));
        when(followRepository.findFollowedIdsByFollowerIdAndFollowedIdIn(viewerId, List.of(followerId)))
                .thenReturn(List.of(followerId));

        Page<?> result = followService.getFollowers("target", viewerId, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getFollowing_hydratesAuthorSummariesAndFollowedByCurrentUserFlag() {
        var targetId = UUID.randomUUID();
        var viewerId = UUID.randomUUID();
        var followedId = UUID.randomUUID();

        var follow = new Follow();
        follow.setFollower(authUserWithId(targetId));
        follow.setFollowed(authUserWithId(followedId));

        when(userProfileService.findAuthorByUsername("target")).thenReturn(Optional.of(authorProfile(targetId, "target")));
        when(followRepository.findByFollowerIdOrderByCreatedAtDesc(eq(targetId), any()))
                .thenReturn(new PageImpl<>(List.of(follow)));
        var summary = new AuthorSummary("followed", "Followed Name", null);
        when(userProfileService.getAuthorSummaries(List.of(followedId))).thenReturn(Map.of(followedId, summary));
        when(followRepository.findFollowedIdsByFollowerIdAndFollowedIdIn(viewerId, List.of(followedId)))
                .thenReturn(List.of());

        var result = followService.getFollowing("target", viewerId, Pageable.ofSize(20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).followedByCurrentUser()).isFalse();
    }

    @Test
    void getFollowedAuthorIds_delegatesToRepository() {
        var followerId = UUID.randomUUID();
        var followedIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(followRepository.findFollowedIdsByFollowerId(followerId)).thenReturn(followedIds);

        var result = followService.getFollowedAuthorIds(followerId);

        assertThat(result).isEqualTo(followedIds);
    }
}
