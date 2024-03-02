package local.socialnetwork.authserver.service;

import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.entity.AuthUser;

import java.util.UUID;
import java.util.Optional;

public interface AuthUserService {
    Optional<AuthUser> findByUsername(String username);
    void signUp(SignUpDto registrationDTO);
    void deleteById(UUID id);
}
