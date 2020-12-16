package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.kafka.model.dto.profile.ProfileDto;

import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.userservice.kafka.producer.user.UserProducer;

import local.socialnetwork.userservice.model.dto.ChangePasswordDto;
import local.socialnetwork.userservice.model.dto.RegistrationDto;

import local.socialnetwork.userservice.model.user.CustomRole;
import local.socialnetwork.userservice.model.user.CustomUser;
import local.socialnetwork.userservice.model.user.CustomUserDetails;

import local.socialnetwork.userservice.repository.RoleRepository;
import local.socialnetwork.userservice.repository.UserRepository;

import local.socialnetwork.userservice.service.UserService;

import local.socialnetwork.userservice.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String DEFAULT_USER_ROLE = "USER";

    private static final String TOPIC_PROFILE_NEW = "topic.profile.new";

    @Value("${sn.profile.default.avatar.path}")
    private String pathDefaultAvatar;

    private UserProducer userProducer;

    @Autowired
    public void setUserProducer(UserProducer userProducer) {
        this.userProducer = userProducer;
    }

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
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

    @Transactional
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

        newRole.setAuthority(DEFAULT_USER_ROLE);
        newRole.setUser(newUser);

        ProfileDto newProfile = new ProfileDto();

        var encodedPhoto = resourceUtil.getEncodedResource(pathDefaultAvatar);

        newProfile.setAvatar(encodedPhoto);
        newProfile.setActive(true);
        newProfile.setUserId(newUser.getId());

        userRepository.save(newUser);
        roleRepository.save(newRole);

        userProducer.send(TOPIC_PROFILE_NEW, newProfile);

    }

    @Override
    public boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto) {
        var user = userRepository.findByUsername(changePasswordDto.getUsername());
        return user.isPresent() && passwordEncoder.matches(changePasswordDto.getOldPassword(), user.get().getPassword());
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {

        var user = userRepository.findByUsername(changePasswordDto.getUsername());

        user.ifPresent(u -> {
            String newPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
            u.setPassword(newPassword);
            userRepository.save(u);
        });
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }
}