package local.socialnetwork.profiles.repository;

import local.socialnetwork.profiles.entity.UserProfile;

import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, UUID> {
    @EntityGraph(attributePaths = {"authUser", "authUser.authUserRoles"})
    Optional<UserProfile> findByUsername(String username);
    Optional<UserProfile> findByAuthUserId(UUID authUserId);
    boolean existsByUsername(String username);
}
