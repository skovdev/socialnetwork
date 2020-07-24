package local.socialnetwork.core.service.impl;

import local.socialnetwork.common.auth.AuthenticationHelper;

import local.socialnetwork.core.repository.UserRepository;
import local.socialnetwork.core.repository.ProfileRepository;

import local.socialnetwork.core.service.ProfileService;

import local.socialnetwork.core.util.ResourceUtil;

import local.socialnetwork.model.dto.EditProfileDto;

import local.socialnetwork.model.user.CustomUser;

import local.socialnetwork.model.profile.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Value("${sn.profile.avatar.dir}")
    private String pathUploadAvatar;

    @Value("${sn.profile.default.avatar.dir}")
    private String pathDefaultAvatar;

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

    private AuthenticationHelper authenticationHelper;

    @Autowired
    public void setAuthenticationHelper(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    @Override
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findProfileByUsername(String username) {
        return profileRepository.findProfileByUsername(username);
    }

    @Override
    public List<Profile> findProfilesByFirstName(String firstName) {
        return profileRepository.findProfilesByFirstName(firstName);
    }

    @Override
    public Profile findById(UUID id) {
        return profileRepository.getOne(id);
    }

    @Transactional
    @Override
    public Profile findProfileByUserId(UUID userId) {
        return profileRepository.findProfileByUserId(userId);
    }

    @Override
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    @Transactional
    @Override
    public void update(Profile profile, EditProfileDto editProfileDto) {

        if (profile != null) {

            var user = profile.getCustomUser();

            user.setFirstName(editProfileDto.getFirstName());
            user.setLastName(editProfileDto.getLastName());

            var userDetails = user.getCustomUserDetails();

            if (userDetails != null) {

                userDetails.setCountry(editProfileDto.getCountry());
                userDetails.setCity(editProfileDto.getCity());
                userDetails.setAddress(editProfileDto.getAddress());
                userDetails.setPhone(editProfileDto.getPhone());
                userDetails.setBirthday(editProfileDto.getBirthday());
                userDetails.setFamilyStatus(editProfileDto.getFamilyStatus());

                profileRepository.save(profile);

            }
        }
    }

    @Override
    public void delete(UUID id) {
        profileRepository.deleteById(id);
    }

    @Transactional
    @Override
    public String setDefaultAvatar(String username) throws IOException {

        var customUser = userRepository.findByUsername(username);

        if (customUser != null) {

            var profile = customUser.getProfile();

            if (profile != null) {

                var encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);

                profile.setAvatar(encodedDefaultAvatar);

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has deleted current avatar and set default avatar", profile.getId());

               return encodedDefaultAvatar;

            }
        }

        return "";

    }

    @Transactional
    @Override
    public void updateAvatarProfile(MultipartFile multipartFile) throws IOException {

        CustomUser customUser = authenticationHelper.getAuthenticatedUser();

        if (customUser != null) {

            Profile profile = customUser.getProfile();

            if (profile != null) {

                String encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);

                profile.setAvatar(encodedAvatar);

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has updated avatar", profile.getId());

            }
        }
    }

    @Transactional
    @Override
    public void changeStatus(String username, boolean isActive) {

        CustomUser customUser = userRepository.findByUsername(username);

        if (customUser != null) {

            Profile profile = customUser.getProfile();

            if (profile != null) {
                profile.setActive(isActive);
                profileRepository.save(profile);
            }
        }
    }

    @Transactional
    @Override
    public EditProfileDto editProfileByUsername(String username) {

        var profile = profileRepository.findProfileByUsername(username);

        if (profile != null) {

            EditProfileDto editProfileDto = new EditProfileDto();

            editProfileDto.setFirstName(profile.getCustomUser().getFirstName());
            editProfileDto.setLastName(profile.getCustomUser().getLastName());
            editProfileDto.setCountry(profile.getCustomUser().getCustomUserDetails().getCountry());
            editProfileDto.setCity(profile.getCustomUser().getCustomUserDetails().getCity());
            editProfileDto.setAddress(profile.getCustomUser().getCustomUserDetails().getAddress());
            editProfileDto.setPhone(profile.getCustomUser().getCustomUserDetails().getPhone());
            editProfileDto.setBirthday(profile.getCustomUser().getCustomUserDetails().getBirthday());
            editProfileDto.setFamilyStatus(profile.getCustomUser().getCustomUserDetails().getFamilyStatus());

            return editProfileDto;

        }

        return new EditProfileDto();

    }
}