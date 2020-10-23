package local.socialnetwork.userservice.service.impl;

import local.socialnetwork.userservice.client.ProfileProxyService;

import local.socialnetwork.userservice.model.dto.ChangePasswordDto;
import local.socialnetwork.userservice.model.dto.ProfileDto;
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

    @Value("${sn.profile.default.avatar.dir}")
    private String pathDefaultAvatar;

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

    private ProfileProxyService profileProxyService;

    @Autowired
    public void setProfileProxyService(ProfileProxyService profileProxyService) {
        this.profileProxyService = profileProxyService;
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
    public void update(CustomUser user) {
        userRepository.save(user);
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

        profileProxyService.save(newProfile);

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
}