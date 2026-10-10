package local.socialnetwork.likes.repository;

import local.socialnetwork.likes.entity.Like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.time.Instant;

import java.util.List;
import java.util.UUID;
import java.util.Collection;

@Repository
public interface LikeRepository extends CrudRepository<Like, UUID>, PagingAndSortingRepository<Like, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO likes (id, post_id, author_id, created_at) VALUES (:id, :postId, :authorId, :createdAt)
            ON CONFLICT (post_id, author_id) DO NOTHING""", nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("postId") UUID postId,
                       @Param("authorId") UUID authorId, @Param("createdAt") Instant createdAt);
    boolean existsByPostIdAndAuthorId(UUID postId, UUID authorId);
    void deleteByPostIdAndAuthorId(UUID postId, UUID authorId);
    long countByPostId(UUID postId);
    Page<Like> findByPostIdOrderByCreatedAtDesc(UUID postId, Pageable pageable);
    List<Like> findByPostIdInAndAuthorId(Collection<UUID> postIds, UUID authorId);
    @Query("SELECT new local.socialnetwork.likes.repository.PostLikeCount(l.post.id, COUNT(l)) "
            + "FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<PostLikeCount> countByPostIdIn(@Param("postIds") Collection<UUID> postIds);
}
