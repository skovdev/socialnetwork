package local.socialnetwork.follows.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A user's follow engagement as seen by a particular viewer.
 *
 * @param followerCount        total number of users following this user
 * @param followingCount       total number of users this user follows
 * @param followedByCurrentUser whether the requesting user follows this user
 */
@Schema(description = "A user's follow engagement as seen by the requesting user")
public record FollowSummary(long followerCount, long followingCount, boolean followedByCurrentUser) {

    private static final FollowSummary EMPTY = new FollowSummary(0L, 0L, false);

    /**
     * The summary for a user with no follow data yet known.
     */
    public static FollowSummary empty() {
        return EMPTY;
    }
}
