package local.socialnetwork.shared.exception;

/**
 * Thrown when a user attempts to follow themselves.
 */
public class SelfFollowException extends RuntimeException {

    public SelfFollowException(String message) {
        super(message);
    }
}
