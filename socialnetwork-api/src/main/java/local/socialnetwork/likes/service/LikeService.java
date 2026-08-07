package local.socialnetwork.likes.service;

import local.socialnetwork.likes.dto.http.response.LikeSummary;
import local.socialnetwork.likes.dto.http.response.LikeResponse;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;
import java.util.Collection;

/**
 * Service interface for post-like operations.
 */
public interface LikeService {

    /**
     * Likes {@code postId} on behalf of {@code authUserId}. Idempotent: if the user has already
     * liked the post, this is a no-op and returns the post's current like summary.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    LikeSummary likePost(UUID authUserId, UUID postId);

    /**
     * Unlikes {@code postId} on behalf of {@code authUserId}. Idempotent: if the user has not
     * liked the post, this is a no-op and returns the post's current like summary.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    LikeSummary unlikePost(UUID authUserId, UUID postId);

    /**
     * Returns a page of users who liked {@code postId}, newest like first.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    Page<LikeResponse> getLikers(UUID postId, Pageable pageable);

    /**
     * Returns {@code postId}'s current like summary as seen by {@code viewerId}.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    LikeSummary getSummary(UUID postId, UUID viewerId);

    /**
     * Batch variant of {@link #getSummary(UUID, UUID)} for rendering feeds/lists efficiently.
     * Post IDs are assumed to already reference existing posts; unknown IDs simply yield an
     * empty summary rather than an error.
     */
    Map<UUID, LikeSummary> getSummaries(Collection<UUID> postIds, UUID viewerId);

    /**
     * Returns {@code post} with its like engagement fields replaced by its live summary as seen
     * by {@code viewerId}.
     */
    PostResponse decorate(PostResponse post, UUID viewerId);

    /**
     * Batch variant of {@link #decorate(PostResponse, UUID)} for rendering feeds/lists
     * efficiently: fetches all summaries in a single query rather than one per post.
     */
    Page<PostResponse> decorate(Page<PostResponse> posts, UUID viewerId);
}
