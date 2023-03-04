package local.socialnetwork.authserver.service.impl;

import local.socialnetwork.authserver.dto.RegistrationDTO;

import local.socialnetwork.authserver.model.entity.Role;
import local.socialnetwork.authserver.model.entity.User;

import local.socialnetwork.authserver.repository.RoleRepository;
import local.socialnetwork.authserver.repository.UserRepository;

import local.socialnetwork.authserver.service.UserService;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${sn.user.registration.default.role}")
    String defaultUserRole;

    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void registration(RegistrationDTO registrationDTO) {
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        Role role = new Role();
        role.setAuthority(defaultUserRole);
        role.setUser(user);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        roleRepository.save(role);
    }
}