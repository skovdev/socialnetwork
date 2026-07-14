package local.socialnetwork.shared.exception;

public class PostAccessDeniedException extends RuntimeException {

    public PostAccessDeniedException(String message) {
        super(message);
    }
}
