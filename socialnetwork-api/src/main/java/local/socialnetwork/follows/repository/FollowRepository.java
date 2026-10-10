package local.socialnetwork.follows.repository;

import local.socialnetwork.follows.entity.Follow;

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
public interface FollowRepository extends CrudRepository<Follow, UUID>, PagingAndSortingRepository<Follow, UUID> {
    @Modifying
    @Query(value = """
        INSERT INTO follows (id, follower_id, followed_id, created_at)
        VALUES (:id, :followerId, :followedId, :createdAt)
        ON CONFLICT (follower_id, followed_id) DO NOTHING""", nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("followerId") UUID followerId,
                       @Param("followedId") UUID followedId, @Param("createdAt") Instant createdAt);
    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
    long countByFollowedId(UUID followedId);
    long countByFollowerId(UUID followerId);
    Page<Follow> findByFollowedIdOrderByCreatedAtDesc(UUID followedId, Pageable pageable);
    Page<Follow> findByFollowerIdOrderByCreatedAtDesc(UUID followerId, Pageable pageable);
    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :followerId")
    List<UUID> findFollowedIdsByFollowerId(@Param("followerId") UUID followerId);
    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id IN :followedIds")
    List<UUID> findFollowedIdsByFollowerIdAndFollowedIdIn(
            @Param("followerId") UUID followerId, @Param("followedIds") Collection<UUID> followedIds);
}
