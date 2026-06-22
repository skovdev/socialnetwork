package local.socialnetwork.shared.exception;

/**
 * Thrown when the supplied current password does not match the stored hash.
 */
public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException(String message) {
        super(message);
    }
}
