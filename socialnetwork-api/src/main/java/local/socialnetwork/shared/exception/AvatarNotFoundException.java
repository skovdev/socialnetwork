package local.socialnetwork.shared.exception;

/**
 * Thrown when a requested user avatar cannot be found.
 */
public class AvatarNotFoundException extends RuntimeException {

    public AvatarNotFoundException(String message) {
        super(message);
    }
}