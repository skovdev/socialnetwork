package local.socialnetwork.profileservice.service.impl;

import local.socialnetwork.profileservice.model.dto.profile.ChangePasswordDto;
import local.socialnetwork.profileservice.model.dto.profile.EditProfileDto;
import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.service.ProfileService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    @Override
    public List<ProfileDto> findAll() {
        return null;
    }

    @Override
    public ProfileDto findByUserId(UUID id) {
        return null;
    }

    @Override
    public ProfileDto findByUsername(String username) {
        return null;
    }

    @Override
    public EditProfileDto findEditProfileByUsername(String username) {
        return null;
    }

    @Override
    public String findAvatarByUsername(String username) {
        return null;
    }

    @Override
    public void createProfile(ProfileDto profileDto) {

    }

    @Override
    public void updateProfile(UUID id, EditProfileDto editProfileDto) {

    }

    @Override
    public void updateAvatarProfile(String username, MultipartFile multipartFile) {

    }

    @Override
    public String setDefaultAvatar(String username) {
        return null;
    }

    @Override
    public boolean changeStatus(String username, boolean isActive) {
        return false;
    }

    @Override
    public boolean checkIfValidOldPassword(ChangePasswordDto changePasswordDto) {
        return false;
    }

    @Override
    public void changePassword(String username, String newPassword) {

    }
}