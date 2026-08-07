package local.socialnetwork.likes.dto.http.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A post's like engagement as seen by a particular viewer.
 *
 * @param count               total number of likes on the post
 * @param likedByCurrentUser  whether the requesting user has liked the post
 */
@Schema(description = "A post's like engagement as seen by the requesting user")
public record LikeSummary(long count, boolean likedByCurrentUser) {

    private static final LikeSummary EMPTY = new LikeSummary(0L, false);

    /**
     * The summary for a post with no likes, or one whose like data is not yet known
     * (e.g. right after creation).
     */
    public static LikeSummary empty() {
        return EMPTY;
    }
}
