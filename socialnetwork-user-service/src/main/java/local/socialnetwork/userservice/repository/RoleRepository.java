package local.socialnetwork.userservice.repository;

import local.socialnetwork.userservice.model.user.CustomRole;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<CustomRole, UUID> {

}