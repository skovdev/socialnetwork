package local.socialnetwork.shared.exception;

/**
 * Thrown when a comment references a parent comment that is invalid, e.g. one
 * belonging to a different post.
 */
public class InvalidCommentParentException extends RuntimeException {

    public InvalidCommentParentException(String message) {
        super(message);
    }
}
