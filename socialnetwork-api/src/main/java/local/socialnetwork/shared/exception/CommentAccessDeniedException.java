package local.socialnetwork.shared.exception;

public class CommentAccessDeniedException extends RuntimeException {

    public CommentAccessDeniedException(String message) {
        super(message);
    }
}
