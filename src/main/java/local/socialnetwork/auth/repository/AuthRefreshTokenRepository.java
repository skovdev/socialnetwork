package local.socialnetwork.auth.repository;

import local.socialnetwork.auth.entity.AuthRefreshToken;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
    void deleteByUser_Id(UUID userId);

}
