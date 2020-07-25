package local.socialnetwork.core.service.impl;

import local.socialnetwork.core.exception.ProfileServiceException;

import local.socialnetwork.core.repository.ProfileRepository;

import local.socialnetwork.core.service.ProfileService;

import local.socialnetwork.core.util.ResourceUtil;

import local.socialnetwork.model.dto.EditProfileDto;

import local.socialnetwork.model.profile.Profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

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

    private ProfileRepository profileRepository;

    @Autowired
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
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
    public void update(UUID id, EditProfileDto editProfile) throws ProfileServiceException {

        var profile = profileRepository.findProfileByUserId(id);

        if (profile != null) {

            var user = profile.getCustomUser();

            user.setFirstName(editProfile.getFirstName());
            user.setLastName(editProfile.getLastName());

            var userDetails = user.getCustomUserDetails();

            if (userDetails != null) {

                userDetails.setCountry(editProfile.getCountry());
                userDetails.setCity(editProfile.getCity());
                userDetails.setAddress(editProfile.getAddress());
                userDetails.setPhone(editProfile.getPhone());
                userDetails.setBirthday(editProfile.getBirthday());
                userDetails.setFamilyStatus(editProfile.getFamilyStatus());

                profileRepository.save(profile);

                LOGGER.info("Profile has updated successfully");

            }
        } else {
            throw new ProfileServiceException("Profile has not updated");
        }
    }

    @Override
    public void delete(UUID id) {
        profileRepository.deleteById(id);
    }

    @Transactional
    @Override
    public String setDefaultAvatar(String username) throws IOException, ProfileServiceException {

        var profile = profileRepository.findProfileByUsername(username);

        if (profile != null) {

            var encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);

            profile.setAvatar(encodedDefaultAvatar);

            profileRepository.save(profile);

            LOGGER.info("Profile with ID: {} has deleted current avatar and set default avatar", profile.getId());

            return encodedDefaultAvatar;

        } else {
            throw new ProfileServiceException("Could not set default avatar for profile");
        }
    }

    @Transactional
    @Override
    public void updateAvatarProfile(String username, MultipartFile multipartFile) throws IOException, ProfileServiceException {

        var profile = profileRepository.findProfileByUsername(username);

        if (profile != null) {

            String encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);

            profile.setAvatar(encodedAvatar);

            profileRepository.save(profile);

            LOGGER.info("Profile with ID: {} has updated avatar", profile.getId());

        } else {
            throw new ProfileServiceException("Avatar for profile has not updated");
        }
    }

    @Transactional
    @Override
    public boolean changeStatus(String username, boolean isActive) {

        var profile = profileRepository.findProfileByUsername(username);

        if (profile != null) {

            profile.setActive(isActive);

            profileRepository.save(profile);

            LOGGER.info("Profile with ID: {} has changed status", profile.getId());

            return true;

        }

        return false;

    }

    @Transactional
    @Override
    public EditProfileDto editProfileByUsername(String username) throws ProfileServiceException {

        var profile = profileRepository.findProfileByUsername(username);

        if (profile != null) {

            EditProfileDto editProfile = new EditProfileDto();

            editProfile.setFirstName(profile.getCustomUser().getFirstName());
            editProfile.setLastName(profile.getCustomUser().getLastName());
            editProfile.setCountry(profile.getCustomUser().getCustomUserDetails().getCountry());
            editProfile.setCity(profile.getCustomUser().getCustomUserDetails().getCity());
            editProfile.setAddress(profile.getCustomUser().getCustomUserDetails().getAddress());
            editProfile.setPhone(profile.getCustomUser().getCustomUserDetails().getPhone());
            editProfile.setBirthday(profile.getCustomUser().getCustomUserDetails().getBirthday());
            editProfile.setFamilyStatus(profile.getCustomUser().getCustomUserDetails().getFamilyStatus());

            return editProfile;

        } else {
            throw new ProfileServiceException("Profile is not exist");
        }
    }
}