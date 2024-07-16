package local.socialnetwork.authserver.service;

import local.socialnetwork.authserver.dto.SignUpDto;
import local.socialnetwork.authserver.dto.ChangePasswordDto;

import local.socialnetwork.authserver.dto.authuser.AuthUserDto;
import local.socialnetwork.authserver.dto.authuser.AuthAdditionalTokenDataDto;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public interface AuthUserService {
    String generateToken(AuthUserDto authUserDto);
    String extendToken(String token, Map<String, Object> data);
    Optional<AuthAdditionalTokenDataDto> findAdditionalTokenData(UUID authUserId);
    Optional<AuthUserDto> findByUsername(String username);
    void signUp(SignUpDto registrationDto);
    void deleteById(UUID id);
    boolean changePassword(ChangePasswordDto changePasswordDto);
}
