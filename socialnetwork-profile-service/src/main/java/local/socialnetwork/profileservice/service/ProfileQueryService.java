package local.socialnetwork.profileservice.service;

import local.socialnetwork.profileservice.model.dto.profile.ProfileDto;

import java.util.List;
import java.util.UUID;

import java.util.concurrent.ExecutionException;

public interface ProfileQueryService {
    List<ProfileDto> findAll() throws ExecutionException, InterruptedException;
    ProfileDto findByUserId(UUID id) throws ExecutionException, InterruptedException;
    ProfileDto findByUsername(String username) throws ExecutionException, InterruptedException;
}