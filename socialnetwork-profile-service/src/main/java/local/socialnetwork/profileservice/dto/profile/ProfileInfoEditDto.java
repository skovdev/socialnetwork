package local.socialnetwork.profileservice.dto.profile;

import local.socialnetwork.profileservice.type.FamilyStatus;

import java.util.UUID;

public record ProfileInfoEditDto(
        UUID id,
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        FamilyStatus familyStatus
) {}