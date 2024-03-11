package local.socialnetwork.authserver.dto.authuser;

import java.util.List;
import java.util.UUID;

public record AuthUserDto(UUID id, String username, String password, List<AuthRoleDto> authRoles) {}