package local.socialnetwork.groupservice.repository;

import local.socialnetwork.groupservice.model.entity.group.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    Group findByName(String name);
}