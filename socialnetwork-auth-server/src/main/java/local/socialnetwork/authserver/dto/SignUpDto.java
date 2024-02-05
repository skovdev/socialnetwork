package local.socialnetwork.authserver.dto;

public record SignUpDto(
        String username,
        String password,
        String firstName,
        String lastName,
        String country,
        String city,
        String address,
        String phone,
        String birthDay,
        String familyStatus
) {}