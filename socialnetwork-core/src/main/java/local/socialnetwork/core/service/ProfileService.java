package local.socialnetwork.core.service;

import local.socialnetwork.core.exception.ProfileServiceException;

import local.socialnetwork.model.dto.EditProfileDto;

import local.socialnetwork.model.profile.Profile;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

public interface ProfileService {
    List<Profile> findAll();
    Profile findProfileByUsername(String username);
    List<Profile> findProfilesByFirstName(String firstName);
    Profile findById(UUID id);
    Profile findProfileByUserId(UUID userId);
    void save(Profile profile);
    void update(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException;
    void delete(UUID id);
    String setDefaultAvatar(String username) throws IOException, ProfileServiceException;
    void updateAvatarProfile(String username, MultipartFile multipartFile) throws IOException, ProfileServiceException;
    boolean changeStatus(String username, boolean isActive);
    EditProfileDto editProfileByUsername(String username) throws ProfileServiceException;
}