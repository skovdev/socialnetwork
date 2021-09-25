package local.socialnetwork.userservice.model.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDto {
    UUID id;
    String authority;
    UserDto user;
}
