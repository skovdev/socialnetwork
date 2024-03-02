package local.socialnetwork.profileservice.dto.profile;

import java.util.UUID;

public record ProfileInfoDto(
        UUID id,
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        String familyStatus,
        boolean isActive,
        String avatar,
        UUID userId
) {}