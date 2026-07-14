package local.socialnetwork.posts.service;

import local.socialnetwork.posts.dto.http.request.CreatePostRequestDto;
import local.socialnetwork.posts.dto.http.request.UpdatePostRequestDto;

import local.socialnetwork.posts.dto.http.response.PostResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for post operations.
 */
public interface PostService {

    /**
     * Creates a new post authored by {@code authUserId}.
     */
    PostResponse createPost(UUID authUserId, CreatePostRequestDto request);

    /**
     * Finds a single post by ID.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException if no post exists with the given ID
     */
    PostResponse getPost(UUID postId);

    /**
     * Returns a page of posts ordered from newest to oldest.
     */
    Page<PostResponse> getFeed(Pageable pageable);

    /**
     * Updates the content of the post belonging to {@code postId}, provided {@code authUserId} is the author.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException     if no post exists with the given ID
     * @throws local.socialnetwork.shared.exception.PostAccessDeniedException if {@code authUserId} is not the author
     */
    PostResponse updatePost(UUID authUserId, UUID postId, UpdatePostRequestDto request);

    /**
     * Deletes the post belonging to {@code postId}, provided {@code authUserId} is the author.
     *
     * @throws local.socialnetwork.shared.exception.PostNotFoundException     if no post exists with the given ID
     * @throws local.socialnetwork.shared.exception.PostAccessDeniedException if {@code authUserId} is not the author
     */
    void deletePost(UUID authUserId, UUID postId);
}
