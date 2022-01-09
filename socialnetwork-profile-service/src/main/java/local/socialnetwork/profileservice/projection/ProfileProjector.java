package local.socialnetwork.profileservice.projection;

import local.socialnetwork.profileservice.client.user.UserProxyService;

import local.socialnetwork.profileservice.events.AvatarDeletedEvent;
import local.socialnetwork.profileservice.events.AvatarUpdatedEvent;
import local.socialnetwork.profileservice.events.CheckValidatedOldPasswordEvent;
import local.socialnetwork.profileservice.events.PasswordChangedEvent;
import local.socialnetwork.profileservice.events.ProfileCreatedEvent;
import local.socialnetwork.profileservice.events.ProfileUpdatedEvent;

import local.socialnetwork.profileservice.events.StatusChangedEvent;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.service.kafka.producer.user.UserProducerService;

import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.query.EditProfileByUsernameQuery;
import local.socialnetwork.profileservice.query.FindAllProfilesQuery;
import local.socialnetwork.profileservice.query.FindAvatarByUsernameQuery;
import local.socialnetwork.profileservice.query.FindProfileByUserIdQuery;
import local.socialnetwork.profileservice.query.FindProfileByUsernameQuery;

import local.socialnetwork.profileservice.repository.ProfileRepository;

import local.socialnetwork.profileservice.util.ResourceUtil;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.List;

@Component
public class ProfileProjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileProjector.class);

    @Value("${sn.profile.upload.avatar.dir.path}")
    private String pathUploadAvatar;

    @Value("${sn.profile.default.avatar.path}")
    private String pathDefaultAvatar;

    @Value("${sn.kafka.topic.user.update}")
    private String topicUserUpdate;

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

    public UserProducerService userProducerService;

    @Autowired
    public void setUserProducer(UserProducerService userProducerService) {
        this.userProducerService = userProducerService;
    }

    private ResourceUtil resourceUtil;

    @Autowired
    public void setResourceUtil(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @EventHandler
    public void on(ProfileCreatedEvent profileCreatedEvent) {

        Profile profile = new Profile();

        profile.setId(profileCreatedEvent.getId());
        profile.setActive(profileCreatedEvent.isActive());
        profile.setAvatar(profileCreatedEvent.getAvatar());
        profile.setUserId(profileCreatedEvent.getUser().getId());

        profileRepository.save(profile);

    }

    @EventHandler
    public void on(ProfileUpdatedEvent profileUpdatedEvent) throws ProfileServiceException {

        var user = userProxyService.findUserByUserId(profileUpdatedEvent.getId());

        if (user != null) {

            EditProfileDto editProfileDto = new EditProfileDto();

            editProfileDto.setFirstName(profileUpdatedEvent.getFirstName());
            editProfileDto.setLastName(profileUpdatedEvent.getLastName());
            editProfileDto.setCountry(profileUpdatedEvent.getCountry());
            editProfileDto.setCity(profileUpdatedEvent.getCity());
            editProfileDto.setAddress(profileUpdatedEvent.getAddress());
            editProfileDto.setPhone(profileUpdatedEvent.getPhone());
            editProfileDto.setBirthday(profileUpdatedEvent.getBirthday());
            editProfileDto.setFamilyStatus(profileUpdatedEvent.getFamilyStatus());
            editProfileDto.setUserId(user.getId());

            userProducerService.send(topicUserUpdate, editProfileDto);

        } else {
            throw new ProfileServiceException("Profile has not updated");
        }
    }

    @EventHandler
    public void on(AvatarUpdatedEvent avatarUpdatedEvent) throws ProfileServiceException, IOException {

        UserDto user = userProxyService.findUserByUsername(avatarUpdatedEvent.getUsername());

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile != null) {

                var encodedAvatar = resourceUtil.writeResource(avatarUpdatedEvent.getMultipartFile(), pathUploadAvatar);

                profile.setAvatar(encodedAvatar);

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has updated avatar", profile.getId());

            } else {
                throw new ProfileServiceException("Avatar for profile has not updated");
            }
        }
    }

    @EventHandler
    public String on(AvatarDeletedEvent avatarDeletedEvent) throws ProfileServiceException, IOException {

        UserDto user = userProxyService.findUserByUsername(avatarDeletedEvent.getUsername());

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

        throw new NullPointerException();

    }

    @EventHandler
    public boolean on(StatusChangedEvent statusChangedEvent) {

        UserDto user = userProxyService.findUserByUsername(statusChangedEvent.getUsername());

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile != null) {

                profile.setActive(statusChangedEvent.isActive());

                profileRepository.save(profile);

                LOGGER.info("Profile with ID: {} has changed status", profile.getId());

                return true;

            }
        }

        return false;

    }

    @EventHandler
    public boolean on(CheckValidatedOldPasswordEvent checkValidatedOldPasswordEvent) {
        var user = userProxyService.findUserByUsername(checkValidatedOldPasswordEvent.getUsername());
        return user != null && passwordEncoder.matches(checkValidatedOldPasswordEvent.getOldPassword(), user.getPassword());
    }

    @EventHandler
    public void on(PasswordChangedEvent passwordChangedEvent) {

        var user = userProxyService.findUserByUsername(passwordChangedEvent.getUsername());

        if (user != null) {
            String encodedPassword = passwordEncoder.encode(passwordChangedEvent.getNewPassword());
            userProxyService.changePassword(user.getUsername(), encodedPassword);
        }
    }

    @QueryHandler
    public List<Profile> handle(FindAllProfilesQuery findAllProfilesQuery) {
        return profileRepository.findAll();
    }

    @QueryHandler
    public Profile handle(FindProfileByUserIdQuery findProfileByUserIdQuery) {
        return profileRepository.findProfileById(findProfileByUserIdQuery.getUserId());
    }

    @QueryHandler
    public Profile handle(FindProfileByUsernameQuery findProfileByUsernameQuery) {

        UserDto user = userProxyService.findUserByUsername(findProfileByUsernameQuery.getUsername());

        if (user != null) {
            return profileRepository.findProfileByUserId(user.getId());
        }

        throw new NullPointerException();

    }

    @QueryHandler
    public EditProfileDto handle(EditProfileByUsernameQuery editProfileByUsernameQuery) {

        UserDto user = userProxyService.findUserByUsername(editProfileByUsernameQuery.getUsername());

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

        throw new NullPointerException();

    }

    @QueryHandler
    public String handle(FindAvatarByUsernameQuery findAvatarByUsernameQuery) {

        UserDto user = userProxyService.findUserByUsername(findAvatarByUsernameQuery.getUsername());

        if (user != null) {

            Profile profile = profileRepository.findProfileByUserId(user.getId());

            if (profile != null) {
                return profile.getAvatar();
            }
        }

        throw new NullPointerException();

    }
}