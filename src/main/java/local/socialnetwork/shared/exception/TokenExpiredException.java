package local.socialnetwork.shared.exception;

/**
 * Thrown when a time-limited token (email verification or refresh) has passed its expiry time.
 */
public class TokenExpiredException extends RuntimeException {

    /**
     * @param message describes the expiry situation
     */
    public TokenExpiredException(String message) {
        super(message);
    }

}
