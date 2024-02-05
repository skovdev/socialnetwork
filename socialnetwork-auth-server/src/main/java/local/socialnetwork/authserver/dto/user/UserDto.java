package local.socialnetwork.authserver.dto.user;

import java.util.UUID;

public record UserDto(
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        String familyStatus,
        UUID authUserId
) {}
