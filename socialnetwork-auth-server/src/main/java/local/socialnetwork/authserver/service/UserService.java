package local.socialnetwork.authserver.service;

import local.socialnetwork.authserver.dto.RegistrationDTO;
import local.socialnetwork.authserver.model.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    void registration(RegistrationDTO registrationDTO);
}
