package local.socialnetwork.profileservice.dto.profile;

public record ProfileInfoEditDto(
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        String familyStatus
) {}