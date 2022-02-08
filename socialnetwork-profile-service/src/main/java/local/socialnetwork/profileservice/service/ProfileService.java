package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<ProfileDto> findAll();
    ProfileDto findByUserId(UUID id);
    ProfileDto findByUsername(String username) ;
    EditProfileDto findEditProfileByUsername(String username);
    String findAvatarByUsername(String username);
    void createProfile(ProfileDto profileDto);
    void updateProfile(UUID id, EditProfileDto editProfileDto);
    void updateAvatarProfile(String username, MultipartFile multipartFile);
    String setDefaultAvatar(String username);
    boolean changeStatus(String username, boolean isActive);
    boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto);
    void changePassword(String username, String newPassword);
}
