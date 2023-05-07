package local.socialnetwork.authserver.repository;

import local.socialnetwork.authserver.model.entity.AuthUser;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import java.util.UUID;

public interface AuthUserRepository extends CrudRepository<AuthUser, UUID> {
    Optional<AuthUser> findByUsername(String username);
}