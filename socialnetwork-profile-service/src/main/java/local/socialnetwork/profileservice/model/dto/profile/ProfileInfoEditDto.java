package local.socialnetwork.profileservice.model.dto.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileInfoEditDto {
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthDay;
    String familyStatus;
}