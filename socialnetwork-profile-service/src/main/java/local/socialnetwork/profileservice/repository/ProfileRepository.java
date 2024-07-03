package local.socialnetwork.profileservice.repository;

import local.socialnetwork.profileservice.entity.profile.Profile;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findProfileByUserId(UUID userId);
}