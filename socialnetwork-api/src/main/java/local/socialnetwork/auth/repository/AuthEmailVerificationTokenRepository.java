package local.socialnetwork.auth.repository;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.time.Instant;

import java.util.UUID;
import java.util.Optional;

/**
 * Repository for {@link AuthEmailVerificationToken} entities.
 */
@Repository
public interface AuthEmailVerificationTokenRepository extends CrudRepository<AuthEmailVerificationToken, UUID> {
    Optional<AuthEmailVerificationToken> findByToken(byte[] token);
    void deleteByAuthUser(AuthUser authUser);
    @Modifying
    @Query("delete from AuthEmailVerificationToken t where t.expiresAt < :cutoff")
    int deleteExpiredBefore(@Param("cutoff") Instant cutoff);
}
