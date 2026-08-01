package local.socialnetwork.shared.exception;

/**
 * Thrown when a JWT fails validation and cannot be used to authenticate a request.
 */
public class InvalidJwtAuthenticationException extends RuntimeException {

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
