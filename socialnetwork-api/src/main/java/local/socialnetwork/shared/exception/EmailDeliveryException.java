package local.socialnetwork.shared.exception;

/**
 * Thrown when an outgoing email fails to be delivered.
 */
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
