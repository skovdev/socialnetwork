package local.socialnetwork.authserver.exception;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6296010907297189435L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
