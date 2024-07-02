package local.socialnetwork.userservice.repository;

import local.socialnetwork.userservice.entity.user.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByAuthUserId(UUID authUserId);

    @Query("delete from User user where user.authUserId =:authUserId")
    @Modifying
    void deleteByAuthUserId(@Param("authUserId") UUID authUserId);

}