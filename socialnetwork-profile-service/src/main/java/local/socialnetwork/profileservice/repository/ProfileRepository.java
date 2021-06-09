package local.socialnetwork.profileservice.repository;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query(value = "from Profile p where p.id = :id")
    Profile findProfileById(@Param("id") UUID id);

    @Query(value = "from Profile p where p.userId = :userId")
    Profile findProfileByUserId(@Param("userId") UUID userId);

}