package local.socialnetwork.shared.exception;

/**
 * Thrown when a requested token cannot be found.
 */
public class TokenNotFoundException extends RuntimeException {

    public TokenNotFoundException(String message) {
        super(message);
    }
}
