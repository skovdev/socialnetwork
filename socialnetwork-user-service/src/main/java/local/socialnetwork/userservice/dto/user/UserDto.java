package local.socialnetwork.userservice.dto.user;

import local.socialnetwork.userservice.type.FamilyStatus;

import java.util.UUID;

public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        FamilyStatus familyStatus,
        UUID authUserId
) {}