package local.socialnetwork.profiles.service;

import local.socialnetwork.auth.entity.AuthUser;

import local.socialnetwork.profiles.dto.AuthorProfile;
import local.socialnetwork.profiles.dto.NewProfileDetails;

import local.socialnetwork.profiles.dto.http.request.UpdateProfileRequestDto;

import local.socialnetwork.profiles.entity.UserProfile;

import local.socialnetwork.shared.dto.response.AuthorSummary;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.Collection;

/**
 * Service interface for user profile operations.
 * <p>
 * The {@code AuthorSummary}/{@code AuthorProfile}-returning methods below are this feature's
 * public surface for other features (auth, posts, comments): they expose only the public,
 * read-only shape those features need, without leaking {@link UserProfile} or
 * {@code UserProfileRepository} across the module boundary.
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
     * Builds a new, unsaved profile for {@code authUser} from the given registration details.
     */
    UserProfile createProfile(AuthUser authUser, NewProfileDetails details);

    /**
     * Returns whether a profile with the given username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Returns the username of the profile owned by {@code authUserId}, if one exists.
     */
    Optional<String> findUsernameByAuthUserId(UUID authUserId);

    /**
     * Returns the public author summary (with a freshly presigned avatar URL) for {@code authUserId}.
     *
     * @throws local.socialnetwork.shared.exception.UserNotFoundException if no profile exists for the given user
     */
    AuthorSummary getAuthorSummary(UUID authUserId);

    /**
     * Batch variant of {@link #getAuthorSummary(UUID)} for rendering feeds/lists efficiently.
     * Auth-user IDs without a matching profile are simply omitted from the result.
     */
    Map<UUID, AuthorSummary> getAuthorSummaries(Collection<UUID> authUserIds);

    /**
     * Resolves the owning auth-user ID and public author summary for a profile by username.
     */
    Optional<AuthorProfile> findAuthorByUsername(String username);

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
