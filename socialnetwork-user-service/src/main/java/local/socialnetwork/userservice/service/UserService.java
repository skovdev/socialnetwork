package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.model.user.CustomUser;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<CustomUser> findById(UUID id);
}