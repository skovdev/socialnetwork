package local.socialnetwork.userservice.model.dto.profile;

import local.socialnetwork.userservice.model.dto.user.UserDto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1780142791519942009L;

    UUID id;
    String avatar;
    boolean isActive;
    UserDto user;

}