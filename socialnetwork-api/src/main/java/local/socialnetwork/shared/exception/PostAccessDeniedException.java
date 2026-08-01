package local.socialnetwork.shared.exception;

/**
 * Thrown when a caller attempts to modify or delete a post they do not own.
 */
public class PostAccessDeniedException extends RuntimeException {

    public PostAccessDeniedException(String message) {
        super(message);
    }
}
