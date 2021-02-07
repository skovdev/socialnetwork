package local.socialnetwork.groupservice.repository;

import local.socialnetwork.groupservice.model.entity.group.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query(value = "from Group g where g.id = :id")
    Group findGroupById(@Param("id") UUID id);

    Group findByName(String name);

}