package local.socialnetwork.authserver.repository;

import local.socialnetwork.authserver.model.entity.Role;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RoleRepository extends CrudRepository<Role, UUID> {

}