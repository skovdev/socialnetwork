package local.socialnetwork.likes.repository;

import local.socialnetwork.likes.entity.Like;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Collection;

/**
 * Repository for {@link Like} entities.
 */
@Repository
public interface LikeRepository extends CrudRepository<Like, UUID>, PagingAndSortingRepository<Like, UUID> {

    boolean existsByPostIdAndAuthorId(UUID postId, UUID authorId);

    void deleteByPostIdAndAuthorId(UUID postId, UUID authorId);

    long countByPostId(UUID postId);

    Page<Like> findByPostIdOrderByCreatedAtDesc(UUID postId, Pageable pageable);

    /**
     * Returns the subset of {@code postIds} that {@code authorId} has liked.
     */
    List<Like> findByPostIdInAndAuthorId(Collection<UUID> postIds, UUID authorId);

    /**
     * Batch like counts, grouped by post, for the given posts.
     */
    @Query("SELECT new local.socialnetwork.likes.repository.PostLikeCount(l.post.id, COUNT(l)) "
            + "FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<PostLikeCount> countByPostIdIn(@Param("postIds") Collection<UUID> postIds);
}
