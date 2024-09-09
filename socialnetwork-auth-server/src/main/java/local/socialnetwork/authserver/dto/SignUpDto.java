package local.socialnetwork.authserver.dto;

import local.socialnetwork.authserver.type.FamilyStatus;

public record SignUpDto(
        String username,
        String password,
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        FamilyStatus familyStatus
) {}