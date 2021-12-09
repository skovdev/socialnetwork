package local.socialnetwork.userservice.exception;

import java.io.Serial;

public class UserAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1619603745405485439L;

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
