package local.socialnetwork.userservice.model.dto.profile;

import lombok.Setter;
import lombok.Getter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {
    UUID id;
    boolean isActive;
    String avatar;
    UUID authUserId;
    UUID userId;
}
