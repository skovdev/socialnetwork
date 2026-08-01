package local.socialnetwork.shared.exception;

/**
 * Thrown when a requested comment cannot be found.
 */
public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String message) {
        super(message);
    }
}
