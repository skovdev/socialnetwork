package local.socialnetwork.shared.exception;

/**
 * Thrown when a one-time token (e.g. email verification) is presented after it has already been consumed.
 */
public class TokenAlreadyUsedException extends RuntimeException {

    /**
     * @param message describes the token re-use situation
     */
    public TokenAlreadyUsedException(String message) {
        super(message);
    }

}
