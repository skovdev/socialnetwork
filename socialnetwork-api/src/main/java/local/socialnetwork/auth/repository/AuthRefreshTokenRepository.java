package local.socialnetwork.auth.repository;

import local.socialnetwork.auth.entity.AuthRefreshToken;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

/**
 * Repository for {@link AuthRefreshToken} persistence.
 */
@Repository
public interface AuthRefreshTokenRepository extends CrudRepository<AuthRefreshToken, UUID> {

    /**
     * Finds a refresh token by its opaque JTI (JWT ID) value.
     *
     * @param jti the unique token identifier
     * @return the matching refresh token record, if present
     */
    Optional<AuthRefreshToken> findByJti(UUID jti);

    /**
     * Deletes all refresh tokens belonging to the given user, effectively logging out all sessions.
     *
     * @param userId the auth user's primary key
     */
    void deleteByUserId(UUID userId);

    /**
     * Atomically deletes a refresh token by its JTI and reports whether a row was removed.
     * Used instead of load-then-{@link #delete} so that two concurrent rotations of the same
     * token race safely: the loser sees 0 affected rows instead of Hibernate raising
     * {@code StaleObjectStateException} on a delete of an already-removed entity.
     *
     * @param jti the unique token identifier
     * @return the number of rows deleted (0 or 1)
     */
    @Modifying
    @Query("delete from AuthRefreshToken t where t.jti = :jti")
    int deleteByJti(@Param("jti") UUID jti);

}
