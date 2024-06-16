package local.socialnetwork.authserver.service;

import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.dto.authuser.AuthUserDto;

import java.util.UUID;
import java.util.Optional;

public interface AuthUserService {
    Optional<AuthUserDto> findByUsername(String username);
    Optional<UUID> findAuthIdByUsername(String username);
    void signUp(SignUpDto registrationDTO);
    void deleteById(UUID id);
}
