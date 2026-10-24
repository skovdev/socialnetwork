package local.socialnetwork.posts.repository;

import local.socialnetwork.posts.entity.Post;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Post} entities.
 */
@Repository
public interface PostRepository extends CrudRepository<Post, UUID>, PagingAndSortingRepository<Post, UUID> {
    Slice<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Slice<Post> findByAuthorIdOrderByCreatedAtDesc(UUID authorId, Pageable pageable);
    @Query(value = """
            SELECT p.* FROM posts p
            WHERE p.author_id = :viewerId
               OR p.author_id IN (SELECT f.followed_id FROM follows f WHERE f.follower_id = :viewerId)
            ORDER BY p.created_at DESC, p.id DESC""", nativeQuery = true)
    Slice<Post> findFollowingFeed(@Param("viewerId") UUID viewerId, Pageable pageable);
}
