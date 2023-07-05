package local.socialnetwork.profileservice.model.dto.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDto {
    UUID id;
    boolean isActive;
    String avatar;
    UUID userId;
}