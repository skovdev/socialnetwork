package local.socialnetwork.userservice.dto.user;

import local.socialnetwork.userservice.type.FamilyStatus;

public record UserRequestUpdateDto(
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        FamilyStatus familyStatus) {
}