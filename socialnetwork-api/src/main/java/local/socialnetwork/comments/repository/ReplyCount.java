package local.socialnetwork.comments.repository;

import java.util.UUID;

/**
 * Number of replies under one top-level comment.
 */
public record ReplyCount(UUID parentId, long count) {
}
