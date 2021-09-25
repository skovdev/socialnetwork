package local.socialnetwork.userservice.model.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    UUID id;
    String firstName;
    String lastName;
    String username;
    String password;
    UserDetailsDto userDetails;
    List<RoleDto> roles;
}