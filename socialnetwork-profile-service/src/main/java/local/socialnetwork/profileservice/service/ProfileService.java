package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileInfoDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<ProfileDto> findAll();
    ProfileDto findByProfileId(UUID profileId);
    ProfileDto findByUserId(UUID id);
    ProfileInfoDto findByUsername(String username) ;
    EditProfileDto findEditProfileByUsername(String username);
    String findAvatarByUsername(String username);
    void createProfile(ProfileDto profileDto);
    void updateProfile(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException;
    void updateAvatarProfile(String username, MultipartFile multipartFile) throws ProfileServiceException, IOException;
    String setDefaultAvatar(String username) throws ProfileServiceException, IOException;
    boolean changeStatus(String username, boolean isActive);
    boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto);
    void changePassword(String username, String newPassword);
}
