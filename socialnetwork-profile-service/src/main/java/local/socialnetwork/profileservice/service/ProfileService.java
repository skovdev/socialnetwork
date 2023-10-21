package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<ProfileDto> findAll();
    ProfileDto findByProfileId(UUID profileId);
    ProfileDto findByAuthUserId(UUID authUserId);
    String findAvatarByAuthUserId(UUID authUserId);
    void createProfile(ProfileDto profileDto);
    void updateAvatarProfile(UUID userId, MultipartFile multipartFile) throws ProfileServiceException, IOException;
    String setDefaultAvatar(UUID userId) throws ProfileServiceException, IOException;
    boolean changeStatus(UUID userId, boolean isActive);
}
