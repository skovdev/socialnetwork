package local.socialnetwork.likes.repository;

import java.util.UUID;

/**
 * A post's like count, as produced by {@link LikeRepository#countByPostIdIn}.
 */
public record PostLikeCount(UUID postId, long count) {
}
