package local.socialnetwork.authserver.dto;

import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpDTO {
    String username;
    String password;
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthDay;
    String familyStatus;
}