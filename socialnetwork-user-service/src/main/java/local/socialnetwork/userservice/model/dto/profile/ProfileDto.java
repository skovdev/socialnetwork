package local.socialnetwork.userservice.model.dto.profile;

import java.util.UUID;

public record ProfileDto (
        UUID id,
        boolean isActive,
        String avatar,
        UUID userId
) {}
