package local.socialnetwork.profileservice.model.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    UUID id;
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthDay;
    String familyStatus;
}
