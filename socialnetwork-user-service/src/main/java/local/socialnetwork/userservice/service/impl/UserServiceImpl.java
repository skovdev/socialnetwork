package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.userservice.model.dto.profile.ProfileDto;

import local.socialnetwork.userservice.model.entity.user.CustomRole;
import local.socialnetwork.userservice.model.entity.user.CustomUser;
import local.socialnetwork.userservice.model.entity.user.CustomUserDetails;

import local.socialnetwork.userservice.repository.RoleRepository;
import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import local.socialnetwork.userservice.service.kafka.producer.profile.ProfileProducerService;

import local.socialnetwork.userservice.util.ResourceUtil;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${sn.kafka.producer.topic.profile.new}")
    String topicProfileNew;

    @Value("${sn.user.default.role}")
    String defaultUserRole;

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final ProfileProducerService profileProducerService;
    final PasswordEncoder passwordEncoder;
    final ResourceUtil resourceUtil;

    @Override
    public Optional<CustomUser> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<CustomUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void update(EditProfileDto editProfile) {

        Optional<CustomUser> optionalUser = userRepository.findById(editProfile.getUserId());

        optionalUser.ifPresent(user -> {

            user.setFirstName(editProfile.getFirstName());
            user.setLastName(editProfile.getLastName());

            CustomUserDetails userDetails = new CustomUserDetails();

            userDetails.setCountry(editProfile.getCountry());
            userDetails.setCity(editProfile.getCity());
            userDetails.setAddress(editProfile.getAddress());
            userDetails.setPhone(editProfile.getPhone());
            userDetails.setBirthday(editProfile.getBirthday());
            userDetails.setFamilyStatus(editProfile.getFamilyStatus());

            user.setUserDetails(userDetails);

            userRepository.save(user);

        });
    }

    @Override
    public void registration(RegistrationDto registration) throws IOException {

        CustomUser newUser = new CustomUser();

        newUser.setFirstName(registration.getFirstName());
        newUser.setLastName(registration.getLastName());
        newUser.setUsername(registration.getUsername());

        String encodedUserPassword = passwordEncoder.encode(registration.getPassword());

        newUser.setPassword(encodedUserPassword);

        CustomUserDetails newUserDetails = new CustomUserDetails();

        newUserDetails.setCountry(registration.getCountry());
        newUserDetails.setCity(registration.getCity());
        newUserDetails.setAddress(registration.getAddress());
        newUserDetails.setPhone(registration.getPhone());
        newUserDetails.setBirthday(registration.getBirthday());
        newUserDetails.setFamilyStatus(registration.getFamilyStatus());

        newUser.setUserDetails(newUserDetails);

        CustomRole newRole = new CustomRole();

        newRole.setAuthority(defaultUserRole);
        newRole.setUser(newUser);

        List<CustomRole> roles = new ArrayList<>();

        roles.add(newRole);

        newUser.setRoles(roles);

        userRepository.save(newUser);

        ProfileDto newProfile = new ProfileDto();

        var encodedPhoto = resourceUtil.getEncodedResource(pathDefaultAvatar);

        newProfile.setId(UUID.randomUUID());
        newProfile.setAvatar(encodedPhoto);
        newProfile.setActive(true);
        newProfile.setUserId(newUser.getId());

        roleRepository.save(newRole);

        profileProducerService.sendProfileAndSave(topicProfileNew, newProfile);

    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(String username, String newPassword) {

        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);
        });
    }
}