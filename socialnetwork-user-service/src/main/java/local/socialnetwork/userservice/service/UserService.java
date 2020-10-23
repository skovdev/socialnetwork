package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.model.dto.ChangePasswordDto;
import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.user.CustomUser;

import java.io.IOException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<CustomUser> findById(UUID id);
    Optional<CustomUser> findByUsername(String username);
    void update(CustomUser user);
    void registration(RegistrationDto registrationDTO) throws IOException;
    boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto);
    void changePassword(ChangePasswordDto changePasswordDto);
}