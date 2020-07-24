package local.socialnetwork.core.repository;

import local.socialnetwork.model.user.CustomUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, UUID> {

    @Query(value = "select cu from CustomUser cu where cu.username = :username")
    CustomUser findByUsername(String username);

}