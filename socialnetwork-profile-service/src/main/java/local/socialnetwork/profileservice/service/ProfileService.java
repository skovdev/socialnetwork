package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.dto.profile.ProfileInfoDto;
import local.socialnetwork.profileservice.dto.profile.ProfileInfoEditDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileService {
    List<ProfileDto> findAll();
    Optional<ProfileDto> findById(UUID profileId);
    Optional<ProfileInfoDto> findProfileInfoByProfileIdAndUserId(UUID profileId, UUID userId);
    Optional<ProfileInfoEditDto> findProfileInfoToEditByProfileIdAndUserId(UUID profileId, UUID userId);
    Optional<String> findAvatarById(UUID profileId);
    Optional<UUID> findProfileIdByUserId(UUID userId);
    void save(ProfileDto profileDto);
    void updateAvatarProfile(UUID profileId, MultipartFile multipartFile) throws ProfileServiceException, IOException;
    String setDefaultAvatar(UUID profileId) throws IOException;
    boolean changeStatus(UUID profileId, boolean isActive);
}
