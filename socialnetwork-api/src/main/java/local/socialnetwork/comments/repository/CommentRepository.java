package local.socialnetwork.comments.repository;

import local.socialnetwork.comments.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Collection;

/**
 * Repository for {@link Comment} entities.
 */
@Repository
public interface CommentRepository extends CrudRepository<Comment, UUID>, PagingAndSortingRepository<Comment, UUID> {
    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(UUID postId, Pageable pageable);
    Page<Comment> findByParentIdOrderByCreatedAtAsc(UUID parentId, Pageable pageable);
    long countByParentId(UUID parentId);
    @Query(value = """
            SELECT t.id, t.post_id, t.author_id, t.parent_comment_id, t.content, t.version, t.created_at, t.updated_at
            FROM (SELECT c.*, ROW_NUMBER() OVER (PARTITION BY c.parent_comment_id ORDER BY c.created_at, c.id) AS rn
                  FROM comments c WHERE c.parent_comment_id IN (:parentIds)) t
            WHERE t.rn <= :limit
            ORDER BY t.created_at, t.id""", nativeQuery = true)
    List<Comment> findFirstRepliesByParentIdIn(@Param("parentIds") Collection<UUID> parentIds, @Param("limit") int limit);
    @Query("SELECT new local.socialnetwork.comments.repository.ReplyCount(c.parent.id, COUNT(c)) "
            + "FROM Comment c WHERE c.parent.id IN :parentIds GROUP BY c.parent.id")
    List<ReplyCount> countRepliesByParentIdIn(@Param("parentIds") Collection<UUID> parentIds);
    List<Comment> findByParentIdOrderByCreatedAtDesc(UUID parentId, Pageable pageable);
}
