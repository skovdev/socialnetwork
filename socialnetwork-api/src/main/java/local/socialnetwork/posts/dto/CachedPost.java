package local.socialnetwork.posts.dto;

import local.socialnetwork.posts.entity.Post;

import java.time.Instant;

import java.util.UUID;

/**
 * The part of a post that is safe to cache by post ID. It deliberately excludes author details
 * (display name, avatar URL) and like data, which change independently and are composed on read.
 */
public record CachedPost(UUID id, UUID authorId, String content, Instant createdAt, Instant updatedAt) {

    public static CachedPost from(Post post) {
        return new CachedPost(post.getId(), post.getAuthor().getId(), post.getContent(),
                post.getCreatedAt(), post.getUpdatedAt());
    }
}
