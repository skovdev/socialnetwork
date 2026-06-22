package local.socialnetwork.auth.repository;

import local.socialnetwork.auth.entity.AuthUser;
import local.socialnetwork.auth.entity.AuthEmailVerificationToken;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthEmailVerificationTokenRepository extends CrudRepository<AuthEmailVerificationToken, UUID> {
    Optional<AuthEmailVerificationToken> findByToken(byte[] token);
    void deleteByAuthUser(AuthUser authUser);
}
