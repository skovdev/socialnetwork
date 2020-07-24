package local.socialnetwork.core.repository;

import local.socialnetwork.model.profile.Profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    @Query("from Profile pr where pr.customUser.id = :userId")
    Profile findProfileByUserId(UUID userId);

    @Query("from Profile pr where pr.customUser.username = :username")
    Profile findProfileByUsername(String username);

    @Query("from Profile pr where pr.customUser.firstName like %:firstName%")
    List<Profile> findProfilesByFirstName(String firstName);

}