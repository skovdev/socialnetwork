package local.socialnetwork.profiles.service;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Uploads a new avatar image for the profile belonging to {@code authUserId}, replacing
     * any existing one, and returns the updated entity.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException     if no profile exists for the given user
     * @throws local.socialnetwork.shared.exception.InvalidAvatarFileException if the file fails validation or the upload fails
     */
    UserProfile updateAvatar(UUID authUserId, MultipartFile file);

    /**
     * Deletes the avatar of the profile belonging to {@code authUserId} from storage and clears the stored key.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException   if no profile exists for the given user
     * @throws local.socialnetwork.shared.exception.AvatarNotFoundException if the profile has no avatar set
     */
    void deleteAvatar(UUID authUserId);
}
