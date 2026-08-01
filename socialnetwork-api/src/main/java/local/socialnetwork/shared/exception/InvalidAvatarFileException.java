package local.socialnetwork.shared.exception;

/**
 * Thrown when an uploaded avatar file fails content or format validation.
 */
public class InvalidAvatarFileException extends RuntimeException {

    public InvalidAvatarFileException(String message) {
        super(message);
    }
}