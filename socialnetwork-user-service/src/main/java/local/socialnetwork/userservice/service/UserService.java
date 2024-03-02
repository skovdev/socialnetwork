package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.dto.user.UserDto;

import java.util.UUID;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> findById(UUID id);
    void save(UserDto userDto);
    void deleteById(UUID id);
    void deleteByAuthUserId(UUID authUserId);
}