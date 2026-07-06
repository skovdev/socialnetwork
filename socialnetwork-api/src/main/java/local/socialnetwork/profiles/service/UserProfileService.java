package local.socialnetwork.profiles.service;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import java.util.UUID;
import java.util.Optional;

/**
 * Service interface for user profile operations.
 */
public interface UserProfileService {

    /**
     * Persists a new or existing profile.
     */
    void save(UserProfile userProfile);

    /**
     * Finds a profile by username.
     */
    Optional<UserProfile> findByUsername(String username);

    /**
     * Finds a profile by the owning auth-user ID.
     */
    Optional<UserProfile> findByAuthUserId(UUID authUserId);

    /**
     * Updates the profile belonging to {@code authUserId} with the supplied fields
     * and returns the updated entity.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no profile exists for the given user
     */
    UserProfile update(UUID authUserId, UpdateProfileRequestDto request);
}
