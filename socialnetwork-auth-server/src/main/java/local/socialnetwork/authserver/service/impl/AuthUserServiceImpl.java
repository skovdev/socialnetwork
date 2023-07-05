package local.socialnetwork.authserver.service.impl;

import local.socialnetwork.authserver.dto.SignUpDTO;

import local.socialnetwork.authserver.kafka.producer.user.UserProducer;

import local.socialnetwork.authserver.model.entity.AuthRole;
import local.socialnetwork.authserver.model.entity.AuthUser;

import local.socialnetwork.authserver.repository.AuthRoleRepository;
import local.socialnetwork.authserver.repository.AuthUserRepository;

import local.socialnetwork.authserver.service.AuthUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    @Value("${sn.auth.user.signup.default.role}")
    String defaultUserRole;

    @Value("${sn.kafka.topic.user.new}")
    String kafkaTopicNewUser;

    final AuthUserRepository authUserRepository;
    final AuthRoleRepository authRoleRepository;
    final UserProducer userProducer;
    final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public void signUp(SignUpDTO signUpDTO) {

        AuthUser authUser = new AuthUser();
        authUser.setUsername(signUpDTO.getUsername());
        authUser.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));

        AuthRole authRole = new AuthRole();
        authRole.setAuthority(defaultUserRole);
        authRole.setAuthUser(authUser);

        authUser.setAuthRoles(Collections.singletonList(authRole));

        authUserRepository.save(authUser);
        authRoleRepository.save(authRole);

        userProducer.sendUserAndSave(kafkaTopicNewUser, signUpDTO, authUser.getId());

    }
}