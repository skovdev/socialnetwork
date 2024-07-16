package local.socialnetwork.authserver.service.impl;

import local.socialnetwork.authserver.client.UserFeignClient;
import local.socialnetwork.authserver.client.ProfileFeignClient;

import local.socialnetwork.authserver.config.jwt.JwtTokenProvider;

import local.socialnetwork.authserver.dto.SignUpDto;
import local.socialnetwork.authserver.dto.ChangePasswordDto;

import local.socialnetwork.authserver.dto.authuser.AuthUserDto;
import local.socialnetwork.authserver.dto.authuser.AuthRoleDto;
import local.socialnetwork.authserver.dto.authuser.AuthAdditionalTokenDataDto;

import local.socialnetwork.authserver.event.UserDetailsEvent;

import local.socialnetwork.authserver.entity.AuthUser;
import local.socialnetwork.authserver.entity.AuthRole;

import local.socialnetwork.authserver.repository.AuthUserRepository;
import local.socialnetwork.authserver.repository.AuthRoleRepository;

import local.socialnetwork.authserver.service.AuthUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import java.util.Collections;

import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    @Value("${sn.auth.user.signup.default.role}")
    String defaultUserRole;

    final AuthUserRepository authUserRepository;
    final AuthRoleRepository authRoleRepository;
    final ApplicationEventPublisher applicationEventPublisher;
    final JwtTokenProvider jwtTokenProvider;
    final PasswordEncoder passwordEncoder;
    final UserFeignClient userFeignClient;
    final ProfileFeignClient profileFeignClient;

    @Override
    public String generateToken(AuthUserDto authUserDto) {
        Map<String, Object> data = new HashMap<>();
        data.put("authUserId", authUserDto.id());
        data.put("username", authUserDto.username());
        data.put("isAdmin", isAdmin(getRoles(authUserDto.authRoles())));
        return jwtTokenProvider.createToken(data);
    }

    private boolean isAdmin(List<String> roles) {
        return roles.stream()
                .anyMatch(role -> role.equalsIgnoreCase("ADMIN"));
    }

    private List<String> getRoles(List<AuthRoleDto> authRoles) {
        return authRoles.stream()
                .map(AuthRoleDto::authority)
                .collect(Collectors.toList());
    }

    @Override
    public String extendToken(String token, Map<String, Object> data) {
        return jwtTokenProvider.extendToken(token, data);
    }

    @Override
    @Transactional
    public Optional<AuthAdditionalTokenDataDto> findAdditionalTokenData(UUID authUserId) {
      return Optional.ofNullable(userFeignClient.findUserIdByAuthUserId(authUserId))
              .flatMap(userId -> Optional.ofNullable(profileFeignClient.findProfileIdByUserId(userId))
                      .map(profileId -> new AuthAdditionalTokenDataDto(userId, profileId)));
    }

    @Override
    @Transactional
    public Optional<AuthUserDto> findByUsername(String username) {
        return authUserRepository.findByUsername(username)
                .map(authUser -> new AuthUserDto(
                        authUser.getId(),
                        authUser.getUsername(),
                        authUser.getPassword(),
                        authUser.getAuthRoles()
                                .stream()
                                .map(authRole -> new AuthRoleDto(
                                        authRole.getId(),
                                        authRole.getAuthority(),
                                        authRole.getAuthUser().getId()))
                                .collect(Collectors.toList())));
    }

    @Override
    @Transactional
    public void signUp(SignUpDto signUpDTO) {

        AuthUser authUser = new AuthUser();
        authUser.setId(UUID.randomUUID());
        authUser.setUsername(signUpDTO.username());
        authUser.setPassword(passwordEncoder.encode(signUpDTO.password()));

        AuthRole authRole = new AuthRole();
        authRole.setId(UUID.randomUUID());
        authRole.setAuthority(defaultUserRole);
        authRole.setAuthUser(authUser);

        authUser.setAuthRoles(Collections.singletonList(authRole));

        authUserRepository.save(authUser);
        authRoleRepository.save(authRole);

        applicationEventPublisher.publishEvent(new UserDetailsEvent(signUpDTO, authUser.getId()));

        log.info("Auth user is saved successfully. AuthUserID: {}", authUser.getId());

    }

    @Override
    @Transactional
    public void deleteById(UUID id) {

        authUserRepository.findById(id)
                .ifPresentOrElse(
                        authUser -> {
                            authUserRepository.delete(authUser);
                            log.info("Auth user is deleted. AuthUserID: {}", id);
                        },
                        () -> log.info("Auth user not found. AuthUserID: {}", id)
                );
    }

    @Override
    public boolean changePassword(ChangePasswordDto changePasswordDto) {
        String username = changePasswordDto.username();
        log.info("Attempting to change password for user: {}", username);
        Optional<AuthUser> authUser = authUserRepository.findByUsername(username);
        if (authUser.isPresent()) {
            return updatePassword(authUser.get(), changePasswordDto.newPassword(), username);
        } else {
            log.info("User not found. Password can't be changed. Username: {}", username);
            return false;
        }
    }
    private boolean updatePassword(AuthUser authUser, String newPassword, String username) {
        try {
            authUser.setPassword(passwordEncoder.encode(newPassword));
            authUserRepository.save(authUser);
            log.info("Password successfully changed for user: {}", username);
            return true;
        } catch (Exception e) {
            log.error("Failed to change password for user: {}", username, e);
            throw new RuntimeException("An error occurred while updating the password for user: " + username, e);
        }
    }
}