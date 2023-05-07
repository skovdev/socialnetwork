package local.socialnetwork.authserver.dto.profile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {
    UUID id;
    boolean isActive;
    String avatar;
    UUID authUserId;}
