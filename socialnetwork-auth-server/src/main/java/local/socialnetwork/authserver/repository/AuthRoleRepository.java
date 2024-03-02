package local.socialnetwork.authserver.repository;

import local.socialnetwork.authserver.entity.AuthRole;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRoleRepository extends JpaRepository<AuthRole, UUID> {

}