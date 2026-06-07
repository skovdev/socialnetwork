package local.socialnetwork.shared.exception;

/**
 * Thrown when a user profile lookup returns no matching record.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * @param message describes which user could not be found
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
