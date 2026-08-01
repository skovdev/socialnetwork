package local.socialnetwork.shared.exception;

/**
 * Thrown when registration is attempted with a username that is already in use.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
