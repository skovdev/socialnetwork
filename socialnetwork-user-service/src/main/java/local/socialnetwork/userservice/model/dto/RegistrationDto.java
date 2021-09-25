package local.socialnetwork.userservice.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationDto {
    String firstName;
    String lastName;
    String username;
    String password;
    String country;
    String city;
    String address;
    String phone;
    String birthday;
    String familyStatus;
}