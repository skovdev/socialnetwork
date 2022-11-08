package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.client.user.UserProxyService;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;
import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.repository.ProfileRepository;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.service.kafka.producer.user.UserProducerService;

import local.socialnetwork.profileservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Value("${sn.profile.upload.avatar.dir.path}")
    String pathUploadAvatar;

    @Value("${sn.profile.default.avatar.path}")
    String pathDefaultAvatar;

    @Value("${sn.kafka.topic.user.update}")
    String topicUserUpdate;

    final ProfileRepository profileRepository;
    final UserProxyService userProxyService;
    final UserProducerService userProducerService;
    final ResourceUtil resourceUtil;
    final PasswordEncoder passwordEncoder;

    @Override
    public List<ProfileDto> findAll() {
        List<Profile> profile = profileRepository.findAll();
        return profile.stream().map(p -> {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(p.getId());
            profileDto.setActive(p.isActive());
            profileDto.setAvatar(p.getAvatar());
            profileDto.setUserId(p.getUserId());
            return profileDto;
        }).collect(Collectors.toList());
    }

    @Override
    public ProfileDto findByProfileId(UUID profileId) {
        Profile profile = profileRepository.findProfileById(profileId);
        if (profile != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(profile.getId());
            profileDto.setActive(profile.isActive());
            profileDto.setAvatar(profile.getAvatar());
            profileDto.setUserId(profile.getUserId());
            return profileDto;
        }
        throw new NullPointerException();
    }

    @Override
    public ProfileDto findByUserId(UUID id) {
        Profile profile = profileRepository.findProfileByUserId(id);
        if (profile != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(profile.getId());
            profileDto.setActive(profile.isActive());
            profileDto.setAvatar(profile.getAvatar());
            profileDto.setUserId(profile.getUserId());
            return profileDto;
        }
        throw new NullPointerException();
    }

    @Override
    public ProfileInfoDto findByUsername(String username) {
        UserDto userDto = userProxyService.findUserByUsername(username);
        if (userDto != null) {
            Profile profile = profileRepository.findProfileByUserId(userDto.getId());
            if (profile != null) {
                ProfileInfoDto profileInfoDto = new ProfileInfoDto();
                profileInfoDto.setActive(profile.isActive());
                profileInfoDto.setAvatar(profile.getAvatar());
                profileInfoDto.setFirstName(userDto.getFirstName());
                profileInfoDto.setLastName(userDto.getLastName());
                profileInfoDto.setBirthDay(userDto.getUserDetails().getBirthday());
                profileInfoDto.setCountry(userDto.getUserDetails().getCountry());
                profileInfoDto.setCity(userDto.getUserDetails().getCity());
                profileInfoDto.setFamilyStatus(userDto.getUserDetails().getFamilyStatus());
                profileInfoDto.setPhone(userDto.getUserDetails().getFamilyStatus());
                profileInfoDto.setAddress(userDto.getUserDetails().getAddress());
                return profileInfoDto;
            }
        }
        throw new NullPointerException();
    }

    @Override
    public EditProfileDto findEditProfileByUsername(String username) {
        UserDto userDto = userProxyService.findUserByUsername(username);
        if (userDto != null) {
            EditProfileDto editProfileDto = new EditProfileDto();
            editProfileDto.setFirstName(userDto.getFirstName());
            editProfileDto.setFirstName(userDto.getFirstName());
            editProfileDto.setCountry(userDto.getUserDetails().getCountry());
            editProfileDto.setCity(userDto.getUserDetails().getCity());
            editProfileDto.setAddress(userDto.getUserDetails().getAddress());
            editProfileDto.setBirthday(userDto.getUserDetails().getBirthday());
            editProfileDto.setFamilyStatus(userDto.getUserDetails().getFamilyStatus());
            editProfileDto.setUserId(userDto.getId());
            return editProfileDto;
        }
        throw new NullPointerException();
    }

    @Override
    public String findAvatarByUsername(String username) {
        UserDto userDto = userProxyService.findUserByUsername(username);
        if (userDto != null) {
            Profile profile = profileRepository.findProfileByUserId(userDto.getId());
            if (profile != null) {
                return profile.getAvatar();
            }
        }
        throw new NullPointerException();
    }

    @Override
    public void createProfile(ProfileDto profileDto) {
        if (profileDto != null) {
            Profile profile = new Profile();
            profile.setId(profileDto.getId());
            profile.setActive(profileDto.isActive());
            profile.setAvatar(profileDto.getAvatar());
            profile.setUserId(profileDto.getUserId());
            profileRepository.save(profile);
        }
    }

    @Override
    public void updateProfile(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException {
        var user = userProxyService.findUserByUserId(id);
        if (user != null) {
            editProfileDto.setUserId(user.getId());
            userProducerService.sendUserForUpdate(topicUserUpdate, editProfileDto);
        } else {
            throw new ProfileServiceException("Profile has not updated");
        }
    }

    @Override
    public void updateAvatarProfile(String username, MultipartFile multipartFile) throws ProfileServiceException, IOException {
        UserDto user = userProxyService.findUserByUsername(username);
        if (user != null) {
            Profile profile = profileRepository.findProfileByUserId(user.getId());
            if (profile != null) {
                var encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);
                profile.setAvatar(encodedAvatar);
                profileRepository.save(profile);
                log.info("Profile with ID: {} has updated avatar", profile.getId());
            } else {
                throw new ProfileServiceException("Avatar for profile has not updated");
            }
        }
    }

    @Override
    public String setDefaultAvatar(String username) throws ProfileServiceException, IOException {
        UserDto user = userProxyService.findUserByUsername(username);
        if (user != null) {
            Profile profile = profileRepository.findProfileByUserId(user.getId());
            if (profile.getUserId().equals(user.getId())) {
                String encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);
                profile.setAvatar(encodedDefaultAvatar);
                log.info("Profile with ID: {} has deleted current avatar and set default avatar", profile.getId());
                return encodedDefaultAvatar;
            } else {
                throw new ProfileServiceException("Could not set default avatar for profile");
            }
        }
        throw new NullPointerException();
    }

    @Override
    public boolean changeStatus(String username, boolean isActive) {
        UserDto user = userProxyService.findUserByUsername(username);
        if (user != null) {
            Profile profile = profileRepository.findProfileByUserId(user.getId());
            if (profile != null) {
                profile.setActive(isActive);
                profileRepository.save(profile);
                log.info("Profile with ID: {} has changed status", profile.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto) {
        var user = userProxyService.findUserByUsername(changePasswordDto.getUsername());
        return user != null && passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword());
    }

    @Override
    public void changePassword(String username, String newPassword) {
        var user = userProxyService.findUserByUsername(username);
        if (user != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            userProxyService.changePassword(user.getUsername(), encodedPassword);
        }
    }
}