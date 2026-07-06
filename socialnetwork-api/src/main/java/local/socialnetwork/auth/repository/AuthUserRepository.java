package local.socialnetwork.auth.repository;

import local.socialnetwork.auth.entity.AuthUser;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends CrudRepository<AuthUser, UUID> {
    boolean existsByEmail(String email);
    Optional<AuthUser> findByEmail(String email);
}
