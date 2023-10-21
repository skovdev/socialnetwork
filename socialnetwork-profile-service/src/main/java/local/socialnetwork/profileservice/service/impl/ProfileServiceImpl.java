package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

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

    final ProfileRepository profileRepository;
    final ResourceUtil resourceUtil;

    @Override
    public List<ProfileDto> findAll() {
        List<Profile> profile = profileRepository.findAll();
        return profile.stream().map(p -> {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(p.getId());
            profileDto.setActive(p.isActive());
            profileDto.setAvatar(p.getAvatar());
            profileDto.setAuthUserId(p.getAuthUserId());
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
            profileDto.setAuthUserId(profile.getAuthUserId());
            profileDto.setUserId(profile.getUserId());
            return profileDto;
        }
        throw new NullPointerException();
    }

    @Override
    public ProfileDto findByAuthUserId(UUID userId) {
        Profile profile = profileRepository.findProfileByAuthUserId(userId);
        if (profile != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(profile.getId());
            profileDto.setActive(profile.isActive());
            profileDto.setAvatar(profile.getAvatar());
            profileDto.setAuthUserId(profile.getAuthUserId());
            profileDto.setUserId(profile.getUserId());
            return profileDto;
        }
        throw new NullPointerException();
    }

    @Override
    public String findAvatarByAuthUserId(UUID userId) {
        Profile profile = profileRepository.findProfileByAuthUserId(userId);
        if (profile != null) {
            return profile.getAvatar();
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
            profile.setAuthUserId(profileDto.getAuthUserId());
            profile.setUserId(profileDto.getUserId());
            profileRepository.save(profile);
        }
    }

    @Override
    public void updateAvatarProfile(UUID userId, MultipartFile multipartFile) throws ProfileServiceException, IOException {
        Profile profile = profileRepository.findProfileByAuthUserId(userId);
        if (profile != null) {
            var encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);
            profile.setAvatar(encodedAvatar);
            profileRepository.save(profile);
            log.info("Profile with ID: {} has updated avatar", profile.getId());
        } else {
            throw new ProfileServiceException("Avatar for profile has not updated");
        }
    }

    @Override
    public String setDefaultAvatar(UUID userId) throws ProfileServiceException, IOException {
        Profile profile = profileRepository.findProfileByAuthUserId(userId);
        if (profile != null) {
            String encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);
            profile.setAvatar(encodedDefaultAvatar);
            log.info("Profile with ID: {} has deleted current avatar and set default avatar", profile.getId());
            return encodedDefaultAvatar;
        } else {
            throw new ProfileServiceException("Could not set default avatar for profile");
        }
    }

    @Override
    public boolean changeStatus(UUID userId, boolean isActive) {
        Profile profile = profileRepository.findProfileByAuthUserId(userId);
        if (profile != null) {
            profile.setActive(isActive);
            profileRepository.save(profile);
            log.info("Profile with ID: {} has changed status", profile.getId());
            return true;
        }
        return false;
    }
}