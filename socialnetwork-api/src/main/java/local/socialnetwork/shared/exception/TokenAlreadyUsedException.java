package local.socialnetwork.shared.exception;

/**
 * Thrown when a single-use token has already been consumed.
 */
public class TokenAlreadyUsedException extends RuntimeException {

    public TokenAlreadyUsedException(String message) {
        super(message);
    }
}
