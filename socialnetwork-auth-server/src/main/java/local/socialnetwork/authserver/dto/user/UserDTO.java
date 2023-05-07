package local.socialnetwork.authserver.dto.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthday;
    String familyStatus;
    UUID authUserId;
}
