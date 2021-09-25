package local.socialnetwork.userservice.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordDto {
    String username;
    String oldPassword;
    String newPassword;
}