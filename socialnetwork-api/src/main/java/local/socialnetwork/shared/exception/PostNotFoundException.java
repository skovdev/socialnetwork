package local.socialnetwork.shared.exception;

/**
 * Thrown when a requested post cannot be found.
 */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }
}
