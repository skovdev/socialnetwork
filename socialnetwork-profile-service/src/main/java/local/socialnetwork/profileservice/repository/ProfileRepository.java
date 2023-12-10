package local.socialnetwork.profileservice.repository;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

}