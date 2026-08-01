package local.socialnetwork.shared.exception;

/**
 * Thrown when registration is attempted with an email address that is already in use.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
