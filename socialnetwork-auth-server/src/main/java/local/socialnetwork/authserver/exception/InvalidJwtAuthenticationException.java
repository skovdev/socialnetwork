package local.socialnetwork.authserver.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = -1444026248179450950L;

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
