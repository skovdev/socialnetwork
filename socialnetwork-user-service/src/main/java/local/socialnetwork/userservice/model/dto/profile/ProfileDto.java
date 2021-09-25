package local.socialnetwork.userservice.model.dto.profile;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDto {
    UUID id;
    String avatar;
    boolean isActive;
    UserDto user;
}