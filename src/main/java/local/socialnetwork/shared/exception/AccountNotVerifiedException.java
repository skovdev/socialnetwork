package local.socialnetwork.shared.exception;

/**
 * Thrown when a user attempts to log in but their account has not yet been email-verified.
 */
public class AccountNotVerifiedException extends RuntimeException {

    /**
     * @param message describes the unverified account situation
     */
    public AccountNotVerifiedException(String message) {
        super(message);
    }

}
