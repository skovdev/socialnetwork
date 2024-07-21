package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.dto.user.UserDto;
import local.socialnetwork.userservice.dto.user.UserRequestUpdateDto;

import java.util.UUID;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> findById(UUID id);
    Optional<UUID> findUserIdByAuthUserId(UUID authUserId);
    void save(UserDto userDto);
    void update(UUID id, UserRequestUpdateDto userRequestUpdateDto);
    void deleteById(UUID id);
    void deleteByAuthUserId(UUID authUserId);
}