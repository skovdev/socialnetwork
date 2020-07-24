package local.socialnetwork.api.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationUserException extends AuthenticationException {

    private static final long serialVersionUID = -511833929392312685L;

    public AuthenticationUserException(String msg) {
        super(msg);
    }
}