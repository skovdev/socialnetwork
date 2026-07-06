package local.socialnetwork.shared.exception;

public class TokenAlreadyUsedException extends RuntimeException {

    public TokenAlreadyUsedException(String message) {
        super(message);
    }
}
