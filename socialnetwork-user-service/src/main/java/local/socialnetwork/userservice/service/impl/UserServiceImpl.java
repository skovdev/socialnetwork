package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser;
import local.socialnetwork.userservice.aspect.annotation.IdentifyNewUser;

import local.socialnetwork.userservice.mapping.MappingObject;

import local.socialnetwork.userservice.client.ProfileProxyService;

import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.dto.profile.ProfileDto;

import local.socialnetwork.userservice.model.entity.user.CustomRole;
import local.socialnetwork.userservice.model.entity.user.CustomUser;
import local.socialnetwork.userservice.model.entity.user.CustomUserDetails;

import local.socialnetwork.userservice.repository.RoleRepository;
import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import local.socialnetwork.userservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @Value("${sn.user.default.role}")
    String defaultUserRole;

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    ProfileProxyService profileProxyService;

    @Autowired
    public void setProfileProxyService(ProfileProxyService profileProxyService) {
        this.profileProxyService = profileProxyService;
    }

    MappingObject mappingObject;

    @Autowired
    public void setMappingObject(MappingObject mappingObject) {
        this.mappingObject = mappingObject;
    }

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
    public void registration(@IdentifyNewUser RegistrationDto registration) throws IOException {

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

        ProfileDto newProfile = new ProfileDto();

        var encodedPhoto = resourceUtil.getEncodedResource(pathDefaultAvatar);

        newProfile.setId(UUID.randomUUID());
        newProfile.setAvatar(encodedPhoto);
        newProfile.setActive(true);
        newProfile.setUser(mappingObject.convertUserToUserDto(newUser));

        userRepository.save(newUser);
        roleRepository.save(newRole);

        profileProxyService.save(resourceUtil.convertToString(newProfile));

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