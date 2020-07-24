package local.socialnetwork.core.repository;

import local.socialnetwork.model.group.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Group findByName(String name);
}