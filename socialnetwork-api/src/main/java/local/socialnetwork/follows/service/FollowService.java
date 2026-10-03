package local.socialnetwork.follows.service;

import local.socialnetwork.follows.dto.http.response.FollowCounts;
import local.socialnetwork.follows.dto.http.response.FollowSummary;
import local.socialnetwork.follows.dto.http.response.FollowUserResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for follow/unfollow operations and followers/following browsing.
 */
public interface FollowService {

    /**
     * Makes {@code followerId} follow the user identified by {@code targetUsername}. Idempotent:
     * if already following, this is a no-op and returns the target's current follow summary.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no user exists with the given username
     * @throws local.socialnetwork.shared.exception.SelfFollowException   if {@code targetUsername} is the follower's own username
     */
    FollowSummary follow(UUID followerId, String targetUsername);

    /**
     * Makes {@code followerId} unfollow the user identified by {@code targetUsername}. Idempotent:
     * if not currently following, this is a no-op and returns the target's current follow summary.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no user exists with the given username
     */
    FollowSummary unfollow(UUID followerId, String targetUsername);

    /**
     * Returns the follow summary of the user identified by {@code targetUsername} as seen by {@code viewerId}.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no user exists with the given username
     */
    FollowSummary getSummary(String targetUsername, UUID viewerId);

    /**
     * Returns the follower/following counts for {@code authUserId}'s own profile.
     */
    FollowCounts getCounts(UUID authUserId);

    /**
     * Returns a page of users following the user identified by {@code username}, newest first.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no user exists with the given username
     */
    Page<FollowUserResponse> getFollowers(String username, UUID viewerId, Pageable pageable);

    /**
     * Returns a page of users the user identified by {@code username} follows, newest first.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no user exists with the given username
     */
    Page<FollowUserResponse> getFollowing(String username, UUID viewerId, Pageable pageable);

    /**
     * Returns the auth-user IDs that {@code followerId} follows. Used by the posts feature to
     * build a personalized, following-based feed.
     */
    List<UUID> getFollowedAuthorIds(UUID followerId);
}
