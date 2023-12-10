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

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

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
    public ProfileDto findById(UUID profileId) {
        return profileRepository.findById(profileId)
                .map(profile -> new ProfileDto(profile.getId(), profile.isActive(), profile.getAvatar(), profile.getUserId()
                )).orElse(null);
    }

    @Override
    public ProfileInfoDto findProfileInfoByProfileIdAndUserId(UUID profileId, UUID userId) {
        ProfileInfoDto profileInfoDto = null;
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            profileInfoDto = new ProfileInfoDto();
            profileInfoDto.setId(profile.get().getId());
            profileInfoDto.setActive(profile.get().isActive());
            profileInfoDto.setAvatar(profile.get().getAvatar());
            profileInfoDto.setUserId(profile.get().getUserId());
            UserDto userDto = userClient.findUserByUserId(userId);
            if (userDto != null) {
                profileInfoDto.setFirstName(userDto.getFirstName());
                profileInfoDto.setLastName(userDto.getLastName());
                profileInfoDto.setCountry(userDto.getCountry());
                profileInfoDto.setCity(userDto.getCity());
                profileInfoDto.setAddress(userDto.getAddress());
                profileInfoDto.setPhone(userDto.getPhone());
                profileInfoDto.setBirthDay(userDto.getBirthDay());
                profileInfoDto.setFamilyStatus(userDto.getFamilyStatus());
            }
        }
        return profileInfoDto;
    }

    @Override
    public ProfileInfoEditDto findProfileInfoToEditByProfileIdAndUserId(UUID profileId, UUID userId) {
        ProfileInfoEditDto profileInfoEditDto = null;
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent() && profile.get().getUserId().equals(userId)) {
            UserDto userDto = userClient.findUserByUserId(userId);
            if (userDto != null) {
                profileInfoEditDto = new ProfileInfoEditDto();
                profileInfoEditDto.setFirstName(userDto.getFirstName());
                profileInfoEditDto.setLastName(userDto.getLastName());
                profileInfoEditDto.setCountry(userDto.getCountry());
                profileInfoEditDto.setCity(userDto.getCity());
                profileInfoEditDto.setAddress(userDto.getAddress());
                profileInfoEditDto.setPhone(userDto.getPhone());
                profileInfoEditDto.setBirthDay(userDto.getBirthDay());
                profileInfoEditDto.setFamilyStatus(userDto.getFamilyStatus());
            }
        }
        return profileInfoEditDto;
    }

    @Override
    public String findAvatarById(UUID profileId) {
        return profileRepository.findById(profileId)
                .map(Profile::getAvatar)
                .orElse(null);
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
            log.info("Profile is saved. Profile ID: {}", profile.getId());
        }
    }

    @Override
    public void updateAvatarProfile(UUID profileId, MultipartFile multipartFile) throws IOException {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            var encodedAvatar = resourceUtil.writeResource(multipartFile, pathUploadAvatar);
            profile.get().setAvatar(encodedAvatar);
            profileRepository.save(profile.get());
            log.info("Profile avatar is updated. Profile ID: {}", profile.get().getId());
        }
    }

    @Override
    public void setDefaultAvatar(UUID profileId) throws IOException {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            String encodedDefaultAvatar = resourceUtil.getEncodedResource(pathDefaultAvatar);
            profile.get().setAvatar(encodedDefaultAvatar);
            profileRepository.save(profile.get());
            log.info("Delete the current avatar and set default avatar. Profile ID: {}", profile.get().getId());
        }
    }

    @Override
    public boolean changeStatus(UUID profileId, boolean isActive) {
        Optional<Profile> profile = profileRepository.findById(profileId);
        if (profile.isPresent()) {
            profile.get().setActive(isActive);
            profileRepository.save(profile.get());
            log.info("Status is changed: Profile ID: {}", profile.get().getId());
            return true;
        }
        return false;
    }
}