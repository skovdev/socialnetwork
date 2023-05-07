package local.socialnetwork.authserver.exception;

import java.io.Serial;

public class AuthenticationUserNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6296010907297189435L;

    public AuthenticationUserNotFoundException(String message) {
        super(message);
    }
}
