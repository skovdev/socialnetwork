package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import java.util.concurrent.CompletableFuture;

public interface ProfileCommandService {
    CompletableFuture<String> createProfile(ProfileDto profileDto);
}
