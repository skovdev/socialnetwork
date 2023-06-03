package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(UUID id);
    void save(User user);
    void deleteById(UUID id);
}