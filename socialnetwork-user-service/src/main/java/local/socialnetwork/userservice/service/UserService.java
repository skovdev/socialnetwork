package local.socialnetwork.userservice.service;

import local.socialnetwork.userservice.model.dto.ChangePasswordDto;
import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.dto.profile.EditProfileDto;

import local.socialnetwork.userservice.model.user.CustomUser;

import java.io.IOException;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<CustomUser> findById(UUID id);
    Optional<CustomUser> findByUsername(String username);
    void update(UUID id, EditProfileDto editProfile);
    void registration(RegistrationDto registrationDTO) throws IOException;
    boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto);
    void changePassword(ChangePasswordDto changePasswordDto);
    void deleteById(UUID id);
}