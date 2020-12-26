package local.socialnetwork.groupservice.repository;

import local.socialnetwork.groupservice.model.group.GroupUser;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, UUID> {

}