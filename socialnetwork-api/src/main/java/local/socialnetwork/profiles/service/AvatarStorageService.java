package local.socialnetwork.profiles.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Contract for avatar object storage operations, backed by Amazon S3.
 */
public interface AvatarStorageService {

    /**
     * Validates and uploads an avatar image, returning the storage key it was saved under.
     *
     * @param authUserId the owning user's auth ID (used to namespace the storage key)
     * @param file       the multipart image file to upload
     * @return the storage key of the uploaded file
     * @throws local.socialnetwork.shared.exception.InvalidAvatarFileException if the file fails validation or the upload fails
     */
    String upload(UUID authUserId, MultipartFile file);

    /**
     * Deletes an avatar from storage using its key.
     *
     * @param key the storage key of the avatar to delete
     */
    void delete(String key);

    /**
     * Generates a time-limited, publicly reachable URL for the given storage key.
     *
     * @param key the storage key, or {@code null} if no avatar is set
     * @return a presigned URL, or {@code null} if {@code key} is {@code null}
     */
    String presign(String key);
}