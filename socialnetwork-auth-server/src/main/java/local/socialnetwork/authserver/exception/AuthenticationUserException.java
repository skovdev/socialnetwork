package local.socialnetwork.authserver.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class AuthenticationUserException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = -1243226497368681892L;

    public AuthenticationUserException(String msg) {
        super(msg);
    }
}
