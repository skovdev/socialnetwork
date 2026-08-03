package local.socialnetwork.profiles.dto;

import java.time.LocalDate;

/**
 * Details needed to create a profile for a newly registered user.
 */
public record NewProfileDetails(
        String firstName,
        String lastName,
        String username,
        LocalDate birthDate,
        String phoneNumber) {
}
