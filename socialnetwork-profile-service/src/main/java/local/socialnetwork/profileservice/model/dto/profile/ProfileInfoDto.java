package local.socialnetwork.profileservice.model.dto.profile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileInfoDto {
    boolean isActive;
    String avatar;
    String firstName;
    String lastName;
    String birthDay;
    String country;
    String city;
    String familyStatus;
    String phone;
    String address;
}