package local.socialnetwork.groupservice.repository;

import local.socialnetwork.groupservice.model.entity.GroupUser;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, UUID> {

    @Query(value = "from GroupUser gu where gu.userId = :userId")
    List<GroupUser> findAllGroupIdsByUserId(@Param("userId") UUID userId);

}