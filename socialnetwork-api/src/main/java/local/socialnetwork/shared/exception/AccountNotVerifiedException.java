package local.socialnetwork.shared.exception;

/**
 * Thrown when an operation requires a verified account but the account has not
 * completed email verification.
 */
public class AccountNotVerifiedException extends RuntimeException {

    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
