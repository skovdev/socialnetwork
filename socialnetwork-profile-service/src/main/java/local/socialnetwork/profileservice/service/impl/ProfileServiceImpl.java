package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.client.user.UserProxyService;

import local.socialnetwork.profileservice.kafka.producer.user.UserProducer;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.profileservice.model.dto.user.UserDetailsDto;
import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Value("${sn.profile.upload.avatar.dir.path}")
    private String pathUploadAvatar;

    @Value("${sn.profile.default.avatar.path}")
    private String pathDefaultAvatar;

    @Value("${sn.kafka.topic.user.update}")
    private String topicUserUpdate;

    @Value("${sn.kafka.topic.user.delete}")
    private String topicUserDelete;

    private UserProducer userProducer;

    @Autowired
    public void setUserProducer(UserProducer userProducer) {
        this.userProducer = userProducer;
    }

    private ProfileRepository profileRepository;

    @Autowired
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    private UserProxyService userProxyService;

    @Autowired
    public void setUserProxyService(UserProxyService userProxyService) {
        this.userProxyService = userProxyService;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    @Override
    public List<ProfileDto> findAll() {

        ProfileDto profileDto = new ProfileDto();
        UserDto userDto = new UserDto();
        UserDetailsDto userDetailsDto = new UserDetailsDto();

        List<ProfileDto> profileList = new ArrayList<>();

        for (var profile : profileRepository.findAll()) {

            profileDto.setId(profile.getId());
            profileDto.setAvatar(profile.getAvatar());
            profileDto.setActive(profile.isActive());

            var user = userProxyService.findUserByUserId(profile.getUserId());

            if (user != null) {

                userDto.setId(user.getId());
                userDto.setFirstName(user.getFirstName());
                userDto.setLastName(user.getLastName());

                userDetailsDto.setCountry(user.getUserDetails().getCountry());
                userDetailsDto.setCity(user.getUserDetails().getCity());
                userDetailsDto.setAddress(user.getUserDetails().getAddress());
                userDetailsDto.setPhone(user.getUserDetails().getPhone());
                userDetailsDto.setBirthday(user.getUserDetails().getBirthday());
                userDetailsDto.setFamilyStatus(user.getUserDetails().getFamilyStatus());

                userDto.setUserDetails(userDetailsDto);
                userDto.setRoles(user.getRoles());

                profileDto.setUser(userDto);

                profileList.add(profileDto);

            }
        }

        return profileList;

    }

    @Override
    public Profile findProfileByUsername(String username) {
        var user = userProxyService.findUserByUsername(username);
        return profileRepository.findProfileByUserId(user.getId());
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

    @Transactional
    @Override
    public Profile findProfileByUserId(UUID userId) {
        return profileRepository.findProfileByUserId(userId);
    }

    @Override
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    @Override
    public void update(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException {

        var user = userProxyService.findUserByUserId(id);

        if (user != null) {
            editProfileDto.setUserId(user.getId());
            userProducer.send(topicUserUpdate, editProfileDto);
        } else {
            throw new ProfileServiceException("Profile has not updated");
        }
    }

    @Override
    public void delete(UUID id) {

        var profile = profileRepository.findProfileById(id);

        if (profile != null) {

            UserDto user = userProxyService.findUserByUserId(profile.getUserId());

            if (user != null && profile.getUserId().equals(user.getId())) {
                profileRepository.delete(profile);
                userProducer.send(topicUserDelete, user.getId());
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

        UserDto user = userProxyService.findUserByUsername(username);

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile != null) {

                var encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);

                profile.setAvatar(encodedAvatar);

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has updated avatar", profile.getId());

            } else {
                throw new ProfileServiceException("Avatar for profile has not updated");
            }
        }
    }

    @Override
    public boolean changeStatus(String username, boolean isActive) {

        UserDto user = userProxyService.findUserByUsername(username);

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile != null) {

                profile.setActive(isActive);

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has changed status", profile.getId());

                return true;

            }
        }

        return false;
    }

    @Override
    public EditProfileDto editProfileByUsername(String username) throws ProfileServiceException {

        UserDto user = userProxyService.findUserByUsername(username);

        if (user != null) {

            EditProfileDto editProfile = new EditProfileDto();

            editProfile.setFirstName(user.getFirstName());
            editProfile.setLastName(user.getLastName());
            editProfile.setCountry(user.getUserDetails().getCountry());
            editProfile.setCity(user.getUserDetails().getCity());
            editProfile.setAddress(user.getUserDetails().getAddress());
            editProfile.setPhone(user.getUserDetails().getPhone());
            editProfile.setBirthday(user.getUserDetails().getBirthday());
            editProfile.setFamilyStatus(user.getUserDetails().getFamilyStatus());

            return editProfile;

        }

        return null;

    }
}