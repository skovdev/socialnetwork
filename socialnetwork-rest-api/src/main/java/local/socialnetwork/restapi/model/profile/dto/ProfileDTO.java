package local.socialnetwork.restapi.model.profile.dto;

import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {
    boolean isActive;
    String avatar;
    UUID userId;
}
