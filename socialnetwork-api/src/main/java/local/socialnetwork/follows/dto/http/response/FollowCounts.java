package local.socialnetwork.follows.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Follower/following counts for the currently authenticated user's own profile, where a
 * viewer-relative "followed by current user" flag would be meaningless.
 *
 * @param followerCount  total number of users following this user
 * @param followingCount total number of users this user follows
 */
@Schema(description = "Follower/following counts for the authenticated user's own profile")
public record FollowCounts(long followerCount, long followingCount) {

    private static final FollowCounts EMPTY = new FollowCounts(0L, 0L);

    /**
     * The counts for a user with no follow data yet known.
     */
    public static FollowCounts empty() {
        return EMPTY;
    }
}
