package local.socialnetwork.profileservice.service;

import local.socialnetwork.kafka.model.dto.profile.EditProfileDto;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import local.socialnetwork.profileservice.model.entity.profile.Profile;

import local.socialnetwork.profileservice.exception.ProfileServiceException;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface ProfileService {
    List<ProfileDto> findAll();
    Profile findProfileByUsername(String username) throws ExecutionException, InterruptedException;
    List<ProfileDto> findProfilesByFirstName(String firstName);
    Profile findById(UUID id);
    Profile findProfileByUserId(UUID userId);
    void save(Profile profile);
    void update(UUID id, EditProfileDto editProfileDto) throws ProfileServiceException;
    void delete(UUID id);
    String setDefaultAvatar(String username) throws ProfileServiceException, IOException;
    void updateAvatarProfile(String username, MultipartFile multipartFile) throws IOException, ProfileServiceException;
    boolean changeStatus(String username, boolean isActive);
    EditProfileDto editProfileByUsername(String username) throws ProfileServiceException;
}