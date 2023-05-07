package local.socialnetwork.authserver.repository;

import local.socialnetwork.authserver.model.entity.AuthRole;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AuthRoleRepository extends CrudRepository<AuthRole, UUID> {

}