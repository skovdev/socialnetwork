package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.client.UserClient;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoEditDto;

import local.socialnetwork.profileservice.model.dto.user.UserDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.repository.ProfileRepository;

import local.socialnetwork.profileservice.service.ProfileService;

import local.socialnetwork.profileservice.util.ResourceUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
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

    final ProfileRepository profileRepository;
    final ResourceUtil resourceUtil;
    final UserClient userClient;

    @Override
    public List<ProfileDto> findAll() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(profile -> new ProfileDto(
                        profile.getId(),
                        profile.isActive(),
                        profile.getAvatar(),
                        profile.getUserId())
                ).collect(Collectors.toList());
    }

    @Override
    public Optional<ProfileDto> findById(UUID profileId) {
        return profileRepository.findById(profileId)
                .map(profile -> new ProfileDto(
                        profile.getId(),
                        profile.isActive(),
                        profile.getAvatar(),
                        profile.getUserId())
                );
    }

    @Override
    public Optional<ProfileInfoDto> findProfileInfoByProfileIdAndUserId(UUID profileId, UUID userId) {
        return profileRepository.findById(profileId)
                .map(profile -> {
                    UserDto userDto = userClient.findUserByUserId(userId);
                    if (userDto == null) {
                        return null;
                    }
                    return new ProfileInfoDto(
                            profile.getId(),
                            userDto.firstName(),
                            userDto.lastName(),
                            userDto.country(),
                            userDto.city(),
                            userDto.address(),
                            userDto.phone(),
                            userDto.birthDay(),
                            userDto.familyStatus(),
                            profile.isActive(),
                            profile.getAvatar(),
                            profile.getUserId());
                });
    }

    @Override
    public Optional<ProfileInfoEditDto> findProfileInfoToEditByProfileIdAndUserId(UUID profileId, UUID userId) {
        return profileRepository.findById(profileId)
                .filter(profile -> profile.getUserId().equals(userId))
                .map(profile -> {
                    UserDto userDto = userClient.findUserByUserId(userId);
                    if (userDto == null) {
                        return null;
                    }
                    return new ProfileInfoEditDto(
                            userDto.firstName(),
                            userDto.lastName(),
                            userDto.country(),
                            userDto.city(),
                            userDto.address(),
                            userDto.phone(),
                            userDto.birthDay(),
                            userDto.familyStatus());
                });
    }

    @Override
    public Optional<String> findAvatarById(UUID profileId) {
        return profileRepository.findById(profileId).map(Profile::getAvatar);
    }

    @Override
    public void createProfile(ProfileDto profileDto) {
        if (profileDto != null) {
            Profile profile = new Profile();
            profile.setActive(profileDto.isActive());
            profile.setAvatar(profileDto.avatar());
            profile.setUserId(profileDto.userId());
            profileRepository.save(profile);
            log.info("Profile is saved. Profile ID: {}", profile.getId());
        }
    }

    @Override
    public void updateAvatarProfile(UUID profileId, MultipartFile multipartFile) {
        profileRepository.findById(profileId)
                .ifPresent(profile -> {
                    var encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);
                    profile.setAvatar(encodedAvatar);
                    profileRepository.save(profile);
                    log.info("Profile avatar is updated. Profile ID: {}", profile.getId());
                });
    }

    @Override
    public void setDefaultAvatar(UUID profileId) {
        profileRepository.findById(profileId)
                .ifPresent(profile -> {
                    String encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);
                    profile.setAvatar(encodedDefaultAvatar);
                    profileRepository.save(profile);
                    log.info("Delete the current avatar and set default avatar. Profile ID: {}", profile.getId());
                });
    }

    @Override
    public boolean changeStatus(UUID profileId, boolean isActive) {
       return profileRepository.findById(profileId)
               .map(profile -> {
                   profile.setActive(isActive);
                   profileRepository.save(profile);
                   log.info("Status is changed: Profile ID: {}", profile.getId());
                   return true;
               }).orElse(false);
    }
}