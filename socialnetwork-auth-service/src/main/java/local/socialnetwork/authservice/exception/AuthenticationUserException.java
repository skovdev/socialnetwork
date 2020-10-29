package local.socialnetwork.authservice.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationUserException extends AuthenticationException {

    private static final long serialVersionUID = -1243226497368681892L;

    public AuthenticationUserException(String msg) {
        super(msg);
    }
}
