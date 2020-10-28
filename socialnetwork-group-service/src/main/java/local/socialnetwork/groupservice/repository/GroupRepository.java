package local.socialnetwork.groupservice.repository;

import local.socialnetwork.groupservice.model.group.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    
    @Query(value = "SELECT * FROM sn_groups as g inner join sn_group_user gu on g.id = gu.group_id where gu.user_id = ?", nativeQuery = true)
    List<Group> findAllByUserId(UUID id);
    Group findByName(String name);
}