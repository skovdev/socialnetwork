package local.socialnetwork.shared.exception;

/**
 * Thrown when a token is presented after its expiration time.
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
