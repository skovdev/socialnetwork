package local.socialnetwork.userservice.model.dto.profile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditProfileDto {
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthday;
    String familyStatus;
}