package local.socialnetwork.userservice.dto.user;

import local.socialnetwork.userservice.dto.profile.ProfileDto;

import java.util.UUID;

public record UserProfileDto(ProfileDto profile, UUID authUserId) {}
