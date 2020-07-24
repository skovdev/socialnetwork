package local.socialnetwork.core.service;

import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.dto.RegistrationDto;

import java.io.IOException;

public interface UserService {
    void registration(RegistrationDto registrationDTO) throws IOException;
    CustomUser findByName(String username);
}