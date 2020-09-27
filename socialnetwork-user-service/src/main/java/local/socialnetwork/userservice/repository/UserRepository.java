package local.socialnetwork.userservice.repository;

import local.socialnetwork.userservice.model.user.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<CustomUser, UUID> {


}