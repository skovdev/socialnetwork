package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.dto.profile.EditProfileDto;

import local.socialnetwork.profileservice.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.entity.profile.Profile;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<Profile> findAll();
    ProfileDto findProfileByUsername(String username);
    List<ProfileDto> findProfilesByFirstName(String firstName);
    Profile findById(UUID id);
    ProfileDto findProfileByUserId(UUID userId);
    void update(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException;
    void delete(UUID id);
    String setDefaultAvatar(String username) throws IOException, ProfileServiceException;
    void updateAvatarProfile(String username, MultipartFile multipartFile) throws IOException, ProfileServiceException;
    boolean changeStatus(String username, boolean isActive);
    EditProfileDto editProfileByUsername(String username) throws ProfileServiceException;
}