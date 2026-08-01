package local.socialnetwork.shared.exception;

/**
 * Thrown when a caller attempts to modify or delete a comment they do not own.
 */
public class CommentAccessDeniedException extends RuntimeException {

    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
