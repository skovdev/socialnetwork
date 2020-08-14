package local.socialnetwork.core.service.impl;

import local.socialnetwork.core.repository.ProfileRepository;
import local.socialnetwork.core.repository.RoleRepository;
import local.socialnetwork.core.repository.UserRepository;

import local.socialnetwork.core.service.UserService;

import local.socialnetwork.core.util.ResourceUtil;

import local.socialnetwork.model.dto.ChangePasswordDto;
import local.socialnetwork.model.user.CustomRole;
import local.socialnetwork.model.user.CustomUser;
import local.socialnetwork.model.user.CustomUserDetails;

import local.socialnetwork.model.profile.Profile;

import local.socialnetwork.model.dto.RegistrationDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String DEFAULT_USER_ROLE = "USER";

    @Value("${sn.profile.default.avatar.dir}")
    private String pathDefaultAvatar;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private ProfileRepository profileRepository;

    @Autowired
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    @Transactional
    @Override
    public CustomUser findByName(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    @Override
    public void registration(RegistrationDto registrationDto) throws IOException {

        CustomUser newUser = new CustomUser();

        newUser.setFirstName(registrationDto.getFirstName());
        newUser.setLastName(registrationDto.getLastName());
        newUser.setUsername(registrationDto.getUsername());

        String encodedUserPassword = passwordEncoder.encode(registrationDto.getPassword());

        newUser.setPassword(encodedUserPassword);

        CustomUserDetails newUserDetails = new CustomUserDetails();

        newUserDetails.setCountry(registrationDto.getCountry());
        newUserDetails.setCity(registrationDto.getCity());
        newUserDetails.setAddress(registrationDto.getAddress());
        newUserDetails.setPhone(registrationDto.getPhone());
        newUserDetails.setBirthday(registrationDto.getBirthday());
        newUserDetails.setFamilyStatus(registrationDto.getFamilyStatus());

        newUser.setCustomUserDetails(newUserDetails);

        CustomRole newRole = new CustomRole();

        newRole.setAuthority(DEFAULT_USER_ROLE);
        newRole.setCustomUser(newUser);

        Profile newProfile = new Profile();

        var encodedPhoto = resourceUtil.getEncodedResource(pathDefaultAvatar);

        newProfile.setAvatar(encodedPhoto);
        newProfile.setActive(true);
        newProfile.setCustomUser(newUser);

        userRepository.save(newUser);
        profileRepository.save(newProfile);
        roleRepository.save(newRole);

    }

    @Transactional
    @Override
    public boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto) {
        var user = userRepository.findByUsername(changePasswordDto.getUsername());
        return user != null && passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword());
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {

        var user = userRepository.findByUsername(changePasswordDto.getUsername());

        if (user != null) {
            String newPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }
}