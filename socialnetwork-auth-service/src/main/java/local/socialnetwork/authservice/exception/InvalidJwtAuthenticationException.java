package local.socialnetwork.authservice.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = -1444026248179450950L;

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
