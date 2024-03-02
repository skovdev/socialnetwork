package local.socialnetwork.profileservice.dto.user;

import local.socialnetwork.profileservice.dto.profile.ProfileDto;

import java.util.UUID;

public record UserProfileDto(ProfileDto profile, UUID authUserId) {}
