package local.socialnetwork.core.service;

import local.socialnetwork.model.dto.ChangePasswordDto;
import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.dto.RegistrationDto;

import java.io.IOException;

public interface UserService {
    CustomUser findByName(String username);
    void registration(RegistrationDto registrationDTO) throws IOException;
    boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto);
    void changePassword(ChangePasswordDto changePasswordDto);
}