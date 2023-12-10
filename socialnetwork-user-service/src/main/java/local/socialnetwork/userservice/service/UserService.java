package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import local.socialnetwork.userservice.model.entity.User;

import java.util.UUID;

public interface UserService {
    UserDto findById(UUID id);
    void save(User user);
    void deleteById(UUID id);
}