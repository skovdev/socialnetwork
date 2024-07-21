package local.socialnetwork.userservice.dto.user;

public record UserRequestUpdateDto(
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        String familyStatus) {
}