package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.client.UserProxyService;

import local.socialnetwork.profileservice.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.dto.user.UserDto;

import local.socialnetwork.profileservice.entity.profile.Profile;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.repository.ProfileRepository;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.util.ResourceUtil;
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

import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Value("${sn.profile.avatar.dir}")
    private String pathUploadAvatar;

    @Value("${sn.profile.default.avatar.dir}")
    private String pathDefaultAvatar;

    private UserProxyService userProxyService;

    @Autowired
    public void setUserProxyService(UserProxyService userProxyService) {
        this.userProxyService = userProxyService;
    }

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
    public ProfileDto findProfileByUsername(String username) {
        UserDto user = userProxyService.findUserByUsername(username);
        return user.getProfile();
    }

    @Override
    public List<ProfileDto> findProfilesByFirstName(String firstName) {

        return userProxyService.findUserByFirstName(firstName).stream()
                                                              .map(UserDto::getProfile)
                                                              .collect(Collectors.toList());

    }

    @Override
    public Profile findById(UUID id) {
        return profileRepository.findProfileById(id);
    }

    @Override
    public ProfileDto findProfileByUserId(UUID userId) {
        UserDto user = userProxyService.findUserByUserId(userId);
        return user.getProfile();
    }

    @Override
    public void update(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException {

        var user = userProxyService.findUserByUserId(id);

        if (user != null) {

            user.setFirstName(editProfileDto.getFirstName());
            user.setLastName(editProfileDto.getLastName());

            var userDetails = user.getUserDetails();

            if (userDetails != null) {

                userDetails.setCountry(editProfileDto.getCountry());
                userDetails.setCity(editProfileDto.getCity());
                userDetails.setAddress(editProfileDto.getAddress());
                userDetails.setPhone(editProfileDto.getPhone());
                userDetails.setBirthday(editProfileDto.getBirthday());
                userDetails.setFamilyStatus(editProfileDto.getFamilyStatus());

                userProxyService.update(user);

                LOGGER.info("Profile has updated successfully");

            }
        } else {
            throw new ProfileServiceException("Profile has not updated");
        }
    }

    @Transactional
    @Override
    public void delete(UUID id) {

        var profile = profileRepository.findProfileById(id);

        if (profile != null) {

            UserDto user = userProxyService.findUserByUserId(profile.getUserId());

            if (user != null && profile.getUserId().equals(user.getId())) {
                profileRepository.delete(profile);
                userProxyService.delete(user);
            }
        }
    }

    @Override
    public String setDefaultAvatar(String username) throws IOException, ProfileServiceException {

        UserDto user = userProxyService.findUserByUsername(username);

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile.getUserId().equals(user.getId())) {

                String encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);

                profile.setAvatar(encodedDefaultAvatar);

                LOGGER.info("Profile with ID: {} has deleted current avatar and set default avatar", profile.getId());

                return encodedDefaultAvatar;

            } else {
                throw new ProfileServiceException("Could not set default avatar for profile");
            }
        }

        return null;

    }

    @Override
    public void updateAvatarProfile(String username, MultipartFile multipartFile) throws IOException, ProfileServiceException {

    }

    @Override
    public boolean changeStatus(String username, boolean isActive) {
        return false;
    }

    @Override
    public EditProfileDto editProfileByUsername(String username) throws ProfileServiceException {
        return null;
    }
}