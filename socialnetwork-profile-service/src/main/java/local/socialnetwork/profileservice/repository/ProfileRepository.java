package local.socialnetwork.profileservice.repository;

import local.socialnetwork.profileservice.entity.profile.Profile;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query(value = "from Profile p where p.id = :id")
    Profile findProfileById(UUID id);

    @Query(value = "from Profile  p where p.userId = :userId")
    Profile findProfileByUserId(UUID userId);

}