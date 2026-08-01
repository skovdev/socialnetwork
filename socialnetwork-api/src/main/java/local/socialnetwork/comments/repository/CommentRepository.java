package local.socialnetwork.comments.repository;

import local.socialnetwork.comments.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.CrudRepository;
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
    List<Comment> findByParentIdInOrderByCreatedAtAsc(Collection<UUID> parentIds);
}
