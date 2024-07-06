package local.socialnetwork.authserver.dto.authuser;

import java.util.UUID;

public record AuthAdditionalTokenDataDto(
        UUID userId,
        UUID profileId
) {}
