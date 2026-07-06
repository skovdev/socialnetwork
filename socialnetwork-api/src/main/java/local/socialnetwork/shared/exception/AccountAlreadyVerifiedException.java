package local.socialnetwork.shared.exception;

/**
 * Thrown when a resend-verification request is made for an account that is already active.
 */
public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
