package local.socialnetwork.authserver.service.impl;

import local.socialnetwork.authserver.dto.SignUpDto;

import local.socialnetwork.authserver.dto.authuser.AuthRoleDto;
import local.socialnetwork.authserver.dto.authuser.AuthUserDto;

import local.socialnetwork.authserver.event.UserDetailsEvent;

import local.socialnetwork.authserver.entity.AuthRole;
import local.socialnetwork.authserver.entity.AuthUser;

import local.socialnetwork.authserver.repository.AuthRoleRepository;
import local.socialnetwork.authserver.repository.AuthUserRepository;

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

import java.util.UUID;
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
    final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
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

    @Transactional
    @Override
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

    @Transactional
    @Override
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
}