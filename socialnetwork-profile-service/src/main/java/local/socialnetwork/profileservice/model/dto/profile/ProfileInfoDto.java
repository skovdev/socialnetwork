package local.socialnetwork.profileservice.model.dto.profile;

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
public class ProfileInfoDto {
    UUID id;
    String firstName;
    String lastName;
    String country;
    String city;
    String address;
    String phone;
    String birthDay;
    String familyStatus;
    boolean isActive;
    String avatar;
    UUID userId;
}