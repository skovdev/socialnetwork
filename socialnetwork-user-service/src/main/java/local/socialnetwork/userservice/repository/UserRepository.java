package local.socialnetwork.userservice.repository;

import local.socialnetwork.userservice.model.entity.user.CustomUser;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, UUID> {

    @Query("from CustomUser u where u.username = :username")
    Optional<CustomUser> findByUsername(@Param("username") String username);

}