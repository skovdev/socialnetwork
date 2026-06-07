package local.socialnetwork.shared.exception;

/**
 * Thrown when a token lookup (verification or refresh) returns no matching record.
 */
public class TokenNotFoundException extends RuntimeException {

    /**
     * @param message describes which token could not be found
     */
    public TokenNotFoundException(String message) {
        super(message);
    }

}
